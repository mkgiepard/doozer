package dev.softtest.doozer.actions;

import java.io.File;
import java.io.IOException;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;

public class TakeScreenshot implements IAction {
    WebDriver driver;
    String options;

    public TakeScreenshot(WebDriver driver, String selector, String options) {
        this.driver = driver;
        this.options = options;
    }

    public void execute() {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        try {
            Files.write(screenshot, new File("/tmp/" + options + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
