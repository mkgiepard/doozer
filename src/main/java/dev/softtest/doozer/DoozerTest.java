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

import java.util.List;
import java.util.ArrayList;

@TestInstance(Lifecycle.PER_CLASS)
public abstract class DoozerTest {
    public static WebDriver driver;
    private static List<DoozerAction> actions = new ArrayList<DoozerAction>();

    // @BeforeAll
    // hint: https://code-case.hashnode.dev/how-to-pass-parameterized-test-parameters-to-beforeeachaftereach-method-in-junit5
    // hint: https://stackoverflow.com/questions/62036724/how-to-parameterize-beforeeach-in-junit-5
    public static void setup(String testFile) throws Exception {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        Parser p = new Parser(testFile, driver);
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
        setup(testFile);
        for (DoozerAction action : actions) {
            System.out.println(action.toString());
            action.execute();
        }
    }

    public abstract Stream<Arguments> provideDoozerTestFiles();
}
