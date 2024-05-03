package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import dev.softtest.doozer.DoozerAction;

public class DoubleClick extends DoozerAction {

    public DoubleClick(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement element = getDriver().findElement(getBySelector(getSelector()));
        new Actions(getDriver()).doubleClick(element);      
    }
}
