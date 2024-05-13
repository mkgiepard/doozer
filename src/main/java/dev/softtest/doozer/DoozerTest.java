package dev.softtest.doozer;

import java.util.stream.*;
import java.time.Duration;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@TestInstance(Lifecycle.PER_CLASS)
// @Execution(ExecutionMode.CONCURRENT)
public abstract class DoozerTest {
    protected static final Logger logger = LogManager.getLogger();

    public static WebDriver driver;
    private static List<DoozerAction> actions = new ArrayList<DoozerAction>();
    private static Context ctx;

    private final String RESULTS_DIR = "target/doozer-tests/";

    // @BeforeAll
    // hint: https://code-case.hashnode.dev/how-to-pass-parameterized-test-parameters-to-beforeeachaftereach-method-in-junit5
    // hint: https://stackoverflow.com/questions/62036724/how-to-parameterize-beforeeach-in-junit-5
    public void setup(String testFile) throws Exception {
        Files.createDirectories(Paths.get(RESULTS_DIR));

        driver = new ChromeDriver();
        setupWindow(driver);
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        ctx = new Context();

        String testResultsDir = RESULTS_DIR
                + testFile.substring(testFile.lastIndexOf("/"), testFile.lastIndexOf(".doozer"))
                + "/";
        Files.createDirectories(Paths.get(testResultsDir));
        ctx.setResultsDir(testResultsDir);

        // Logger per Test -- START
        final LoggerContext logCtx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = logCtx.getConfiguration();     
        
        @SuppressWarnings("rawtypes")
        final Layout layout =  config.getRootLogger().getAppenders().get("logFile").getLayout();

        @SuppressWarnings("unchecked")
        Appender perTestFileAppender = FileAppender.newBuilder()
            .setName(testResultsDir)
            .withFileName(testResultsDir + "doozer.log")
            .setLayout(layout)
            .withAdvertise(false)
            .withAppend(false)
            .build();
            perTestFileAppender.start();
        config.getRootLogger().addAppender(perTestFileAppender, Level.INFO, null);
        // Logger per Test -- STOP  
    }

    public void setupWindow(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1280, 700));
    }

    @AfterEach
    public void cleanUp() {
        actions.clear();
        driver.quit();
        
        // Logger per Test -- START
        final LoggerContext logCtx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = logCtx.getConfiguration();     
        config.getRootLogger().removeAppender(ctx.getResultsDir());
        // Logger per Test -- STOP
    }

    @AfterAll
    public void tearDown() {
        driver.quit();
    }

    @ParameterizedTest
    @MethodSource("provideDoozerTestFiles")
    public void runner(String testFile) throws Exception {
        
        setup(testFile);

        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info("========================== START =========================");
        logger.info("Test: " + testFile);
        
        long startTime = System.nanoTime();

        Parser p = new Parser(ctx, testFile, driver);
        actions = p.parse();

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
        Path path = Paths.get(ctx.getResultsDir() + s[s.length-1] + "-DOM.html");
        byte[] domDump = driver.getPageSource().getBytes();
    
        try {
            Files.write(path, domDump);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public abstract Stream<Arguments> provideDoozerTestFiles();
}
