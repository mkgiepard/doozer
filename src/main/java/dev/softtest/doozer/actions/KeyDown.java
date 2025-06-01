package dev.softtest.doozer.actions;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;


/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/actions_api/keyboard/#key-down">
 * Key down</a> interaction.
 */
public class KeyDown extends DoozerAction {

    public KeyDown(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Keys key = Keys.valueOf(getOption("default").toUpperCase());
        new Actions(getDriver()).keyDown(key).perform();
    }
    
}
