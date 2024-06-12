package dev.softtest.doozer;

import org.openqa.selenium.WebDriver;
import java.util.Map;
import java.util.HashMap;

public class Context {
    private Map<String, String> variables;
    private String resultsDir;
    private DoozerDriver doozerDriver;

    public Context() {
        variables = new HashMap<String, String>();
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariable(String name) {
        return variables.getOrDefault(name, null);
    }

    public void setResultsDir(String dir) {
        resultsDir = dir;
    }

    public String getResultsDir() {
        return resultsDir;
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
} 
