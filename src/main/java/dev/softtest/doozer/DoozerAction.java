package dev.softtest.doozer;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.actions.IAction;

public class DoozerAction implements IAction {
    public WebDriver driver;
    public String actionName;
    public String selector;
    public String options;
    public int lineNumber;

    public DoozerAction(WebDriver driver, String actionName, String selector, String options) {
        this.driver = driver;
        this.actionName = actionName;
        this.selector = selector;
        this.options = options;
    }

    public String getActionName() {
        return actionName;
    }

    public String getSelector() {
        return selector;
    }

    public String getOptions() {
        return options;
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

    @Override
    public String toString() {
        return Integer.toString(lineNumber) + ": " + "actionName: " + actionName + "\n"
                + "selector: " + selector + "\n"
                + "options: " + options + "\n";
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
