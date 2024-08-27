package dev.softtest.doozer.actions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.DoozerSelector;
import dev.softtest.doozer.ElementFinder;
import dev.softtest.doozer.ImageDiff;
import dev.softtest.doozer.ImageDiff.ImageDiffException;
import dev.softtest.doozer.ImageDiff.ImageDiffIOException;
import dev.softtest.doozer.TestArtifact;
import dev.softtest.doozer.TestArtifactType;


/**
 * <code>takeScreenshot</code> action takeS a snapshot of a current view and compares it with
 * the reference file stored in TEST_DIRECTORY/goldens directory.
 * 
 * <p>
 * The action will fail if a system property <code>doozer.failOnPixelDiff</code> is set to
 * <code>true</code> and the image comparison shows differences bigger than accepted threshold
 * (default value is set to 0.01%).
 * </p>
 */
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
        waitBeforeTakeScreenshotFor(2000);
        applyMasks();
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
        } catch (ImageDiffException|ImageDiffIOException e) {
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

    private void waitBeforeTakeScreenshotFor(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {}
    }

    private void applyMasks() {
        for (DoozerSelector sel : getContext().getMaskMap().keySet()) {
            try {
                WebElement element = ElementFinder.findElement(getContext(), sel);
                String script = "elem = document.elementFromPoint(" 
                    + element.getRect().x + ", " 
                    + element.getRect().y +");"
                    + "elem.innerHTML='" + getContext().getMaskMap().get(sel) + "';" ;
                ((JavascriptExecutor) getDriver()).executeScript(script);
            } catch (Exception e) {
                LOG.info("Exception thrown while applying a mask, this will not fail the step. " + e.getMessage());
            }

        }
    }
}
