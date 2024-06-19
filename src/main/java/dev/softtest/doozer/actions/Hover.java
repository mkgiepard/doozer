package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/actions_api/mouse/#move-to-element">
 * Move to element (aka hover)</a> interaction.
 */
public class Hover extends DoozerAction {

    public Hover(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = ElementFinder.findElement(getContext(), getDoozerSelector());
        new Actions(getDriver()).moveToElement(element);      
    }
}
