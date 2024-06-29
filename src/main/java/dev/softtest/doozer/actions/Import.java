package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;


/**
 * Dummy class for Import action that is fully served in Parser.
 */
public class Import extends DoozerAction {
    
    public Import(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        // do nothing
    }
    
}
