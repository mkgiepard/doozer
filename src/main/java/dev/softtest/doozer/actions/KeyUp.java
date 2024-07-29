package dev.softtest.doozer.actions;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/actions_api/keyboard/#key-up">
 * Key up</a> interaction.
 */
public class KeyUp extends DoozerAction {

    public KeyUp(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Keys key = Keys.valueOf(getOption("default").toUpperCase());
        new Actions(getDriver()).keyUp(key).perform();
    }
    
}
