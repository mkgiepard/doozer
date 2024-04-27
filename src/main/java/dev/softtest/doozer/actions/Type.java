package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;
import java.util.Map;

public class Type extends DoozerAction {

    public Type(WebDriver driver, String name, String selector, Map<String, String> options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        WebElement textBox = driver.findElement(getBySelector(selector));
        textBox.sendKeys(this.options.getOrDefault("default", ""));
    }

}
