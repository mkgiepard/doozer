package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
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
                    new Url(driver, action.getOptions()).execute();
                    break;
                case "assertPageTitle":
                    assertPageTitle(action.getOptions());
                    break;
                case "type":
                    type(action.getOptions());
                    break;
                case "click":
                    click();
                    break;
                case "assertInnerText":
                    assertInnerText(action.getOptions());
                    break;
                default:
                    throw new Exception("don't know this action: " + action.getActionName());
            }
        }
    }

    private void assertPageTitle(String title) {
        String pageTitle = driver.getTitle();
        assertEquals(title, pageTitle);
    }

    private void type(String text) {
        WebElement textBox = driver.findElement(By.name("my-text"));
        textBox.sendKeys(text);
    }

    private void click() {
        WebElement submitButton = driver.findElement(By.cssSelector("button"));
        submitButton.click();
    }

    private void assertInnerText(String text) {
        WebElement message = driver.findElement(By.id("message"));
        String value = message.getText();
        assertEquals(text, value);
    }

}
