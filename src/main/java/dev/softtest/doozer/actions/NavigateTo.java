package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class NavigateTo extends DoozerAction {

    public NavigateTo(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().to(getOptions().getOrDefault("default", ""));
    } 
}
