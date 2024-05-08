package dev.softtest.doozer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import dev.softtest.doozer.Parser.ParserException;
import dev.softtest.doozer.actions.Click;
import dev.softtest.doozer.actions.Type;
import dev.softtest.doozer.actions.Url;
import dev.softtest.doozer.actions.Refresh;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;


import org.apache.logging.log4j.core.tools.picocli.CommandLine.ParameterException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import java.util.List;
import java.util.Map;

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

    @Test
    public void parse_action_with_selector_containing_inner_quotes() throws Exception {
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

    @Test
    public void parse_comment_line() throws Exception {
        path = "src/test/java/dev/softtest/doozer/scripts/commentTest.doozer";
        parser = new Parser(new Context(), path, driver);

        List<DoozerAction> actions = parser.parse();

        assertEquals(3, actions.size());
    }

    @Test
    public void parse_unknown_action_throws_exception() {
        // unknown "By.name('my-text')"
        String line = "unknown " + '"' + "By.name('my-text')" + '"';      
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.parseAction(1, line);
        });
        assertEquals("Action 'unknown' is not supported, in: '" + line + "'", thrown.getMessage());
    }

    @Test
    public void validate_missing_quote_selector() {
        String line = "click \"selector";
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.validate(line);
        });
        assertEquals("Missing quote - '\"' in: '" + line + "'", thrown.getMessage());
    }

    @Test
    public void validate_missing_quote_options() {
        String line = "click \"selector\" \"option1";
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.validate(line);
        });
        assertEquals("Missing quote - '\"' in: '" + line + "'", thrown.getMessage());
    }

    @Test
    public void validate_missing_escaped_quote() {
        String line = "click \"By.selector(\\" + "\"some-id)\"" + " \"option1\"";
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.validate(line);
        });
        assertEquals("Missing escaped quote - '\\" + "\"' in: '" + line + "'", thrown.getMessage());
    }

    @Test
    public void validate_extra_split_sequence() {
        String line = "click \"selector\" \"option1\" \"too-many!\"";      
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.validate(line);
        });
        assertEquals("Too many action parameters in: '" + line + "'", thrown.getMessage());

    }

    @Test
    public void validate_invalid_variable_string() {
        String line = "click \"${wrongVar\"";       
        ParserException thrown = assertThrows(ParserException.class, () -> {
            parser.validate(line);
        });
        assertEquals("Wrong variable syntax in: '" + line + "'", thrown.getMessage());
    }
    
    @Test
    public void validate_correct_line_only_action() throws ParameterException, ParserException {
        String line = "refresh";
        assertTrue(parser.validate(line));
    }

    @Test
    public void validate_correct_line_action_selector() throws ParameterException, ParserException {
        String line = "click \"selector\"";
        assertTrue(parser.validate(line));
    }

    @Test
    public void validate_correct_line_action_selector_options() throws ParameterException, ParserException {
        String line = "click \"selector\" \"option1\"";
        assertTrue(parser.validate(line));
    }


    @Test
    public void tokenize_action() {
        String in = "click";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(1, result.size());
    }

    @Test
    public void tokenize_action_selector() {
        String in = "click \"selector\"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(2, result.size());
    }


    @Test
    public void tokenize_action_selector_option() {
        String in = "click \"selector\" \"option\"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_action_empty_selector_option() {
        String in = "click \"\" \"option\"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_action_selector_with_spaces_option() {
        String in = "click \"  selector  \" \"option\"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_action_selector_with_spaces_option_with_spaces() {
        String in = "click \"  selector  \" \"   option   \"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }
    
    @Test
    public void tokenize_action_selector_option_with_spaces() {
        String in = "click \"selector\" \"   option   \"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_action_with_spaces_selector_with_spaces_option_with_spaces() {
        String in = "  click   \"  selector  \" \"   option   \"";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_action_selector_with_escaped_quotes() {
        String in = "click \"ab\\" + "\"bc";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(2, result.size());
    }

    @Test
    public void tokenize_action_selector_option_with_escaped_quotes() {
        String in = "click \"selector\" \"ab\\" + "\"bc";
        Map<String, String> result = parser.tokenize(in);
        assertEquals(3, result.size());
    }

    @Test
    public void tokenize_with_named_params_set() throws ParserException {
        String in = "set name:\"aaa\" value:\"bbb\"";
        Map<String, String> result = parser.tokenizeWithNamedParams(in);
        assertEquals(3, result.size());
        assertEquals("set", result.get("action"));
        assertEquals("aaa", result.get("name"));
        assertEquals("bbb", result.get("value"));
    }

    @Test
    public void tokenize_with_named_params_action() throws ParserException {
        String in = "click";
        Map<String, String> result = parser.tokenizeWithNamedParams(in);
        assertEquals(1, result.size());
        assertEquals("click", result.get("action"));
    }

    @Test
    public void tokenize_with_named_params_action_selector() throws ParserException {
        String in = "click selector:\"aaa\"";
        Map<String, String> result = parser.tokenizeWithNamedParams(in);
        assertEquals(2, result.size());
        assertEquals("click", result.get("action"));
        assertEquals("aaa", result.get("selector"));
    }

    @Test
    public void tokenize_with_named_params_action_selector_args() throws ParserException {
        String in = "click selector:\"aaa\" args:\"bbb\"";
        Map<String, String> result = parser.tokenizeWithNamedParams(in);
        assertEquals(3, result.size());
        assertEquals("click", result.get("action"));
        assertEquals("aaa", result.get("selector"));
        assertEquals("bbb", result.get("args"));
    }

    @Test
    public void use_named_params_set_true() {
        String in = "set name:\"aaa\" value:\"bbb\"";
        assertTrue(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_set_false() {
        String in = "set \"aaa\" \"bbb\"";
        assertFalse(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_only_false() {
        String in = "click";
        assertFalse(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_selector_true() {
        String in = "click selector:\"aaa\"";
        assertTrue(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_selector_false() {
        String in = "click \"aaa\"";
        assertFalse(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_args_true() {
        String in = "click args:\"aaa\"";
        assertTrue(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_selector_args_true() {
        String in = "click selector:\"aaa\" args:\"bbb\"";
        assertTrue(parser.useNamedParams(in));
    }

    @Test
    public void use_named_params_action_selector_args_false() {
        String in = "click \"aaa\" \"bbb\"";
        assertFalse(parser.useNamedParams(in));
    }


    @AfterAll
    public static void tearDown() {
        driver.quit();
    }
}
