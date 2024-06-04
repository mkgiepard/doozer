package dev.softtest.doozer;

import java.util.stream.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;

import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.Dimension;

import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class DoozerTest {
    private final Logger logger = LogManager.getLogger();
    private final String LOGGING_KEY = "ROUTINGKEY";
    private Map<String, TestCase> testCaseRegistry = new HashMap<>();
    private final String RESULTS_DIR = "target/doozer-tests/";

    @BeforeEach
    public void setup(TestInfo tInfo) throws IOException {
        createResultsDirectories(tInfo.getDisplayName());
    }

    @AfterEach
    public void cleanup(TestInfo tInfo) {
        Context ctx = testCaseRegistry.get(tInfo.getDisplayName()).getContext();
        ctx.getWebDriver().quit();
    }

    @AfterAll
    public void tearDown(TestInfo tInfo) {
        TestRunReport tr = new TestRunReport(RESULTS_DIR, testCaseRegistry.values());
        tr.generate();
    }

    @ParameterizedTest
    @MethodSource("provideDoozerTestFiles")
    public void runner(String testFile, TestInfo tInfo) throws Exception {
        TestCase tc = new TestCase(testFile);
        ThreadContext.put(LOGGING_KEY, tc.getTestCaseName());
        tc.getContext().setWebDriver(initWebDriver());
        tc.getContext().setResultsDir(getResultsDirectory(tInfo.getDisplayName()));
        testCaseRegistry.put(tInfo.getDisplayName(), tc);
        
        logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        logger.info("========================== START =========================");
        logger.info("Test: " + testFile);
        
        long startTime = System.nanoTime();
        tc.readTestScript();
        try {
            tc.run();
        } catch (Exception e) {
            throw e;
        } finally {
            long duration = System.nanoTime() - startTime;

            logger.info("========================== STOP =========================");
            logger.info("Execution time: " + TimeUnit.NANOSECONDS.toMillis(duration) + "[ms]");
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    public Stream<Arguments> provideDoozerTestFiles() {
        String test = System.getProperty("doozer.test");
        if (test != null) {
            return Stream.of(Arguments.of(test));
        }

        String directory = System.getProperty("doozer.directory");
        if (directory == null)
            throw new RuntimeException("'doozer.test' or 'doozer.directory' must be defined to proceed.");

        File[] directories = new File(directory).listFiles(File::isDirectory);
        return Arrays.stream(directories)
                .map(d -> Arguments.of(directory + d.getName() + "/" + d.getName() + ".doozer"));
    }

    private WebDriver initWebDriver() {
        ChromeOptions options = new ChromeOptions();
        String browser = System.getProperty("doozer.browser", "chrome-headless");
        if (browser.equals("chrome-headless")) {
            options.addArguments("--headless=new");
        }
        // Keeps the screenshot size in sync with the window size on devices with Retina screens.
        options.addArguments("--force-device-scale-factor=1");
        WebDriver driver = new ChromeDriver(options);
        setupWindow(driver);
        setupTimeouts(driver);
        return driver;
    }

    protected void setupWindow(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1280, 700));
    }

    protected void setupTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));
    }

    private void createResultsDirectories(String displayedName) throws IOException {
        String testCaseResultsDir = getResultsDirectory(displayedName);
        Files.createDirectories(Paths.get(testCaseResultsDir));
    }

    private String getResultsDirectory(String displayedName) {
        return RESULTS_DIR
            + displayedName.substring(
                displayedName.lastIndexOf("/"),
                displayedName.lastIndexOf(".doozer"))
            + "/";
    }

}
