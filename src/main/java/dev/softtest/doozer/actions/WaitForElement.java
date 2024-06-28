package dev.softtest.doozer.actions;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;


/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/waits/#explicit-waits">
 * Explicit wait</a> interaction.
 */
public class WaitForElement extends DoozerAction {

    public WaitForElement(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = ElementFinder.findElement(getContext(), getDoozerSelector());
        org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(getDriver(),
                Duration.ofSeconds(Integer.parseInt(getOption("seconds"))));
        wait.until(d -> element.isDisplayed());
    }
}
