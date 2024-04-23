package dev.softtest.doozer;

import java.io.BufferedReader;
import java.util.stream.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;

public class DoozerTest {
    public static WebDriver driver;
    private static String doozerTestFile = "src/test/java/dev/softtest/doozer/scripts/firstTest.doozer";
    private static ArrayList<DoozerAction> actions = new ArrayList<DoozerAction>();
    private static final String splitterChar = " \"";

    // @BeforeAll
    public static void setup(String testFile) throws Exception {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        Charset charset = StandardCharsets.UTF_8;
        Path path = FileSystems.getDefault().getPath(testFile).toAbsolutePath();

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = null;
            int lineCounter = 1;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(splitterChar);

                String name = null;
                String selector = null;
                String options = null;
                if (splitLine.length == 3) {
                    name = splitLine[0].replaceAll("\"", "");
                    selector = splitLine[1].replaceAll("\"", "");
                    options = splitLine[2].replaceAll("\"", "");
                } else if (splitLine.length == 2) {
                    name = splitLine[0].replaceAll("\"", "");
                    selector = splitLine[1].replaceAll("\"", "");
                } else if (splitLine.length == 1) {
                    name = splitLine[0].replaceAll("\"", "");
                }
                if (name == null || name.length() == 0) {
                    System.out.println(Integer.toString(lineCounter++) + ": ...empty line");
                    continue;
                }
                DoozerAction action = createActionInstance(name, selector, options);
                action.setLineNumber(lineCounter++);
                actions.add(action);
            }
        } catch (Exception e) {
            System.err.format("Exception: %s%n", e);
            throw e;
        }
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

    private static Stream<Arguments> provideDoozerTestFiles() {
        String testFolder = System.getProperty("test.folder");
        return Stream.of(
          Arguments.of(testFolder + "firstTest.doozer"),
          Arguments.of(testFolder + "secondTest.doozer"),
          Arguments.of(testFolder + "googleTest.doozer")
        );
    }

    private static DoozerAction createActionInstance(String actionName, String actionSelector, String actionOptions)
            throws Exception {
        String actionClassPrefix = "dev.softtest.doozer.actions.";
        String actionClassName = actionName.substring(0, 1).toUpperCase()
                + actionName.substring(1);
        return (DoozerAction) Class.forName(actionClassPrefix +
                actionClassName).getConstructor(WebDriver.class, String.class,
                        String.class, String.class)
                .newInstance(driver, actionName, actionSelector, actionOptions);
    }

}
