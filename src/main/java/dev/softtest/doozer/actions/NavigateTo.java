package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class NavigateTo extends DoozerAction {

    public NavigateTo(WebDriver driver, String name, String selector, Map<String, String> options, Boolean isOptional) {
        super(driver, name, selector, options, isOptional);
    }

    @Override
    public void execute() {
        driver.navigate().to(options.getOrDefault("default", ""));
    } 
}
