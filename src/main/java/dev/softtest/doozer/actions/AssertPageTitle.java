package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;

import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class AssertPageTitle extends DoozerAction {

    public AssertPageTitle(WebDriver driver, String name, String selector, Map<String, String> options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        String pageTitle = driver.getTitle();
        if (!pageTitle.equals(options.get("default")))
            throw new Exception("The pageTitle content is different!\nExpected: " + options + "\nIs: " + pageTitle);
    }

}
