package dev.softtest.doozer.actions;

import java.io.File;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import com.google.common.io.Files;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ImageDiff;
import dev.softtest.doozer.TestArtifact;
import dev.softtest.doozer.TestArtifactType;

public class TakeScreenshot extends DoozerAction {
    private String goldensPath;
    private String resultsPath;
    private ImageDiff differ;
    private String fileName;

    public TakeScreenshot(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        byte[] screenshot = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
        fileName = getOptions().get("default");
        if (fileName == null) {
            fileName = getOptions().getOrDefault("fileName", "screenshot-" + getLineNumber());
        }
        fileName += ".png";
        Files.write(screenshot, new File(getContext().getResultsDir() + fileName));

        goldensPath = "src/test/java/dev/softtest/doozer/scripts"
                + getContext().getResultsDir().substring("target/doozer-tests/".length()) + "goldens/";
        resultsPath = getContext().getResultsDir();
        differ = new ImageDiff(goldensPath + fileName, resultsPath + fileName);
        
        try {
            differ.compare();
        } catch (Exception e) {
            Boolean failOnPixelDiff = System.getProperty("failOnPixelDiff") == null ? true
                    : Boolean.getBoolean(System.getProperty("failOnPixelDiff"));
            if (failOnPixelDiff)
                throw e;
        }
    }

    public TestArtifact getTestArtifact() {
        TestArtifact artifact = new TestArtifact.Builder(TestArtifactType.SCREENSHOT)
            .goldenPath(goldensPath)
            .resultPath(resultsPath)
            .diff(differ.getDiffPixel())
            .percentDiff(differ.getDiffRatio())
            .percentDiffThreshold(differ.getThreshold())
            .build();
        return artifact;
    }
}
