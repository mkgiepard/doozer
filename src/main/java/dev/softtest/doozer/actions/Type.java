package dev.softtest.doozer.actions;

import org.openqa.selenium.WebElement;
import dev.softtest.doozer.DoozerAction;

public class Type extends DoozerAction {

    public Type(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        WebElement textBox = getDriver().findElement(getBySelector(getSelector()));
        textBox.sendKeys(getOptions().getOrDefault("default", ""));
    }

}
