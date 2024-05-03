package dev.softtest.doozer.actions;

import org.openqa.selenium.interactions.Actions;
import dev.softtest.doozer.DoozerAction;

public class KeyUp extends DoozerAction {

    public KeyUp(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        new Actions(getDriver()).keyUp(getOption("default"));
    }
    
}
