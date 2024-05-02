package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class NavigateBack extends DoozerAction {

    public NavigateBack(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().back();
    } 
}
