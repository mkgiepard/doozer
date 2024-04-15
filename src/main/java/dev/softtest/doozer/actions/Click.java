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

    public void execute() {
        try {
            WebElement submitButton = driver.findElement(getBySelector(selector));
            submitButton.click();
        } catch (Exception e) {
            System.out.println("ups...");
            System.out.println(e);
        }
    }
}
