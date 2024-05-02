package dev.softtest.doozer;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.actions.Action;

import java.util.Map;

public class DoozerAction implements Action {
    public WebDriver driver;
    public String actionName;
    public String selector;
    public Map<String, String> options;
    public int lineNumber;
    public boolean isOptional = false;

    public DoozerAction(
            WebDriver driver,
            String actionName,
            String selector,
            Map<String, String> options,
            Boolean isOptional) {
        this.driver = driver;
        this.actionName = actionName;
        this.selector = selector;
        this.options = options;
        this.isOptional = isOptional;
    }

    public String getActionName() {
        return actionName;
    }

    public String getSelector() {
        return selector;
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

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void execute() throws Exception {
        throw new Exception("!!! Should never be called !!!");
    }

    public String print() {
        return actionName + " " + selector + " " + options;
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
