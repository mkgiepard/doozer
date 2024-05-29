package dev.softtest.doozer;

import java.util.*;
import java.nio.file.*;

import j2html.tags.ContainerTag;
import j2html.tags.specialized.LiTag;

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
        String css = readCssFromFile("src/main/resources/styles.css");
        return style(css).renderFormatted();
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
