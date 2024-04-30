package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class NavigateBack extends DoozerAction {

    public NavigateBack(WebDriver driver, String name, String selector, Map<String, String> options, Boolean isOptional) {
        super(driver, name, selector, options, isOptional);
    }

    @Override
    public void execute() {
        driver.navigate().back();
    } 
}
