package dev.softtest.doozer.actions;

import java.io.File;
import java.time.Duration;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.google.common.io.Files;
import dev.softtest.doozer.DoozerAction;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TakeScreenshot extends DoozerAction {

    public TakeScreenshot(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        Wait<WebDriver> wait = new WebDriverWait(getDriver(), Duration.ofSeconds(2));
        wait.until(webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));

        byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
        String fileName = getOptions().get("default");
        if (fileName == null) {
            fileName = getOptions().getOrDefault("fileName", "screenshot");
        }
        Files.write(screenshot, new File("./target/doozer-tests/" + fileName + ".png"));
    }
}
