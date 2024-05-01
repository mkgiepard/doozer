package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.DoozerAction;

import java.time.Duration;
import java.util.Map;

public class Alert extends DoozerAction {

    public Alert(WebDriver driver,
            String name,
            String selector,
            Map<String, String> options,
            Boolean isOptional) {
        super(driver, name, selector, options, isOptional);
    }

    @Override
    public void execute() throws Exception {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        org.openqa.selenium.Alert alert = wait.until(ExpectedConditions.alertIsPresent());

        if (options.get("default").equals("confirm")) alert.accept();
        if (options.get("default").equals("dismiss")) alert.dismiss();
        if (!options.get("prompt").isEmpty()) {
            alert.sendKeys(options.get("prompt"));
            alert.accept();
        }

    }
}
