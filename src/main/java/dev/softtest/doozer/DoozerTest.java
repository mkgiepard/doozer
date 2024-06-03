package dev.softtest.doozer;

import java.util.stream.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public abstract class DoozerTest {
    protected static final Logger logger = LogManager.getLogger();

    public Map<String, TestCase> testCaseRegistry = new HashMap<>();
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
        generateReport();
    }

    @ParameterizedTest
    @MethodSource("provideDoozerTestFiles")
    public void runner(String testFile, TestInfo tInfo) throws Exception {
        TestCase tc = new TestCase(testFile);
        ThreadContext.put("ROUTINGKEY", tc.getTestCaseName());
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

    public abstract Stream<Arguments> provideDoozerTestFiles();

    private WebDriver initWebDriver() {
        ChromeOptions options = new ChromeOptions();
        String browser = System.getProperty("browser", "chrome-headless");
        if (browser.equals("chrome-headless")) {
            options.addArguments("--headless=new");
        }
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
        //Files.createDirectories(Paths.get(RESULTS_DIR));

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

    private void generateReport() {
        TestReport tr = new TestReport();
        Path path = Paths.get(RESULTS_DIR + "doozer-report.html");
        String htmlReport = "<html>";
        htmlReport += tr.includeCSS();
        htmlReport += tr.includeHeader();
        
        
        htmlReport += "<body>";
        htmlReport += "<div class=\"container-main\">";
        htmlReport += tr.getHeader();

        for (TestCase tc : testCaseRegistry.values()) {
            htmlReport += "<div class=\"container-testcase\">";
            htmlReport += tr.getTestCaseHeader(tc);
            htmlReport += tr.getTestCaseImages(tc);
            htmlReport += "</div>";
        };
        htmlReport += "</div></body>";
        htmlReport += tr.includeJS();
        htmlReport += "</html>";
    
        try {
            Files.write(path, htmlReport.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
