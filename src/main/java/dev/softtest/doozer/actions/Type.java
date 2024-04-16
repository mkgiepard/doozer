package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;

public class Type extends DoozerAction implements IAction {
    WebDriver driver;

    public Type(WebDriver driver, String selector, String options) {
        super("type", selector, options);
        this.driver = driver;
    }

    public void execute() throws Exception {
        WebElement textBox = driver.findElement(getBySelector(selector));
        textBox.sendKeys(this.options);
    }

}
