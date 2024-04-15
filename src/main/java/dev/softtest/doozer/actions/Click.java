package dev.softtest.doozer.actions;

import java.lang.reflect.Method;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Click implements IAction {
    WebDriver driver;
    String selector;
    String options;

    public Click(WebDriver driver, String selector, String options) {
        this.driver = driver;
        this.selector = selector;
        this.options = options;
    }

    public void execute() {
        try {
            WebElement submitButton = driver.findElement(getBySelector(selector));
            submitButton.click();
        } catch (Exception e) {
            System.out.println("ups...");
        }
    }

    private By getBySelector(String s) throws Exception {

        // Selector format examples:
        // - By.cssSelector('button')
        // - By.name('my-text')

        String methodName = s.substring(s.indexOf(".") + 1, s.indexOf("("));
        String methodParam = s.substring(s.indexOf("'") + 1, s.length() - 2);

        Method bySelectorMethod = By.class.getDeclaredMethod(methodName, String.class);
        return (By) bySelectorMethod.invoke(null, methodParam);
    }
}
