package dev.softtest.doozer;

import java.util.Map;
import java.util.HashMap;

public class Context {
    private Map<String, String> variables;

    public Context() {
        variables = new HashMap<String, String>();
    }

    public void setVariable(String name, String value) {
        variables.put(name, value);
    }

    public String getVariable(String name) {
        return variables.getOrDefault(name, null);
    }
} 
