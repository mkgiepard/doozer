package dev.softtest.doozer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import dev.softtest.doozer.actions.Click;
import dev.softtest.doozer.actions.Type;
import dev.softtest.doozer.actions.Url;
import dev.softtest.doozer.actions.Refresh;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ParserTest {
    public static WebDriver driver = new ChromeDriver();
    public static String path;
    public static Parser parser;

    @BeforeAll
    public static void setup() {
        path = "/some/path";

        parser = new Parser(new Context(), path, driver);
    }

    @Test
    public void parse_refresh_action_without_selector_and_options() throws Exception {
        // refresh
        String line = "refresh";

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Refresh.class, action.getClass());
        assertEquals("refresh", action.getActionName());
        assertEquals(null, action.getSelector());
        assertEquals(0, action.getOptions().size());
    }

    @Test
    public void parse_click_action_without_options() throws Exception {
        // click "By.id('someid')"
        String line = "click " + '"' + "By.id('someid')" + '"' +  ' ' + '"' + '"';

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Click.class, action.getClass());
        assertEquals("click", action.getActionName());
        assertEquals("By.id('someid')", action.getSelector());
        assertEquals(0, action.getOptions().size());
    }
    
    @Test
    public void parse_url_action_without_selector() throws Exception {
        // url "" "www.bing.com"
        String line = "url " + '"' + '"' + ' ' + '"' + "www.bing.com" + '"';

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Url.class, action.getClass());
        assertEquals("url", action.getActionName());
        assertEquals("", action.getSelector());
        assertEquals(1, action.getOptions().size());
        assertEquals("www.bing.com", action.getOptions().get("default"));
    }

    @Test
    public void parse_type_action() throws Exception {
        // type "By.name('my-text')" "text=Selenium"
        String line = "type " + '"' + "By.name('my-text')" + '"' + ' ' + '"' + "text=Selenium" + '"';

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Type.class, action.getClass());
        assertEquals("type", action.getActionName());
        assertEquals("By.name('my-text')", action.getSelector());
        assertEquals(1, action.getOptions().size());
        assertEquals("Selenium", action.getOptions().get("text"));
    }

    @Test
    public void parse_type_action_with_additional_spaces() throws Exception {
        //  type   "By.name('my-text')"    "text=Selenium"   
        String line = " type   " + '"' + "   By.name('my-text')" + '"' + "   " + '"' + "   text=Selenium   " + '"';

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Type.class, action.getClass());
        assertEquals("type", action.getActionName());
        assertEquals("By.name('my-text')", action.getSelector());
        assertEquals(1, action.getOptions().size());
        assertEquals("Selenium", action.getOptions().get("text"));
    }

    @Test
    public void parse_type_action_with_optional_parameter() throws Exception {
        // type? "By.name('my-text')" "text=Selenium"
        String line = "type? " + '"' + "By.name('my-text')" + '"' + ' ' + '"' + "text=Selenium" + '"';

        DoozerAction action = parser.parseAction(1, line);
        
        assertEquals(Type.class, action.getClass());
        assertEquals("type", action.getActionName());
        assertEquals("By.name('my-text')", action.getSelector());
        assertEquals(1, action.getOptions().size());
        assertEquals("Selenium", action.getOptions().get("text"));
        assertEquals(true, action.isOptional());
    }

    @Test
    public void parse_firstTest_file() throws Exception {
        path = "src/test/java/dev/softtest/doozer/scripts/firstTest.doozer";
        parser = new Parser(new Context(), path, driver);

        List<DoozerAction> actions = parser.parse();

        assertEquals(7, actions.size());
        assertEquals("url", actions.get(0).getActionName());
        assertEquals(1, actions.get(0).getLineNumber());
        assertEquals("takeScreenshot", actions.get(6).getActionName());
        assertEquals(8, actions.get(6).getLineNumber());
    }

    @Test void parse_selector_with_xpath() throws Exception {
        String selector = "By.xpath('//*[contains(string()," + '"' + "Accept all" + '"' + ")]')";
        String click = "click " + '"' + selector + '"';

        DoozerAction action = parser.parseAction(1, click);

        assertEquals(selector, action.getSelector());
    }

    @Test void parse_action_with_selector_containing_inner_quotes() throws Exception {
        // click "By.xpath('//button[contains(., \"Accept all\")]')" "some-option"
        // selectorInAction: By.xpath('//button[contains(., \"Accept all\")]')
        // selectorParsed: By.xpath('//button[contains(., "Accept all")]')
        String selectorInAction = "By.xpath('//button[contains(., \\" + '"' + "Accept all\\" + '"' + ")]')";
        String selectorParsed = "By.xpath('//button[contains(., " + '"' + "Accept all" + '"' + ")]')";
        String click = "click " + '"' + selectorInAction + '"' + " " + '"' + "some-option" + '"';

        DoozerAction action = parser.parseAction(1, click);

        assertEquals(selectorParsed, action.getSelector());
        assertEquals(1, action.getOptions().size());
        assertEquals("some-option", action.getOptions().get("default"));
    }

    @Test void parse_comment_line() throws Exception {
        path = "src/test/java/dev/softtest/doozer/scripts/commentTest.doozer";
        parser = new Parser(new Context(), path, driver);

        List<DoozerAction> actions = parser.parse();

        assertEquals(3, actions.size());
    }

    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
