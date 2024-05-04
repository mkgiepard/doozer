package dev.softtest.doozer.actions;

import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class KeyUp extends DoozerAction {

    public KeyUp(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        new Actions(getDriver()).keyUp(getOption("default"));
    }
    
}
