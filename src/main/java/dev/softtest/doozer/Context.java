package dev.softtest.doozer;

import org.openqa.selenium.WebDriver;
import java.nio.file.Path;
import java.util.Map;
import java.util.HashMap;

public class Context {
    private Map<String, String> variables;
    private DoozerDriver doozerDriver;
    private Path testResultPath;
    private Path testRootPath;

    public Context() {
        variables = new HashMap<String, String>();
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariable(String name) {
        return variables.getOrDefault(name, null);
    }

    public void setTestResultPath(Path testResultPath) {
        this.testResultPath = testResultPath;
    }

    public Path getTestResultPath() {
        return testResultPath;
    }

    public void setDoozerDriver(DoozerDriver driver) {
        this.doozerDriver = driver;
    }

    public DoozerDriver getDoozerDriver() {
        return doozerDriver;
    }

    public WebDriver getWebDriver() {
        return doozerDriver.getDriver();
    }

    public void setTestRootPath(Path testRootPath) {
        this.testRootPath = testRootPath;
    }

    public Path getTestRootPath() {
        return testRootPath;
    }

} 
