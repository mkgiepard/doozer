package dev.softtest.doozer.actions;

import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class KeyDown extends DoozerAction {

    public KeyDown(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        new Actions(getDriver()).keyDown(getOption("default"));
    }
    
}
