package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;

public class Click extends DoozerAction implements IAction {
    WebDriver driver;

    public Click(WebDriver driver, String selector, String options) {
        super("click", selector, options);
        this.driver = driver;
    }

    public void execute() throws Exception {
        WebElement submitButton = driver.findElement(getBySelector(selector));
        submitButton.click();
    }
}
