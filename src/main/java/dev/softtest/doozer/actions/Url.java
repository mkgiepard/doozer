package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class Url extends DoozerAction {

    public Url(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().get(getOptions().getOrDefault("default", ""));
    }

}
