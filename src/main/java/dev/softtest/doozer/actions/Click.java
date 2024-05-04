package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class Click extends DoozerAction {

    public Click(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement submitButton = getDriver().findElement(getBySelector(getSelector()));
        submitButton.click();
    }
}
