package dev.softtest.doozer.actions;

import java.io.File;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.google.common.io.Files;
import dev.softtest.doozer.DoozerAction;

public class TakeScreenshot extends DoozerAction {

    public TakeScreenshot(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
        String fileName = getOptions().get("default");
        if (fileName == null) {
            fileName = getOptions().getOrDefault("fileName", "screenshot");
        }
        Files.write(screenshot, new File("./target/doozer-tests/" + fileName + ".png"));
    }
}
