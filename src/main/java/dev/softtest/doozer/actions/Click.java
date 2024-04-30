package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class Click extends DoozerAction {

    public Click(WebDriver driver, String name, String selector, Map<String, String> options, Boolean isOptional) {
        super(driver, name, selector, options, isOptional);
    }

    @Override
    public void execute() throws Exception {
        WebElement submitButton = driver.findElement(getBySelector(selector));
        submitButton.click();
    }
}
