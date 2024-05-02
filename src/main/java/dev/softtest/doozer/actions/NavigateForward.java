package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class NavigateForward extends DoozerAction {

    public NavigateForward(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().forward();
    } 
}
