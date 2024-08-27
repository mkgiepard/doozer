package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class AddMask extends DoozerAction {

    public AddMask(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        if (getDoozerSelector() != null && getOptions() != null) {
            getContext().addMask(getDoozerSelector(), getOption("default"));
        }
    }
}
