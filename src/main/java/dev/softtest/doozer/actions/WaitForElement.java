package dev.softtest.doozer.actions;

import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class WaitForElement extends DoozerAction {

    public WaitForElement(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = getDriver().findElement(getBySelector(getSelector()));
        org.openqa.selenium.support.ui.Wait<WebDriver> wait = new WebDriverWait(getDriver(),
                Duration.ofSeconds(Integer.getInteger(getOption("seconds"))));
        wait.until(d -> element.isDisplayed());
    }
}
