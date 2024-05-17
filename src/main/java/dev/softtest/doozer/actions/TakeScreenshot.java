package dev.softtest.doozer.actions;

import java.io.File;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.google.common.io.Files;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ImageDiff;

public class TakeScreenshot extends DoozerAction {

    public TakeScreenshot(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
        String fileName = getOptions().get("default");
        if (fileName == null) {
            fileName = getOptions().getOrDefault("fileName", "screenshot-" + getLineNumber());
        }
        Files.write(screenshot, new File(getContext().getResultsDir() + fileName + ".png"));

        String goldensPath = "src/test/java/dev/softtest/doozer/scripts"
                + getContext().getResultsDir().substring("target/doozer-tests/".length()) + "goldens/";
        ImageDiff differ = new ImageDiff(goldensPath + fileName + ".png",
                getContext().getResultsDir() + fileName + ".png");
        differ.compare();
    }
}
