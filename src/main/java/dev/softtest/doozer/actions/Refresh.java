package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class Refresh extends DoozerAction {

    public Refresh(WebDriver driver, String name, String selector, Map<String, String> options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() {
        driver.navigate().refresh();
    } 
}
