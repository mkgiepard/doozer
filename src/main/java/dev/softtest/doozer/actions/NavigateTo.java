package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

public class NavigateTo extends DoozerAction {

    public NavigateTo(WebDriver driver, String name, String selector, String options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() {
        driver.navigate().to(options);
    } 
}
