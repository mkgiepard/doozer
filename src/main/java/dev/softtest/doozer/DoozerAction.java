package dev.softtest.doozer;

import java.lang.reflect.Method;

import org.openqa.selenium.By;

public class DoozerAction {
    public String actionName;
    public String selector;
    public String options;

    public DoozerAction(String actionName, String selector, String options) {
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

    @Override
    public String toString() {
        return "actionName: " + actionName + "\n"
                + "selector: " + selector + "\n"
                + "options: " + options + "\n";
    }

    public By getBySelector(String s) throws Exception {

        // Selector format examples:
        // - By.cssSelector('button')
        // - By.name('my-text')

        String methodName = s.substring(s.indexOf(".") + 1, s.indexOf("("));
        String methodParam = s.substring(s.indexOf("'") + 1, s.length() - 2);

        System.out.println(methodName);
        System.out.println(methodParam);

        Method bySelectorMethod = By.class.getDeclaredMethod(methodName, String.class);
        return (By) bySelectorMethod.invoke(null, methodParam);
    }
}
