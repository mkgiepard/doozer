package dev.softtest.doozer;

import java.util.stream.*;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

public class MyDoozerTest extends DoozerTest {

  @Override
  public void setupWindow(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(1920, 1600));
  }

  @Override
  public Stream<Arguments> provideDoozerTestFiles() {
    String testFolder = System.getProperty("test.folder");
    return Stream.of(
        Arguments.of(testFolder + "firstTest/firstTest.doozer"),
        Arguments.of(testFolder + "secondTest/secondTest.doozer"),
        Arguments.of(testFolder + "googleTest/googleTest.doozer"),
        Arguments.of(testFolder + "commentTest/commentTest.doozer"),
        Arguments.of(testFolder + "importTest/importTest.doozer"),
        Arguments.of(testFolder + "softtestTest/softtestTest.doozer"));

  }
}
