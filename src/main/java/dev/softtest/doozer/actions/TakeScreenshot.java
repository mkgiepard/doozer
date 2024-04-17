package dev.softtest.doozer.actions;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;

import dev.softtest.doozer.DoozerAction;

public class TakeScreenshot extends DoozerAction {

    public TakeScreenshot(WebDriver driver, String name, String selector, String options) {
        super(driver, name, selector, options);
    }

    @Override
    public void execute() throws Exception {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(screenshot, new File("/tmp/" + options + ".png"));
    }
}
