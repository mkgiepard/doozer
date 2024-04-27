package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Map;

public class OptionParserTest {
    // in:                              out:
    // null                             null
    // ''                               {}
    // option                           {default: option}
    // key1=value1,key2=value2          {key1:value1, key2=value2}
    // key1=val\,ue1,key2=value2        {key1:value1, key2=value2}
    // key1=vale\=ue1,key2=value2       {key1:value1, key2=value2}
    
    @Test
    public void parse_null_string_returns_null() throws Exception {
        OptionParser parser = new OptionParser(null);
        assertNull(parser.parse());
    }

    @Test
    public void parse_empty_string_returns_empty_map() throws Exception {
        OptionParser parser = new OptionParser("");
        assertEquals(0, parser.parse().size());
    }

    @Test
    public void parse_single_option_returns_one_element_map_with_default_key() throws Exception {
        OptionParser parser = new OptionParser("option");
        Map<String, String> result = parser.parse();

        assertEquals(1, result.size());
        assertTrue(result.containsKey("default"));
        assertEquals("option", result.get("default"));
    }


    @Test
    public void parse_single_option_returns_one_element_map_with_option_with_comma() throws Exception {
        String testString = "opt\\" + ",ion";
        OptionParser parser = new OptionParser(testString);
        Map<String, String> result = parser.parse();

        assertEquals(1, result.size());
        assertTrue(result.containsKey("default"));
        assertEquals("opt,ion", result.get("default"));
    }

    @Test
    public void parse_single_option_returns_one_element_map_with_option_with_equals() throws Exception {
        String testString = "opt\\" + "=ion";
        OptionParser parser = new OptionParser(testString);
        Map<String, String> result = parser.parse();

        assertEquals(1, result.size());
        assertTrue(result.containsKey("default"));
        assertEquals("opt=ion", result.get("default"));
    }

    @Test
    public void parse_two_options_returns_two_element_map() throws Exception {
        OptionParser parser = new OptionParser("key1=value1,key2=value2");
        Map<String, String> result = parser.parse();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("key1"));
        assertTrue(result.containsKey("key2"));
        assertEquals("value1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }


    @Test
    public void parse_two_options_returns_two_element_map_with_option_with_comma() throws Exception {
        String testString = "val\\" + ",ue1";
        OptionParser parser = new OptionParser("key1=" + testString + ",key2=value2");
        Map<String, String> result = parser.parse();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("key1"));
        assertTrue(result.containsKey("key2"));
        assertEquals("val,ue1", result.get("key1"));
        assertEquals("value2", result.get("key2"));
    }

    @Test
    public void parse_two_options_returns_two_element_map_with_option_with_equals() throws Exception {
        String testString = "val\\" + "=ue2";
        OptionParser parser = new OptionParser("key1=value1,key2=" + testString);
        Map<String, String> result = parser.parse();

        assertEquals(2, result.size());
        assertTrue(result.containsKey("key1"));
        assertTrue(result.containsKey("key2"));
        assertEquals("value1", result.get("key1"));
        assertEquals("val=ue2", result.get("key2"));
    }
}
