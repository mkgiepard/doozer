package dev.softtest.doozer;

import java.lang.reflect.Method;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.actions.Action;

public class DoozerAction implements Action {
    private final Integer lineNumber;
    private final String actionName;
    private final String originalAction;

    private WebDriver driver;
    private String selector;
    private Map<String, String> options;
    private boolean isOptional = false;
    
    public DoozerAction(Integer lineNumber, String actionName, String originalAction) {
        this.lineNumber = lineNumber;
        this.actionName = actionName;
        this.originalAction = originalAction;
    }

    public String getActionName() {
        return actionName;
    }

    public String getOriginalAction() {
        return originalAction;
    }

    public String getSelector() {
        return selector;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getOption(String key) {
        return options.getOrDefault(key, "");
    }

    public Boolean isOptional() {
        return isOptional;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }


    public void execute() throws Exception {
        throw new Exception("!!! Should never be called !!!");
    }

    @Override
    public String toString() {
        return Integer.toString(lineNumber) + ": " + "actionName: " + actionName + "\n"
                + "selector: " + selector + "\n"
                + "options: " + options + "\n"
                + "isOptional: " + isOptional + "\n";
    }

    public By getBySelector(String s) throws Exception {

        // Selector format examples:
        // - By.cssSelector('button')
        // - By.name('my-text')

        String methodName = s.substring(s.indexOf(".") + 1, s.indexOf("("));
        String methodParam = s.substring(s.indexOf("'") + 1, s.length() - 2);

        Method bySelectorMethod = By.class.getDeclaredMethod(methodName, String.class);
        return (By) bySelectorMethod.invoke(null, methodParam);
    }
}
