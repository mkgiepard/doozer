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
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

public class Parser {
    protected static final Logger logger = LogManager.getLogger();

    private Context ctx;
    private String filePath;
    private Charset charset = StandardCharsets.UTF_8;
    private final String splitter = " \"";
    private List<DoozerAction> actions = new ArrayList<DoozerAction>();
    private WebDriver driver;

    public Parser(Context ctx, String filePath, WebDriver driver) {
        this.ctx = ctx;
        this.filePath = filePath;
        this.driver = driver;
    }

    public List<DoozerAction> parse() throws Exception {
        Path path = FileSystems.getDefault().getPath(this.filePath).toAbsolutePath();

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = null;
            int lineCounter = 1;
            while ((line = reader.readLine()) != null) {
                if (line.length() == 0) {
                    logger.info("parse: " + Integer.toString(lineCounter++) + ":\t EMPTY LINE");
                    continue;
                }
                if (line.trim().startsWith("//")) {
                    logger.info("parse: " + Integer.toString(lineCounter++) + ":\t COMMENT");
                    continue;
                }
                DoozerAction action = parseAction(lineCounter++, line);
                logger.info("parse: " + Integer.toString(lineCounter) + ":\t " + action.getActionName());
                actions.add(action);
            }
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
            throw e;
        }
        return actions;
    }

    public DoozerAction parseAction(int lineNumber, String line) throws Exception {
        DoozerAction action;

        if (!validate(line)) {
            throw new ParserException("Could not parse the action, in: " + Integer.toString(lineNumber) + ": " + line);
        }

        String[] tokens = tokenize(line);

        String name = null;
        String selector = null;
        Map<String, String> options = new HashMap<>();
        Boolean isOptional = false;
        if (tokens.length == 3) {
            name = tokens[0];
            selector = tokens[1];
            OptionParser oParser = new OptionParser(tokens[2]);
            options = oParser.parse();
        } else if (tokens.length == 2) {
            name = tokens[0];
            selector = tokens[1];
        } else if (tokens.length == 1) {
            name = tokens[0];
        }
        
        if (name.endsWith("?")) {
            isOptional = true;
            name = name.substring(0, name.length()-1);
        }

        try {
            action = createActionInstance(ctx, lineNumber, name, line);
            action.setSelector(selector);
            action.setDriver(driver);
            action.setOptions(options);
            action.setIsOptional(isOptional);
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
            throw e;
        }
        return action;
    }

    public boolean validate(String line) throws ParserException {
        String[] tokens = tokenize(line);

        // The number of escaped quote must be even
        String lineCopy = line.trim().replace("\\" + '"', "<ESCAPED>");
        if (count(lineCopy, "<ESCAPED>") % 2 != 0) {
            throw new ParserException("Missing escaped quote - '\\" + "\"' in: '" + line + "'");
        }

        // The last character after trimming has to be a quotation mark (excl. lines with action only)
        if (tokens.length > 1 && !line.trim().endsWith("\"")) {
            throw new ParserException("Missing quote - '\"' in: '" + line + "'");
        }

        // The number of tokens cannot be higher than 3
        if (tokens.length > 3) {
            throw new ParserException("Too many action parameters in: '" + line + "'");
        }

        // The last character of a called variable should be a closing curly bracket
        if (tokens.length > 1) {
            for (int i = 1; i < tokens.length; i++) {
                if (tokens[i].contains("${")) {
                    if (tokens[i].indexOf("${") > tokens[i].indexOf("}")) {
                        throw new ParserException("Wrong variable syntax in: '" + line + "'");
                    }
                }
            }
        }

        return true;
    }

    public String[] tokenize(String s) {
        String[] tmp = s.replace("\\" + '"', "<QUOTE>").split("\"");
        // [0] - action
        // [1] - selector | empty
        // [2] - empty
        // [3] - option | empty
        // [4+] - not supported
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < tmp.length; i++) {
            if (i == 2) continue;
            result.add(tmp[i].replace("<QUOTE>", "\"").trim());
        }
        // [0] - action
        // [1] - selector | empty
        // [2] - option | empty
        // [3+] - not supported
        return (String[]) result.toArray(new String[0]);
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
        }
    }
}
