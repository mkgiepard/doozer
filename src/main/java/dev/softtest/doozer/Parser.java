package dev.softtest.doozer;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Parses doozer script file into a list of DoozerActions. */
public class Parser {
    protected static final Logger LOG = LogManager.getLogger();

    private Context ctx;
    private String filePath;
    private Charset charset = StandardCharsets.UTF_8;
    private List<DoozerAction> actions = new ArrayList<DoozerAction>();

    public Parser(Context ctx, String filePath) {
        this.ctx = ctx;
        this.filePath = filePath;
    }

    public List<DoozerAction> parseScriptIntoActions() throws Exception {
        Path path = FileSystems.getDefault().getPath(this.filePath).toAbsolutePath();
        if (!Files.exists(path)) {
            throw new Exception("File '" + path.toString() + "' does not exist.");
        }

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String sourceFileName = filePath.substring(filePath.lastIndexOf("/") + 1);
            String line = null;
            int lineCounter = 0;
            while ((line = reader.readLine()) != null) {
                lineCounter++;
                if (line.length() == 0) {
                    LOG.info("parse: " + sourceFileName + " > " + Integer.toString(lineCounter) + ": EMPTY LINE");
                    continue;
                }
                if (line.trim().startsWith("//")) {
                    LOG.info("parse: " + sourceFileName + " > " + Integer.toString(lineCounter) + ": COMMENT");
                    continue;
                }
                DoozerAction action = parseAction(lineCounter, line);
                action.setSourceFileName(sourceFileName);
                String logMsg = "parse: " + sourceFileName + " > "
                    + Integer.toString(lineCounter) + ": " + action.getActionName();
                LOG.info(logMsg);
                if (action.getActionName().equals("import")) {
                    Parser p = new Parser(ctx, action.getOption("default"));
                    actions.addAll(p.parseScriptIntoActions());
                } else {
                    actions.add(action);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("Exception: " + e);
            throw e;
        }
        return actions;
    }

    public DoozerAction parseAction(int lineNumber, String line) throws Exception {
        DoozerAction action;

        if (!validate(line)) {
            throw new ParserException("Could not parse the action, in: " + Integer.toString(lineNumber) + ": " + line);
        }

        Map<String, String> tokens = useNamedParams(line) ? tokenizeWithNamedParams(line) : tokenize(line);

        String actionName = null;
        String selector = null;
        Map<String, String> options = new HashMap<>();
        Boolean isOptional = false;

        String firstParam = "selector";
        String secondParam = "args";

        actionName = tokens.get("action");
        if (actionName.equals("set")) {
            firstParam = "name";
            secondParam = "value";
        }
        selector = tokens.get(firstParam);
        OptionParser oParser = new OptionParser(tokens.getOrDefault(secondParam, ""));
        options = oParser.parse();      

        if (actionName.endsWith("?")) {
            isOptional = true;
            actionName = actionName.substring(0, actionName.length()-1);
        }

        try {
            action = createActionInstance(ctx, lineNumber, actionName, line);
            action.setSelector(selector);
            action.setOptions(options);
            action.setIsOptional(isOptional);
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
            LOG.error("Exception: " + e);
            throw e;
        }
        return action;
    }
    
    public boolean validate(String line) throws ParserException {
        Map<String, String> tokens = tokenize(line);

        // The number of escaped quote must be even
        String lineCopy = line.trim().replace("\\" + '"', "<ESCAPED>");
        if (count(lineCopy, "<ESCAPED>") % 2 != 0) {
            throw new ParserException("Missing escaped quote - '\\" + "\"' in: '" + line + "'");
        }

        // The last character after trimming has to be a quotation mark (excl. lines with action only)
        if (tokens.size() > 1 && !line.trim().endsWith("\"")) {
            throw new ParserException("Missing quote - '\"' in: '" + line + "'");
        }

        // The number of tokens cannot be higher than 3
        if (tokens.size() > 3) {
            throw new ParserException("Too many action parameters in: '" + line + "'");
        }

        // The last character of a called variable should be a closing curly bracket
        if (tokens.size() > 1) {
            for (String k : tokens.keySet()) {
                if (tokens.get(k).contains("${")) {
                    if (tokens.get(k).indexOf("${") > tokens.get(k).indexOf("}")) {
                        throw new ParserException("Wrong variable syntax in: '" + line + "'");
                    }
                }
            }
        }

        return true;
    }

    public boolean useNamedParams(String s) {
        if (s.trim().startsWith("set")) {
            return s.contains("name:");
        }
        return s.contains("selector:") || s.contains("args:");
    }

    public Map<String, String> tokenize(String s) {
        String[] encoded = s.replace("\\" + '"', "<QUOTE>").split("\"");
        // [0] - action
        // [1] - selector | empty
        // [2] - empty
        // [3] - option | empty
        // [4+] - not supported
        Map<String, String> result = new HashMap<String, String>();
        String[] KEYS = {"action", "selector", "", "args"};
        for (int i = 0; i < encoded.length; i++) {
            if (i == 2) continue;
            if (i < 4) {
                result.put(KEYS[i], encoded[i].replace("<QUOTE>", "\"").trim());
            }
            else {
                result.put("unknown_" + Integer.toString(i), encoded[i].replace("<QUOTE>", "\"").trim());
            }
        }
        return result;
    }

    public Map<String, String> tokenizeWithNamedParams(String s) throws ParserException {
        // Syntax rules:
        // 
        // actionName is always first followed by:
        // - for set:
        //   - name:"xyz" value:"xyz"
        //   example of minimum string: set name:"a" value:"b"
        // - for others:
        //   - none
        //   - selector:"xyz"
        //   - args:"xyz"
        //   - selector:"xyz" args:"xyz"

        Map<String, String> m = new HashMap<String, String>();
        if (s.trim().startsWith("set")) {
            String name = getNamedParamValue(s.split("name:"), "value:");
            String value = getNamedParamValue(s.split("value:"));
            if (name == null || value == null) {
                throw new ParserException("Missing parameters in: '" + s + "'");
            }
            m.put("action", "set");
            m.put("name", name);
            m.put("value", value);
        } else {
            String args = getNamedParamValue(s.split("args:"));
            if (args != null) m.put("args", args.replace("\\" + '"', "\""));

            String sel;
            if (args != null) {
                sel = getNamedParamValue(s.split("selector:"), "args:");
            } else {
                sel = getNamedParamValue(s.split("selector:"));
            }
            if (sel != null) m.put("selector", sel.replace("\\" + '"', "\""));

            String action;
            if (sel != null) {
                action = s.substring(0, s.indexOf("selector:")).trim();
            } else if (args != null) {
                action = s.substring(0, s.indexOf("args:")).trim();
            } else {
                action = s.trim();
            }
            m.put("action", action);
        }
        return m;
    }

    private String getNamedParamValue(String[] arr) {
        return getNamedParamValue(arr, null);
    }

    private String getNamedParamValue(String[] arr, String follower) {
        if (arr.length == 2) {
            int endIndex = follower == null ? arr[1].length() : arr[1].indexOf(follower);
            String s = arr[1].substring(0, endIndex).trim();
            return s.substring(1, s.length() - 1);
        }
        return null;
    }

    private int count(String line, String elem) {
        String[] s = line.split(elem);
        if (line.startsWith(elem) && line.endsWith(elem)) return s.length + 1;
        if (line.startsWith(elem) || line.endsWith(elem)) return s.length;
        return s.length - 1;
    }

    private DoozerAction createActionInstance(Context ctx, Integer lineNumber, String actionName, String originalAction)
            throws ParserException {
        String actionClassPrefix = "dev.softtest.doozer.actions.";
        String actionClassName = actionName.substring(0, 1).toUpperCase() + actionName.substring(1);
        try {
            return (DoozerAction) Class.forName(actionClassPrefix + actionClassName)
                .getConstructor(Context.class, Integer.class, String.class, String.class)
                .newInstance(ctx, lineNumber, actionName, originalAction);
        } catch (Exception e) {
            throw new ParserException("Action '" + actionName + "' is not supported, in: '" + originalAction + "'");
        }
    }

    public class ParserException extends Exception {
        public ParserException(String errorMessage) {
            super(errorMessage);
            LOG.error(errorMessage);
        }
    }
}
