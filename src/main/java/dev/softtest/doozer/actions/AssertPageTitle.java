package dev.softtest.doozer.actions;

import dev.softtest.doozer.DoozerAction;

public class AssertPageTitle extends DoozerAction {

    public AssertPageTitle(Integer lineNumber, String actionName, String originalAction) {
        super(lineNumber, actionName, originalAction);
    }

    @Override
    public void execute() throws Exception {
        String pageTitle = getDriver().getTitle();
        if (!pageTitle.equals(getOptions().get("default")))
            throw new Exception("The pageTitle content is different!\nExpected: " + getOptions() + "\nIs: " + pageTitle);
    }

}
