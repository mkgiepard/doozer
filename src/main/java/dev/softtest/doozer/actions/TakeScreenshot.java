package dev.softtest.doozer.actions;

import java.io.File;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import com.google.common.io.Files;

import dev.softtest.doozer.DoozerAction;

import java.util.Map;

public class TakeScreenshot extends DoozerAction {

    public TakeScreenshot(WebDriver driver, String name, String selector, Map<String, String> options, Boolean isOptional) {
        super(driver, name, selector, options, isOptional);
    }

    @Override
    public void execute() throws Exception {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String fileName = options.get("default");
        if (fileName == null) {
            fileName = options.getOrDefault("fileName", "screenshot");
        }
        Files.write(screenshot, new File("./target/" + fileName + ".png"));
    }
}
