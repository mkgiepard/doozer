package dev.softtest.doozer;

import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

import com.google.common.base.Strings;
import com.google.semanticlocators.BySemanticLocator;

public class DoozerSelector {
    protected static final Logger LOG = LogManager.getLogger();

    private String selectorDesc;
    private By bySelector;
    private int index;

    public DoozerSelector(String selector) throws Exception {
        this.selectorDesc = selector;
        parse();
    }

    public By getBySelector() {
        return bySelector;
    }

    public int getIndex() {
        return index;
    }

    public String getSelectorDesc() {
        return selectorDesc;
    }

    private void parse() throws Exception {
        // Selector format examples:
        // - By.cssSelector('button')
        // - By.name('my-text')
        // - By.name('my-text')[2]
        // - SL({button 'Next'})
        // - SL({button 'Next'})[2]
        if (Strings.isNullOrEmpty(selectorDesc))
            throw new SelectorParseException("Action requires 'By' selector or SemanticLocator (Sl) while got none.");

        if (!(selectorDesc.startsWith("By.") || selectorDesc.startsWith("Sl(") || selectorDesc.startsWith("SL(")))
            throw new SelectorParseException("Action requires 'By' selector or SemanticLocator (Sl) while got: '" + selectorDesc + "'.");

        String s =  selectorDesc;
        if (selectorDesc.endsWith("]")) {
            String indexSubString = selectorDesc.substring(selectorDesc.lastIndexOf("[") + 1, selectorDesc.length() - 1);
            try {
                index = Integer.parseInt(indexSubString);
            } catch (NumberFormatException e) {
                throw new SelectorParseException("Index part of the selector is malformed, expected '[%d]', got: '" + indexSubString + "'.");
            }
            s =  selectorDesc.substring(0, selectorDesc.lastIndexOf("["));
        }
        
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
        bySelector = result;
    }

    public class SelectorParseException extends Exception {
        public SelectorParseException(String errorMessage) {
            super(errorMessage);
            LOG.error(errorMessage);
        }
    }

}
