package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;


/**
 * Asserts current URL with <a href="https://www.selenium.dev/documentation/webdriver/interactions/#get-current-url">
 * Get current URL.</a>
 */
public class AssertCurrentUrl extends DoozerAction {
    
    public AssertCurrentUrl(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        String url = getDriver().getCurrentUrl();
        if (!url.equals(getOptions().get("default")))
            throw new Exception("The current URL content is different!\nExpected: " + getOptions() + "\nIs: " + url);
    } 
}
