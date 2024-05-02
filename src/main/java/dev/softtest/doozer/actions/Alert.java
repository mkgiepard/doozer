package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.DoozerAction;

import java.time.Duration;

public class Alert extends DoozerAction {

    public Alert(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Wait<WebDriver> wait = new WebDriverWait(getDriver(), Duration.ofSeconds(2));
        org.openqa.selenium.Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        if (getOptions().get("default").equals("confirm")) alert.accept();
        if (getOptions().get("default").equals("dismiss")) alert.dismiss();
        if (!getOptions().get("prompt").isEmpty()) {
            alert.sendKeys(getOptions().get("prompt"));
            alert.accept();
        }

    }
}
