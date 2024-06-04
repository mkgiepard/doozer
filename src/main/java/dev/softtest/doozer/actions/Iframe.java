package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class Iframe extends DoozerAction {

    public Iframe(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement iframe = getDriver().findElement(getBySelector(getSelector()));
        getDriver().switchTo().frame(iframe);
    }
    
}
