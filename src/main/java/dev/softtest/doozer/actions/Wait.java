package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;


public class Wait extends DoozerAction {

    public Wait(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Thread.sleep(Integer.parseInt(getOption("default"))*1000);
    }
}

