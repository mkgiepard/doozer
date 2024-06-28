package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

/**
 * Stops test execution for 5 [s], this value can be overwritten by setting the expected time (in seconds) in args.
 */
public class Wait extends DoozerAction {
    private final int DEFAULT_WAIT_MS = 5000;

    public Wait(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Thread.sleep(getOption("default").isEmpty() ? DEFAULT_WAIT_MS : Integer.parseInt(getOption("default"))*1000);
    }
}

