package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class NavigateTo extends DoozerAction {

    public NavigateTo(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().to(getOptions().getOrDefault("default", ""));
    } 
}
