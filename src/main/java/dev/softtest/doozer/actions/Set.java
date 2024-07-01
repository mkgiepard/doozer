package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;


/**
 * <code>Set</code> action for adding variables to TestContext.
 */
public class Set extends DoozerAction {

    public Set(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }
    
    @Override
    public void execute() throws Exception {
        this.getContext().setVariable(getSelector(), getOption("default"));
    }

}
