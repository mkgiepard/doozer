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

import org.openqa.selenium.WebDriver;

public class Parser {
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
                    System.out.println(Integer.toString(lineCounter++) + ": ...empty line");
                    continue;
                }
                DoozerAction action = parseAction(lineCounter++, line);
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
        String[] splitLine = line.split(splitter);

        String name = null;
        String selector = null;
        Map<String, String> options = new HashMap<>();
        Boolean isOptional = false;
        if (splitLine.length == 3) {
            name = splitLine[0].replaceAll("\"", "").trim();
            // TODO: make it simpler
            // 1. Trim white spaces
            // 2. Remove trailing '"'
            // 3. Replace escaped quote '\"' with '"' 
            selector = splitLine[1].trim();
            selector = selector.substring(0, selector.length() - 1).replace("\\" + '"' , "\"");
            OptionParser oParser = new OptionParser(splitLine[2].replaceAll("\"", "").trim());
            options = oParser.parse();
        } else if (splitLine.length == 2) {
            name = splitLine[0].replaceAll("\"", "").trim();
            selector = splitLine[1].trim();
            selector = selector.substring(0, selector.length() - 1).replace("\\" + '"' , "\"");
        } else if (splitLine.length == 1) {
            name = splitLine[0].replaceAll("\"", "").trim();
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

    private DoozerAction createActionInstance(Context ctx, Integer lineNumber, String actionName, String originalAction)
            throws Exception {
        String actionClassPrefix = "dev.softtest.doozer.actions.";
        String actionClassName = actionName.substring(0, 1).toUpperCase() + actionName.substring(1);
        return (DoozerAction) Class.forName(actionClassPrefix + actionClassName)
                .getConstructor(Context.class, Integer.class, String.class, String.class)
                .newInstance(ctx, lineNumber, actionName, originalAction);
    }
}
