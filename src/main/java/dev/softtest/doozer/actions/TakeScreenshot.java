package dev.softtest.doozer.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ImageDiff;
import dev.softtest.doozer.TestArtifact;
import dev.softtest.doozer.TestArtifactType;

public class TakeScreenshot extends DoozerAction {
    private Path goldenImgPath;
    private Path resultImgPath;
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
        fileName += "-" + getContext().getDoozerDriver().getBrowserDesc() + ".png";
        
        Path goldensPath = Paths.get(getContext().getTestRootPath().toString(), "goldens/");
        Path resultsPath = Paths.get(getContext().getTestResultPath() + "/");

        goldenImgPath = Paths.get(goldensPath.toString(), fileName);
        resultImgPath = Paths.get(resultsPath.toString(),  fileName);

        Files.write(resultImgPath, screenshot);

        differ = new ImageDiff(goldenImgPath, resultImgPath);
        
        try {
            differ.compare();
        } catch (Exception e) {
            if (Boolean.parseBoolean(System.getProperty("doozer.failOnPixelDiff", "true")))
                throw e;
        }
    }

    public TestArtifact getTestArtifact() {
        TestArtifact artifact = new TestArtifact.Builder(TestArtifactType.SCREENSHOT, fileName)
            .goldenPath(goldenImgPath)
            .resultPath(resultImgPath)
            .diff(differ.getDiffPixel())
            .percentDiff(differ.getDiffRatio())
            .percentDiffThreshold(differ.getThreshold())
            .build();
        return artifact;
    }
}
