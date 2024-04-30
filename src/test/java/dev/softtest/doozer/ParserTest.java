package dev.softtest.doozer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import dev.softtest.doozer.actions.Click;
import dev.softtest.doozer.actions.Type;
import dev.softtest.doozer.actions.Url;
import dev.softtest.doozer.actions.Refresh;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParserTest {
    public static WebDriver driver = new ChromeDriver();
    public static String path;
    public static Parser parser;

    @BeforeAll
    public static void setup() {
        // this.driver = new ChromeDriver();
        path = "/some/path";
        parser = new Parser(path, driver);
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



    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}