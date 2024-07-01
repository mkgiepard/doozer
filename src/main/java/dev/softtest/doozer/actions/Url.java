package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

/**
 * <code>Url</code> action to navigate to new address.
 */
public class Url extends DoozerAction {

    public Url(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().get(getOptions().getOrDefault("default", ""));
    }

}
