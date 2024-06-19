package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import com.google.common.base.Strings;

import dev.softtest.doozer.Context;
import dev.softtest.doozer.DoozerAction;
import dev.softtest.doozer.ElementFinder;

/**
 * Implements <a href="https://www.selenium.dev/documentation/webdriver/actions_api/mouse/#move-by-offset">
 * Move to element by offset (aka hover by offset)</a> interaction.
 */
public class HoverByOffset extends DoozerAction {

    public HoverByOffset(Context ctx, Integer lineNumber, String actionName, String originalAction) {
        super(ctx, lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        if (Strings.isNullOrEmpty(getOption("x")) || Strings.isNullOrEmpty(getOption("y"))) {
            throw new Exception("Missing x, y parameters");
        }
        WebElement element = ElementFinder.findElement(getContext(), getDoozerSelector());
        new Actions(getDriver()).moveToElement(
                element,
                Integer.parseInt(getOption("x")),
                Integer.parseInt(getOption("y")));
    }
}
