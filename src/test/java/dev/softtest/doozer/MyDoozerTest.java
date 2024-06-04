package dev.softtest.doozer;

import java.util.stream.*;
import org.junit.jupiter.params.provider.Arguments;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

public class MyDoozerTest extends DoozerTest {

  @Override
  public void setupWindow(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(1600, 1200));
  }

  @Override
  public Stream<Arguments> provideDoozerTestFiles() {
    String directory = System.getProperty("doozer.directory");
    String test = System.getProperty("doozer.test");
    if (test != null) {
      return Stream.of(Arguments.of(test));
    }
    return Stream.of(
        Arguments.of(directory + "firstTest/firstTest.doozer"),
        Arguments.of(directory + "secondTest/secondTest.doozer"),
        Arguments.of(directory + "googleTest/googleTest.doozer"),
        Arguments.of(directory + "commentTest/commentTest.doozer"),
        Arguments.of(directory + "importTest/importTest.doozer"),
        Arguments.of(directory + "softtestTest/softtestTest.doozer"));

  }
}
