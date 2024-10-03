package dev.softtest.doozer;

import java.util.stream.*;
import java.io.File;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.Dimension;

import java.util.concurrent.TimeUnit;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;


/**
 * Main class of the Doozer framework, it creates a ParameterizedTest for each script identified by
 * provideDoozerTestFiles().
 */
@TestInstance(Lifecycle.PER_CLASS)
@Execution(ExecutionMode.CONCURRENT)
public class DoozerTest {
    private static final Logger LOG = LogManager.getLogger();
    private final String LOGGING_KEY = "ROUTINGKEY";
    private final String RESULTS_DIR = "target/doozer-tests/";
    private final String TEST_DIR_PREFIX = "src/test/java/";
    private final String DIR_SEPARATOR = "/";
    private final String WIN_DIR_SEPARATOR = "\\";

    private Map<String, TestCase> testCaseRegistry = new HashMap<>();

    @BeforeEach
    public void setup(TestInfo tInfo) throws IOException {
        createTestResultDirectory(tInfo.getDisplayName());
    }

    @AfterEach
    public void cleanup(TestInfo tInfo) {
        Context ctx = testCaseRegistry.get(tInfo.getDisplayName()).getContext();
        ctx.getWebDriver().quit();

        TestCaseReport tcr = new TestCaseReport(RESULTS_DIR, testCaseRegistry.get(tInfo.getDisplayName()));
        tcr.generate();
    }

    @AfterAll
    public void tearDown(TestInfo tInfo) {
        TestRunReport tr = new TestRunReport(RESULTS_DIR, testCaseRegistry.values());
        TestRunSummary ts = new TestRunSummary(testCaseRegistry.values());
        try {
            tr.generate();
            ts.generate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Entry point for Doozer test execution.
     * 
     * @param testScriptPath The path to doozer test script file, provided by provideDoozerTestFiles()
     * @param tInfo TestInfo for current ParameterizedTest instance.
     */
    @ParameterizedTest
    @MethodSource("provideDoozerTestFiles")
    @Tag("DOOZER")
    public void runner(Path testScriptPath, TestInfo tInfo) throws Exception {
        TestCase tc = new TestCase(testScriptPath);
        ThreadContext.put(LOGGING_KEY, testResultPath(tInfo.getDisplayName()).toString());
        tc.getContext().setDoozerDriver(initDriver());
        tc.getContext().setTestResultPath(testResultPath(tInfo.getDisplayName()));
        tc.getContext().setTestRootPath(testScriptPath.getParent());
        testCaseRegistry.put(tInfo.getDisplayName(), tc);
        
        LOG.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        LOG.info("========================== START =========================");
        LOG.info("Test: " + testScriptPath);
        
        long startTime = System.nanoTime();
        tc.readTestScript();
        try {
            System.out.println("Running: " + tInfo.getDisplayName());
            tc.run();
        } catch (Exception e) {
            throw e;
        } finally {
            long duration = System.nanoTime() - startTime;

            LOG.info("========================== STOP =========================");
            LOG.info("Execution time: " + TimeUnit.NANOSECONDS.toMillis(duration) + "[ms]");
            LOG.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        }
    }

    /**
     * Provides doozer test scripts consumed by the runner().
     * 
     * The method reads two properties `doozer.test` and `doozer.directory`. If the first one is set 
     * then the method returns a one element stream. If the latter then the output contains all the
     * directories found within passed path.
     * 
     * @return the stream of Arguments with doozer test scripts.
     */
    public Stream<Arguments> provideDoozerTestFiles() {
        String test = System.getProperty("doozer.test");
        if (test != null) {
            if (Files.notExists(Path.of(test)))
                throw new RuntimeException("'" + test + "' must exist to proceed.");

            return Stream.of(Arguments.of(test));
        }

        String directory = System.getProperty("doozer.directory");
        if (directory == null)
            throw new RuntimeException("'doozer.test' or 'doozer.directory' must be defined to proceed.");

        if (Files.notExists(Path.of(directory)))
            throw new RuntimeException("'" + directory + "' must exist to proceed.");

        File[] directories = new File(directory).listFiles(File::isDirectory);
        if (directories == null)
            throw new RuntimeException("'doozer.directory' does not contain any doozer test folders.");

        return Arrays.stream(directories)
                .map(d -> Arguments.of(directory + d.getName() + "/" + d.getName() + ".doozer"));
    }

    private DoozerDriver initDriver() {
        DoozerDriver doozerDriver = new DoozerDriver(System.getProperty("doozer.browser"));
        doozerDriver.init();
        setupWindow(doozerDriver.getDriver());
        setupTimeouts(doozerDriver.getDriver());
        return doozerDriver;
    }

    protected void setupWindow(WebDriver driver) {
        driver.manage().window().setSize(new Dimension(1280, 700));
    }

    protected void setupTimeouts(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));
    }

    private void createTestResultDirectory(String displayedName) throws IOException {
        Path testCaseResultsDir = testResultPath(displayedName);
        Files.createDirectories(testCaseResultsDir);
    }

    private Path testResultPath(String displayedName) {
        boolean onWindows = System.getProperty("os.name").contains("Windows");
        return Paths.get(RESULTS_DIR
            + displayedName.substring(
                displayedName.lastIndexOf(TEST_DIR_PREFIX) + TEST_DIR_PREFIX.length(),
                displayedName.lastIndexOf(onWindows ? WIN_DIR_SEPARATOR : DIR_SEPARATOR)));
    }

}
