package dev.softtest.doozer;

import java.util.*;

import j2html.tags.ContainerTag;

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
                .map(step -> li(step.getAction().getLineNumber() + ": " + step.getAction().getOriginalAction()))
                .toArray(ContainerTag[]::new))
                .render();
    }

    public String generateReport() {
        return body(
            h1("Hello, World!"),
            img().withSrc("/img/hello.png")
        ).render();
    }

}
