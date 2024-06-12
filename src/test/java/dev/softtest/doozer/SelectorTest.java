package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import dev.softtest.doozer.DoozerSelector.SelectorParseException;

public class SelectorTest {
    
    @Test
    public void parse_by_selector() throws Exception {
        DoozerSelector sel = new DoozerSelector("By.name('my-name')");
        assertEquals("By.name: my-name", sel.getBySelector().toString());
    }

    @Test
    public void parse_by_selector_with_index() throws Exception {
        DoozerSelector sel = new DoozerSelector("By.name('my-name')[2]");
        assertEquals("By.name: my-name", sel.getBySelector().toString());
        assertEquals(2, sel.getIndex());
    }

    @Test
    public void parse_sl_selector() throws Exception {
        DoozerSelector sel = new DoozerSelector("SL({button 'my-button'})");
        assertEquals("BySemanticLocator: {button 'my-button'}", sel.getBySelector().toString());
    }

    @Test
    public void parse_sl_selector_with_index() throws Exception {
        DoozerSelector sel = new DoozerSelector("SL({button 'my-button'})[3]");
        assertEquals("BySemanticLocator: {button 'my-button'}", sel.getBySelector().toString());
        assertEquals(3, sel.getIndex());
    }

    @Test
    public void throws_exception_on_null_input() throws Exception {
        SelectorParseException thrown = assertThrows(SelectorParseException.class, () -> {
            new DoozerSelector(null);
        });
        assertEquals("Action requires 'By' selector or SemanticLocator (Sl) while got none.", thrown.getMessage());
    }

    @Test
    public void throws_exception_on_empty_input() throws Exception {
        SelectorParseException thrown = assertThrows(SelectorParseException.class, () -> {
            new DoozerSelector("");
        });
        assertEquals("Action requires 'By' selector or SemanticLocator (Sl) while got none.", thrown.getMessage());
    }

    @Test
    public void throws_exception_on_unknown_input() throws Exception {
        SelectorParseException thrown = assertThrows(SelectorParseException.class, () -> {
            new DoozerSelector("something");
        });
        assertEquals("Action requires 'By' selector or SemanticLocator (Sl) while got: 'something'.", thrown.getMessage());
    }

    @Test
    public void throws_exception_on_empty_index() throws Exception {
        SelectorParseException thrown = assertThrows(SelectorParseException.class, () -> {
            new DoozerSelector("SL({button 'my-button'})[]");
        });
        assertEquals("Index part of the selector is malformed, expected '[%d]', got: ''.", thrown.getMessage());
    }

}
