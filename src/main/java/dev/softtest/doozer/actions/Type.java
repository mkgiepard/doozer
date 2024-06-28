package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/elements/interactions/#send-keys">
 * Send keys</a> interaction.
 */
public class Type extends DoozerAction {

    public Type(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement textBox = ElementFinder.findElement(getContext(), getDoozerSelector());
        textBox.sendKeys(getOptions().getOrDefault("default", ""));
    }

}
