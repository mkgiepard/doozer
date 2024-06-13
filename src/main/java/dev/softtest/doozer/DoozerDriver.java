package dev.softtest.doozer;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import com.google.common.base.Strings;
import org.openqa.selenium.WebDriver;


/** Wrapper class for various WebDriver implementations (Chrome, Firefox, Edge and Safari). */
public class DoozerDriver {
    private static final String DEFAULT_BROWSER = "chrome-headless";
    private final String browserDesc;
    private WebDriver driver;

    public DoozerDriver(String browserParam) {
        this.browserDesc = Strings.isNullOrEmpty(browserParam) ? DEFAULT_BROWSER : browserParam;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getBrowserDesc() {
        return browserDesc;
    }

    /** Creates a WebDriver instance base on the <code>browserParam</code> set in the constructor. */
    public void init() {
        String[] driverData = browserDesc.split("-");
        switch(driverData[0].toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                if (driverData.length == 2 && driverData[1].equals("headless")) {
                    chromeOptions.addArguments("--headless=new");
                }
                chromeOptions.addArguments("--force-device-scale-factor=1");
                driver = new ChromeDriver(chromeOptions);
                break;
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (driverData.length == 2 && driverData[1].equals("headless")) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;
            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (driverData.length == 2 && driverData[1].equals("headless")) {
                    edgeOptions.addArguments("--headless=new");
                }
                edgeOptions.addArguments("--force-device-scale-factor=1");
                driver = new EdgeDriver(edgeOptions);
                break;
            case "safari":
                SafariOptions safariOptions = new SafariOptions();
                driver = new SafariDriver(safariOptions);
                break;
            default:
                ChromeOptions defaultOptions = new ChromeOptions();
                defaultOptions.addArguments("--headless=new");
                defaultOptions.addArguments("--force-device-scale-factor=1");
                driver = new ChromeDriver(defaultOptions);
        }
    }
}
