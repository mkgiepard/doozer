package dev.softtest.doozer.actions;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements switching between <a href="https://www.selenium.dev/documentation/webdriver/interactions/frames/">
 * Iframes</a>.
 * 
 * <ul>
 *   <li>By WebElement, if selector is defined</li>
 *   <li>By Iframe name, name must be given in action args.</li>
 *   <li>By Iframe index, idex must be given in action args.</li>
 * </ul>
 */
public class Iframe extends DoozerAction {

    public Iframe(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        if (getSelector() != null && getSelector() != "") {
            getDriver().switchTo().frame(ElementFinder.findElement(getContext(), getDoozerSelector()));
        }
        if (getOption("name") != "") {
            getDriver().switchTo().frame(getOption("name"));
        }
        if (getOption("index") != "") {
            getDriver().switchTo().frame(Integer.parseInt(getOption("index")));
        }
        getDriver().switchTo().defaultContent();
    }
    
}
