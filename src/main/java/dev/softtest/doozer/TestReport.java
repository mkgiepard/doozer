package dev.softtest.doozer;

import java.util.*;
import java.nio.file.*;

import j2html.tags.ContainerTag;
import j2html.tags.specialized.DivTag;
import j2html.tags.specialized.LiTag;
import j2html.tags.specialized.LinkTag;

import static j2html.TagCreator.*;

public class TestReport {
    Map<DoozerAction, Map<String, String>> data = new HashMap<>();

    // action - result - artifacts

    // artefacts:
    // -- type: screenshot
    // -- path to golden img
    // -- path to result img
    // -- # of pixel diff
    // -- % of pixel diff
    // -- pixel diff threshold

    public String generateHtmlReport(TestCase tc) {
        return ul(tc.getTestSteps()
                .stream()
                .map(step -> getAction(step, tc.getTestCaseName()))
                .toArray(ContainerTag[]::new))
                .renderFormatted();
    }

    public String generateReport() {
        return body(
            h1("Hello, World!"),
            img().withSrc("/img/hello.png")
        ).render(); 
    }

    public String includeCSS() {
        String materialFontLink = link()
            .withRel("stylesheet")
            .withHref("https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0")
            .render();
        String css = style(readCssFromFile("src/main/resources/styles.css")).render();
        return materialFontLink + css;
    }

    public String includeJS() {
        return script(readScriptFromFile("src/main/resources/script.js")).render();
    }

    public String includeHeader() {
        return div(h1("Doozer Test Report")).withClass("header").renderFormatted();
    }

    public String getHeader() {
        return div(div(join(
            div("Test Case"),
            div("STATUS").withClass("center"),
            div("RESULT").withClass("center"),
            div("PIXEL DIFF").withClass("center"),
            div("ACTION").withClass("center")
        )).withClasses("container-testcase-header", "title")).withClass("container-testcase").render();
    }

    public String getTestCaseHeader(TestCase tc) {
        String script = tc.getTestScriptPath();
        String result = tc.getTestResult().toString();
        String diff = "0";
        String id = tc.getTestCaseName();
        TestStep failing = null;
        String actionText = "";
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)) {
                actionText = step.getAction().getLineNumber()
                    + ": "
                    + step.getAction().getOriginalAction();
                failing = step;
                if (step.getArtifact() != null && step.getArtifact().getDiff() != 0 ) {
                    diff = Long.toString(step.getArtifact().getDiff());
                    id += step.getAction().getLineNumber();
                }
            }
        }
        String resultIcon = tc.getTestResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = tc.getTestResult() == TestResult.PASS ? "pass" : "fail";
        String buttonHidden = tc.getTestResult() == TestResult.PASS || diff == "0" ? "hidden" : "";
        
        return div(join(
            div(join(
                div(script).withClass("testcase-name"),
                div(actionText).withClasses("step-name", "hidden").withId(id+"step")
            )),
            div(result).withClass("center"),
            div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center", resultStyle),
            div(diff).withClass("center"),
            div(button("APPROVE")).withClasses("center", buttonHidden)
        )).withClass("container-testcase-header").attr("onclick", "toggleDisplay('" + id + "')").renderFormatted();

    }

    public String getTestCaseImages(TestCase tc) {
        TestStep lastStep = tc.getTestSteps().get(tc.getTestSteps().size() - 1);

        if (lastStep.getResult() == TestResult.FAIL && lastStep.getArtifact() != null) {
            String id = tc.getTestCaseName() + lastStep.getAction().getLineNumber();
            String screenshotName = lastStep.getAction().getOptions().get("default");
            if (screenshotName == null) {
                screenshotName = lastStep.getAction().getOptions().getOrDefault("fileName",
                        "screenshot-" + lastStep.getAction().getLineNumber());
            }
            return getContainerTestCaseImages(lastStep.getArtifact(), screenshotName, id).render();
        }
        return "";
    }

    private LiTag getAction(TestStep step, String tcName) {
        String id = tcName + step.getAction().getLineNumber();

        String actionText = step.getAction().getLineNumber()
                + ": "
                + step.getAction().getOriginalAction();

        if (step.getArtifact() != null) {
            String screenshotName = step.getAction().getOptions().get("default");
            if (screenshotName == null) {
                screenshotName = step.getAction().getOptions().getOrDefault("fileName",
                        "screenshot-" + step.getAction().getLineNumber());
            }

            return li(join(actionText, getContainerTestCaseImages(step.getArtifact(), screenshotName, id)));
        }
        return li(actionText);
    }

    private DivTag getContainerTestCaseImages(TestArtifact ta, String screenshotName, String id) {
        return div(join(
            div(img().withSrc("../../" + ta.getGoldenPath()+screenshotName+".png")).withClass("card"),
            div(img().withSrc("." + ta.getResultPath().substring("target/doozer-tests/".length())+screenshotName + ".DIFF.png")).withClass("card"),
            div(img().withSrc("." + ta.getResultPath().substring("target/doozer-tests/".length())+screenshotName + ".png")).withClass("card")
        )).withClasses("container-testcase-images", "hidden").withId(id);

    }

    private String readCssFromFile(String cssFilePath) {
        String styles = "";
        try {
            styles = new String(
                    Files.readAllBytes(Paths.get(cssFilePath)));
        } catch (Exception e) {
            System.out.println(e);
        }
        return styles;
    }

    private String readScriptFromFile(String jsFilePath) {
        String script = "";
        try {
            script = new String(Files.readAllBytes(Paths.get(jsFilePath)));
        } catch (Exception e) {
            System.out.println(e);
        }
        return script;
    }
}
