package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.DoozerAction;

public class AssertInnerText extends DoozerAction implements IAction {
    WebDriver driver;

    public AssertInnerText(WebDriver driver, String selector, String options) {
        super("assertInnerText", selector, options);
        this.driver = driver;
    }

    public void execute() throws Exception {
        WebElement message = driver.findElement(getBySelector(selector));
        String value = message.getText();
        if (!value.equals(options))
            throw new Exception("The innerText content is different!\nExpected: " + options + "\nIs: " + value);
    }

}
