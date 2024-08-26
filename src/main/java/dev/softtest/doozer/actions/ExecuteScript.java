package dev.softtest.doozer.actions;

import org.openqa.selenium.JavascriptExecutor;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class ExecuteScript extends DoozerAction {

        public ExecuteScript(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        ((JavascriptExecutor) getDriver()).executeScript(getOptions().get("default"));
    }    
}
