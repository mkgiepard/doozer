package dev.softtest.doozer.actions;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements <a href=
 * "https://www.selenium.dev/documentation/webdriver/waits/#explicit-waits">
 * Explicit wait</a> interaction. Default wait duration is set to 10[s] and can be overwritten by
 * passing <code>seconds=VALUE</code> in <code>args</code>. 
 */
public class WaitForElement extends DoozerAction {
    private static final Integer DEFAULT_WAIT_TIMEOUT = 10;

    public WaitForElement(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = ElementFinder.findElement(getContext(), getDoozerSelector());
        Integer timeout = getOption("seconds").isEmpty() ?
                DEFAULT_WAIT_TIMEOUT : Integer.parseInt(getOption("seconds"));
        org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(getDriver(),
                Duration.ofSeconds(timeout));
        wait.until(d -> element.isDisplayed());
    }
}
