package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class Iframe extends DoozerAction {

    public Iframe(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        if (getSelector() != null && getSelector() != "") {
            getDriver().switchTo().frame(getDriver().findElement(getBySelector(getSelector())));
        }
        if (getOption("name") != "") {
            getDriver().switchTo().frame(getOption("name"));
        }
        if (getOption("id") != "") {
            getDriver().switchTo().frame(Integer.parseInt(getOption("id")));
        }
        getDriver().switchTo().defaultContent();
    }
    
}
