package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class Refresh extends DoozerAction {

    public Refresh(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().refresh();
    } 
}
