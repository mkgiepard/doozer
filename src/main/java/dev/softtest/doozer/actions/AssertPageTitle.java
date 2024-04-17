package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.DoozerAction;

public class AssertPageTitle extends DoozerAction implements IAction {
    WebDriver driver;

    public AssertPageTitle(WebDriver driver, String selector, String options) {
        super("assertPageTitle", selector, options);
        this.driver = driver;
    }

    public void execute() throws Exception {
        String pageTitle = driver.getTitle();
        if (!pageTitle.equals(options))
            throw new Exception("The pageTitle content is different!\nExpected: " + options + "\nIs: " + pageTitle);
    }

}
