package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;
import dev.softtest.doozer.DoozerAction;

public class AssertCurrentUrl extends DoozerAction {
    
    public AssertCurrentUrl(WebDriver driver, String name, String selector, String options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        String url = driver.getCurrentUrl();
        if (!url.equals(options))
            throw new Exception("The current URL content is different!\nExpected: " + options + "\nIs: " + url);
    } 
}
