package dev.softtest.doozer;

public class DoozerAction {
    private String actionName;
    private String selector;
    private String options;

    DoozerAction(String actionName, String selector, String options) {
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
}
