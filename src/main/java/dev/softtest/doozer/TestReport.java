package dev.softtest.doozer;

import java.util.*;
import java.nio.file.*;

import j2html.tags.ContainerTag;
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

    public String generateHtmlReport(List<TestStep> steps) {
        return ul(steps
                .stream()
                .map(step -> getAction(step))
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

    public String includeHeader() {
        return div(h1("Doozer Test Report")).withClass("header").renderFormatted();
    }

    public String getTestCaseHeader(TestCase tc) {
        String script = tc.getTestScriptPath();
        String result = tc.getTestResult().toString();
        String diff = "0";
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)
                && step.getArtifact() != null
                && step.getArtifact().getDiff() != 0 ) {
                diff = Long.toString(step.getArtifact().getDiff());
            }
        }
        return div(join(
            div(join(
                div(script).withClass("testcase-name"),
                div().withClasses("step-name", "hidden")
            )),
            div(result).withClass("center"),
            div(span("cancel").withClass("material-symbols-outlined")).withClasses("center", "fail"),
            div(diff).withClass("center"),
            div(button("APPROVE")).withClass("center")
        )).withClass("container-testcase-header").renderFormatted();

    }

    private LiTag getAction(TestStep step) {
        String actionText = step.getAction().getLineNumber()
                + ": "
                + step.getAction().getOriginalAction();

        if (step.getArtifact() != null) {
            String screenshotName = step.getAction().getOptions().get("default");
            if (screenshotName == null) {
                screenshotName = step.getAction().getOptions().getOrDefault("fileName",
                        "screenshot-" + step.getAction().getLineNumber());
            }

            return li(join(actionText, ul(
                    li(img().withSrc("../../"
                            + step.getArtifact().getGoldenPath()
                            + screenshotName + ".png").withStyle("max-width: 30%")),
                    li(img().withSrc("." + step.getArtifact().getResultPath().substring("target/doozer-tests/".length())
                            + screenshotName + ".DIFF.png").withStyle("max-width: 30%")),
                    li(img().withSrc("." + step.getArtifact().getResultPath().substring("target/doozer-tests/".length())
                            + screenshotName + ".png").withStyle("max-width: 30%")))));
        }
        return li(actionText);
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
}
