package dev.softtest.doozer;

import java.util.Map;

import org.openqa.selenium.WebDriver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Context {
    private Map<String, String> variables;
    private String resultsDir;
    private DoozerDriver doozerDriver;
    private List<DoozerAction> actions = new ArrayList<DoozerAction>();

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

    public void setActions(List<DoozerAction> actions) {
        this.actions = actions;
    }

    public List<DoozerAction> getActions() {
        return actions;
    }

    public WebDriver getWebDriver() {
        return doozerDriver.getDriver();
    }
} 
