package dev.softtest.doozer;

import dev.softtest.doozer.actions.Action;

import com.google.common.base.Strings;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.openqa.selenium.WebDriver;

public class DoozerAction implements Action {
    protected static final Logger logger = LogManager.getLogger();
    
    private final Context ctx;
    private final Integer lineNumber;
    private final String actionName;
    private final String originalAction;
    private String sourceFileName;

    private String selector;
    private Map<String, String> options;
    private boolean isOptional = false;
    private DoozerSelector doozerSelector;
    
    public DoozerAction(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        this.ctx = ctx;
        this.lineNumber = lineNumber;
        this.actionName = actionName;
        this.originalAction = originalAction;
    }

    public Context getContext() {
        return ctx;
    }

    public String getActionName() {
        return actionName;
    }

    public String getOriginalAction() {
        return originalAction;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public String getSelector() {
        return selector;
    }

    public WebDriver getDriver() {
        return getContext().getWebDriver();
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getOption(String key) {
        return options.getOrDefault(key, "");
    }

    public Boolean isOptional() {
        return isOptional;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }
    public DoozerSelector getDoozerSelector() {
        return doozerSelector;
    }

    public void setSelector(String selector) throws Exception {
        this.selector = selector;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }

    public void setDoozerSelector() throws Exception {
        doozerSelector = new DoozerSelector(selector);
    }


    public void resolveVariables() throws Exception {
        if (this.selector != null && this.selector.contains("${")) {
            String varName = this.selector.substring(this.selector.indexOf("${")+2, this.selector.indexOf("}"));
            setSelector(getContext().getVariable(varName));
            logger.info("resolveVariable: '${" + varName + "}' ==> '" + getContext().getVariable(varName) + "'"); 
        }
        for (String key : getOptions().keySet()) {
            if (this.options.get(key).contains("${")) {
                String value = this.options.get(key);
                String varName = value.substring(value.indexOf("${")+2, value.indexOf("}"));
                this.options.put(key, getContext().getVariable(varName));
                logger.info("resolveVariable: '${" + varName + "}' ==> '" + getContext().getVariable(varName) + "'");
            }
        }
    }

    public void resolveDoozerSelector() throws Exception {
        if (!Strings.isNullOrEmpty(selector))
            doozerSelector = new DoozerSelector(selector);
    }

    public void execute() throws Exception {
        throw new Exception("!!! Should never be called !!!");
    }

    @Override
    public String toString() {
        return Integer.toString(lineNumber) + ": " + "actionName: " + actionName + "\n"
                + "selector: " + selector + "\n"
                + "options: " + options + "\n"
                + "isOptional: " + isOptional + "\n";
    }

}
