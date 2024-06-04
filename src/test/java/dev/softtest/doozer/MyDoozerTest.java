package dev.softtest.doozer;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;

public class MyDoozerTest extends DoozerTest {

  @Override
  public void setupWindow(WebDriver driver) {
    driver.manage().window().setSize(new Dimension(1600, 1200));
  }
}
