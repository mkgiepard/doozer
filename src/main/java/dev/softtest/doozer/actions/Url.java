package dev.softtest.doozer.actions;

import org.openqa.selenium.WebDriver;

public class Url {
    WebDriver driver;
    String options;

    public Url(WebDriver driver, String options) {
        this.driver = driver;
        this.options = options;
    }

    public void execute() {
        driver.get(options);
    }

}
