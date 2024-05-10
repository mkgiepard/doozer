package dev.softtest.doozer;

import java.util.stream.*;
import java.time.Duration;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class DoozerTest {
    protected static final Logger logger = LogManager.getLogger();

    public static WebDriver driver;
    private static List<DoozerAction> actions = new ArrayList<DoozerAction>();
    private static Context ctx;

    // @BeforeAll
    // hint: https://code-case.hashnode.dev/how-to-pass-parameterized-test-parameters-to-beforeeachaftereach-method-in-junit5
    // hint: https://stackoverflow.com/questions/62036724/how-to-parameterize-beforeeach-in-junit-5
    public static void setup(String testFile) throws Exception {
        Files.createDirectories(Paths.get("target/doozer-tests/"));

        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        ctx = new Context();
        Parser p = new Parser(ctx, testFile, driver);
        actions = p.parse();
    }

    @AfterEach
    public void cleanUp() {
        actions.clear();
        driver.quit();
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

    @ParameterizedTest
    @MethodSource("provideDoozerTestFiles")
    public void runner(String testFile) throws Exception {
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info("========================== START =========================");
        logger.info("Test: " + testFile);
        
        setup(testFile);
        long startTime = System.nanoTime();
        for (DoozerAction action : actions) {
            try {
                logger.info("execute: " + action.getOriginalAction());
                waitForPageLoaded();
                action.resolveVariables();
                action.execute();
            } catch (Exception e) {
                if (!action.isOptional()) {
                    logger.error("EXECUTION FAILED IN ACTION: " + action.getOriginalAction() + " >>> Root cause: " + e.getMessage());
                    e.printStackTrace();
                    saveDom(testFile);
                    fail("EXECUTION FAILED IN ACTION: " + action.getOriginalAction() + " >>> Root cause: " + e.getMessage());
                    throw e;
                }
                else {
                    logger.warn("EXECUTION FAILED but ignoring the failure as the action is optional.");
                }
            }
        }

        logger.info("========================== STOP =========================");
        long duration = System.nanoTime() - startTime;
        logger.info("Execution time: " + TimeUnit.NANOSECONDS.toMillis(duration) + "[ms]");
        logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

    public void waitForPageLoaded() {
        Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(2));
        wait.until(
                d -> ((JavascriptExecutor) d).executeScript("return document.readyState")
                        .equals("complete"));
    }

    public void saveDom(String name) {
        String[] s = name.split("/");
        Path path = Paths.get("target/doozer-tests/" + s[s.length-1] + "-DOM.html");
        byte[] domDump = driver.getPageSource().getBytes();
    
        try {
            Files.write(path, domDump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Stream<Arguments> provideDoozerTestFiles();
}
