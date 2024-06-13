package dev.softtest.doozer;

import java.time.Duration;
import java.util.List;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

/** Looks for UI elements on a web page. */
public class ElementFinder {
    public static final Duration DEFAULT_WAIT_TIMEOUT = Duration.ofSeconds(10);
    private static final Logger LOG = LogManager.getLogger();

    public static WebElement findElement(Context ctx, DoozerSelector selector) throws Exception {
        List<WebElement> list = new ArrayList<>();

        org.openqa.selenium.support.ui.Wait<WebDriver> wait =
            new FluentWait<>(ctx.getWebDriver())
                .withTimeout(DEFAULT_WAIT_TIMEOUT)
                .pollingEvery(Duration.ofMillis(500))
                .ignoring(ElementNotInteractableException.class, NoSuchElementException.class);
        try {
            wait.until((d) -> {
                LOG.info("looking for element: '" + selector.getSelectorDesc() + "'");
                List<WebElement> elements = ctx.getWebDriver().findElements(selector.getBySelector());
                if (selector.getIndex() >= 0 && selector.getIndex() < elements.size()) {
                    WebElement element = elements.get(selector.getIndex());
                    if (element != null && element.isDisplayed()) {
                        list.add(0, element);
                        return true;
                    }
                }
                return false;
            });
        } catch (TimeoutException e) {
            throw new Exception("Cannot find displayed element for selector: " + selector + "\n" + e);
        }
        if (list.size() == 0)
            throw new Exception("Cannot find displayed element for selector: " + selector);
        return list.get(0);
    }
}
