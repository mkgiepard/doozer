package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.DoozerAction;

public class AssertPageTitle extends DoozerAction {

    public AssertPageTitle(WebDriver driver, String name, String selector, String options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        String pageTitle = driver.getTitle();
        if (!pageTitle.equals(options))
            throw new Exception("The pageTitle content is different!\nExpected: " + options + "\nIs: " + pageTitle);
    }

}
