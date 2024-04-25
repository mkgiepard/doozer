package dev.softtest.doozer;

import java.util.stream.*;
import org.junit.jupiter.params.provider.Arguments;

public class MyDoozerTest extends DoozerTest {
    
    @Override
    protected Stream<Arguments> provideDoozerTestFiles() {
        String testFolder = System.getProperty("test.folder");
        return Stream.of(
          Arguments.of(testFolder + "firstTest.doozer"),
          Arguments.of(testFolder + "secondTest.doozer"),
          Arguments.of(testFolder + "googleTest.doozer")
        );

    }
}
