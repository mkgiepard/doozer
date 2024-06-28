package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/interactions/navigation/#refresh">
 * Refresh</a> interaction.
 */
public class Refresh extends DoozerAction {

    public Refresh(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() {
        getDriver().navigate().refresh();
    } 
}
