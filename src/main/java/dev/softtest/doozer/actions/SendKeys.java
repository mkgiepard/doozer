package dev.softtest.doozer.actions;

import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/elements/interactions/#send-keys">
 * Send Keys</a> interaction.
 */
public class SendKeys extends DoozerAction {

    public SendKeys(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        new Actions(getDriver()).sendKeys(getOption("default")).perform();
    }
    
}
