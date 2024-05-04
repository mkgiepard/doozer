package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class AssertInnerText extends DoozerAction {

    public AssertInnerText(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement message = getDriver().findElement(getBySelector(getSelector()));
        String value = message.getText();
        if (!value.equals(getOptions().get("default")))
            throw new Exception("The innerText content is different!\nExpected: " + getOptions() + "\nIs: " + value);
    }

}
