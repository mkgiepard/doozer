package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

public class NavigateForward extends DoozerAction {

    public NavigateForward(WebDriver driver, String name, String selector, String options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() {
        driver.navigate().forward();
    } 
}
