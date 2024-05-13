package dev.softtest.doozer;

import java.util.Map;
import java.util.HashMap;

public class Context {
    private Map<String, String> variables;
    private String resultsDir;

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
} 
