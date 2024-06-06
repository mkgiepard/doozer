package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.google.common.base.Strings;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;

public class HoverByOffset extends DoozerAction {

    public HoverByOffset(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        if (Strings.isNullOrEmpty(getOption("x")) || Strings.isNullOrEmpty(getOption("y"))) {
            throw new Exception("Missing x, y parameters");
        }
        WebElement element = getDriver().findElement(getBySelector());
        new Actions(getDriver()).moveToElement(
                element,
                Integer.parseInt(getOption("x")),
                Integer.parseInt(getOption("y")));
    }
}
