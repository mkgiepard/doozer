package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import dev.softtest.doozer.actions.IAction;
import dev.softtest.doozer.actions.TakeScreenshot;
import dev.softtest.doozer.actions.Url;

import java.util.ArrayList;

public class DoozerTest {
    public static WebDriver driver;
    private static String doozerTestFile = "src/test/java/dev/softtest/doozer/scripts/firstTest.doozer";
    private static ArrayList<DoozerAction> actions = new ArrayList<DoozerAction>();
    private static final String splitterChar = " \"";

    @BeforeAll
    public static void setup() throws IOException {
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        Charset charset = StandardCharsets.UTF_8;
        Path path = FileSystems.getDefault().getPath(doozerTestFile).toAbsolutePath();

        try (BufferedReader reader = Files.newBufferedReader(path, charset)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(splitterChar);

                String action = null;
                String selector = null;
                String options = null;
                if (splitLine.length == 3) {
                    action = splitLine[0].replaceAll("\"", "");
                    selector = splitLine[1].replaceAll("\"", "");
                    options = splitLine[2].replaceAll("\"", "");
                } else if (splitLine.length == 2) {
                    action = splitLine[0].replaceAll("\"", "");
                    selector = splitLine[1].replaceAll("\"", "");
                } else if (splitLine.length == 1) {
                    action = splitLine[0].replaceAll("\"", "");
                }

                actions.add(new DoozerAction(action, selector, options));
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            throw e;
        }
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }

    @Test
    public void runner() throws Exception {
        for (DoozerAction action : actions) {

            System.out.println(action.toString());

            if (action.getActionName() == null || action.getActionName().length() == 0) {
                System.out.println("...empty line");
                continue;
            }

            switch (action.getActionName()) {
                case "url":
                    IAction urlAction = getActionInstanceForActionName("url", action.getSelector(),
                            action.getOptions());
                    urlAction.execute();
                    break;
                case "assertPageTitle":
                    IAction assertPageTitleAction = getActionInstanceForActionName("assertPageTitle",
                            action.getSelector(), action.getOptions());
                    assertPageTitleAction.execute();

                    break;
                case "type":
                    IAction typeAction = getActionInstanceForActionName("type", action.getSelector(),
                            action.getOptions());
                    typeAction.execute();
                    break;
                case "click":
                    IAction clickAction = getActionInstanceForActionName("click", action.getSelector(),
                            action.getOptions());
                    clickAction.execute();
                    break;
                case "assertInnerText":
                    IAction assertInnerTextAction = getActionInstanceForActionName("assertInnerText",
                            action.getSelector(), action.getOptions());
                    assertInnerTextAction.execute();
                    break;
                case "takeScreenshot":
                    IAction takeScreenshotAction = getActionInstanceForActionName("takeScreenshot",
                            action.getSelector(),
                            action.getOptions());
                    takeScreenshotAction.execute();
                    break;
                default:
                    throw new Exception("don't know this action: " + action.getActionName());
            }
        }
    }

    private IAction getActionInstanceForActionName(String actionName, String actionSelector, String actionOptions)
            throws Exception {
        String actionClassPrefix = "dev.softtest.doozer.actions.";
        String actionClassName = actionName.substring(0, 1).toUpperCase()
                + actionName.substring(1);
        return (IAction) Class.forName(actionClassPrefix +
                actionClassName).getConstructor(WebDriver.class,
                        String.class, String.class)
                .newInstance(driver, actionSelector, actionOptions);
    }

}
