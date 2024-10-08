package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/support_features/select_lists/#de-select-option">
 * De-select by visible text</a> interaction.
 */
public class Deselect extends DoozerAction {

    public Deselect(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = ElementFinder.findElement(getContext(), getDoozerSelector());
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(element);
        select.deselectByVisibleText(getOption("default"));
    }
}
