package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.SetSystemProperty;

public class DoozerTestTest {

    @Test
    @ClearSystemProperty(key = "doozer.test")
    @ClearSystemProperty(key = "doozer.directory")
    public void provideDoozerTestFiles_returns_empty_stream_of_arguments_wo_doozer_parameters(){
        DoozerTest dt = new DoozerTest();
        assertEquals(1, dt.provideDoozerTestFiles().toList().size());
        assertEquals(0, dt.provideDoozerTestFiles().toList().get(0).get().length);
    }

    @Test
    @ClearSystemProperty(key = "doozer.directory")
    @SetSystemProperty(key = "doozer.test", value = "not/existing/path/test.doozer")
    public void provideDoozerTestFiles_throws_exception_w_not_existing_doozer_test(){
        DoozerTest dt = new DoozerTest();
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> dt.provideDoozerTestFiles());
        assertEquals("'not/existing/path/test.doozer' must exist to proceed.", thrown.getMessage());
    }

    @Test
    @ClearSystemProperty(key = "doozer.test")
    @SetSystemProperty(key = "doozer.directory", value = "not/existing/path/")
    public void provideDoozerTestFiles_throws_exception_w_not_existing_doozer_directory(){
        DoozerTest dt = new DoozerTest();
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> dt.provideDoozerTestFiles());
        assertEquals("'not/existing/path/' must exist to proceed.", thrown.getMessage());
    }

}
