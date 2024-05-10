package dev.softtest.doozer;

import java.lang.reflect.Method;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.google.common.base.Strings;
import com.google.semanticlocators.BySemanticLocator;

import dev.softtest.doozer.actions.Action;

public class DoozerAction implements Action {
    protected static final Logger logger = LogManager.getLogger();
    
    private final Context ctx;
    private final Integer lineNumber;
    private final String actionName;
    private final String originalAction;

    private WebDriver driver;
    private String selector;
    private Map<String, String> options;
    private boolean isOptional = false;
    
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

    public String getSelector() {
        return selector;
    }

    public WebDriver getDriver() {
        return driver;
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

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public void setIsOptional(Boolean isOptional) {
        this.isOptional = isOptional;
    }


    public void resolveVariables() {
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

    public By getBySelector(String s) throws Exception {
        // Selector format examples:
        // - By.cssSelector('button')
        // - By.name('my-text')
        if (Strings.isNullOrEmpty(s))
            throw new Exception("Action requires 'By' selector or SemanticLocator (Sl) while got none.");

        if (!(s.startsWith("By.") || s.startsWith("Sl(") || s.startsWith("SL(")))
            throw new Exception("Action requires 'By' selector or SemanticLocator (Sl) while got: '" + s + "'");

        By result = null;
        if (s.startsWith("By.")) {
            String methodName = s.substring(s.indexOf(".") + 1, s.indexOf("("));
            String methodParam = s.substring(s.indexOf("'") + 1, s.length() - 2);

            Method bySelectorMethod = By.class.getDeclaredMethod(methodName, String.class);
            result = (By) bySelectorMethod.invoke(null, methodParam);
        }
        if (s.startsWith("Sl(") || s.startsWith("SL(")) {
            String methodParam = s.substring(s.indexOf("(") + 1, s.length() - 1);
            result = new BySemanticLocator(methodParam);
        }
        return result;
    }
}
