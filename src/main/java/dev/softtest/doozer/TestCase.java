package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import dev.softtest.doozer.actions.TakeScreenshot;


public class TestCase {
    protected static final Logger LOG = LogManager.getLogger();
    private final Duration WAIT_DOCUMENT_READY = Duration.ofSeconds(10);
    private Context ctx;
    private List<DoozerAction> actions;
    private Path testScriptPath;
    private String testCaseName;
    private TestResult result;
    private TestStatus status;
    private List<TestStep> steps;

    public TestCase(Path testScriptPath) {
        this.testScriptPath = testScriptPath;
        ctx = new Context();
        actions = new ArrayList<DoozerAction>();
        result = TestResult.UNKNOWN;
        status = TestStatus.NEW;
        steps = new ArrayList<TestStep>();
        
        String fileName = testScriptPath.getFileName().toString();
        testCaseName = fileName.substring(0, fileName.lastIndexOf(".doozer"));
    }

    public Context getContext() {
        return ctx;
    }

    public Path getTestScriptPath() {
        return testScriptPath;
    }

    public String getTestCaseName() {
        return testCaseName;
    }

    public List<DoozerAction> getActions() {
        return actions;
    }

    public List<TestStep> getTestSteps() {
        return steps;
    }

    public TestResult getTestResult() {
        return result;
    }

    public TestStatus getTestStatus() {
        return status;
    }

    public void readTestScript() throws Exception {
        Parser p = new Parser(ctx, testScriptPath.toString());
        setActions(p.parseScriptIntoActions());
    }

    public void run() throws Exception {
        status = TestStatus.RUNNING;
        for (DoozerAction action : getActions()) {
            TestStep step = new TestStep(action);
            try {
                LOG.info("execute: " + action.getOriginalAction());
                waitForPageLoaded(ctx.getWebDriver());
                action.resolveVariables();
                if (action.getActionName() != "set") action.resolveDoozerSelector();
                action.execute();
                if (action.getActionName().equalsIgnoreCase("takeScreenshot")) {
                    step.setArtifact(((TakeScreenshot) action).getTestArtifact());
                }
            } catch (Exception e) {
                if (!action.isOptional()) {
                    String errMsg = "TEST CASE `"+ getTestCaseName() + "` FAILED IN ACTION: `" + action.getOriginalAction() + "` >>> Root cause: " + e.getMessage();
                    saveDom();
                    takeScreenshotOnFailure();
                    result = TestResult.FAIL;
                    status = TestStatus.DONE;
                    step.setResult(TestResult.FAIL);
                    step.setStatus(TestStatus.DONE);
                    step.setError(e.getMessage());
                    if (action.getActionName().equalsIgnoreCase("takeScreenshot")) {
                        step.setArtifact(((TakeScreenshot) action).getTestArtifact());
                    }
                    steps.add(step);
                    LOG.error(errMsg);
                    fail(errMsg);
                }
                else {
                    if (action.getActionName().equalsIgnoreCase("takeScreenshot")) {
                        step.setArtifact(((TakeScreenshot) action).getTestArtifact());
                    }
                    step.setError("EXECUTION FAILED but ignoring the failure as the action is optional.");
                    LOG.warn("EXECUTION FAILED but ignoring the failure as the action is optional.");
                }
            }
            step.setResult(TestResult.PASS);
            step.setStatus(TestStatus.DONE);
            steps.add(step);
        }
        result = TestResult.PASS;
        status = TestStatus.DONE;
    }

    private void setActions(List<DoozerAction> actions) {
        this.actions = actions;
    }

    private void waitForPageLoaded(WebDriver driver) {
        Wait<WebDriver> wait = new WebDriverWait(driver, WAIT_DOCUMENT_READY);
        wait.until(
                d -> ((JavascriptExecutor) d).executeScript("return document.readyState")
                        .equals("complete"));
    }

    private void saveDom() {
        Path path = Paths.get(ctx.getTestResultPath() + "/" + testCaseName + "-DOM.html");
        byte[] domDump = ctx.getWebDriver().getPageSource().getBytes();
    
        try {
            Files.write(path, domDump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void takeScreenshotOnFailure() {
        byte[] screenshot = ((TakesScreenshot) getContext().getWebDriver()).getScreenshotAs(OutputType.BYTES);
        Path path = Paths.get(ctx.getTestResultPath() + "/" +  testCaseName + "-onFAILURE.png");

        try {
            Files.write(path, screenshot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
