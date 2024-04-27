package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class AssertInnerText extends DoozerAction {

    public AssertInnerText(WebDriver driver, String name, String selector, Map<String, String> options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        WebElement message = driver.findElement(getBySelector(selector));
        String value = message.getText();
        if (!value.equals(options.get("default")))
            throw new Exception("The innerText content is different!\nExpected: " + options + "\nIs: " + value);
    }

}
