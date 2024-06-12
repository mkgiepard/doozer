package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.ElementClickInterceptedException;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

public class Click extends DoozerAction {

    public Click(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement submitButton = ElementFinder.findElement(getContext(), getDoozerSelector());
        try {
            submitButton.click();
        } catch (ElementClickInterceptedException e) {
            LOG.warn("click() failed due to: " + e.getMessage() + ". Retrying.");
            submitButton = getDriver().findElement(getDoozerSelector().getBySelector());
            submitButton.click();
        }        
    }
}
