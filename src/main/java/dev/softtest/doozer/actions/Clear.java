package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class Clear extends DoozerAction {

    public Clear(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }
    
    @Override
    public void execute() throws Exception {
        WebElement textBox = getDriver().findElement(getBySelector());
        textBox.clear();
    }

}
