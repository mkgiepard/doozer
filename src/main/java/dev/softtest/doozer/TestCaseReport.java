package dev.softtest.doozer;

import java.io.IOException;
import java.nio.file.*;

import j2html.tags.specialized.DivTag;

import static j2html.TagCreator.*;

public class TestCaseReport {
    private final String REPORT_FILENAME = "doozer-report.html";
    private final String resultsDir;
    private final TestCase testCase;
    private final String testCaseDir;

    public TestCaseReport(String resultsDir, TestCase testCase) {
        this.resultsDir = resultsDir;
        this.testCase = testCase;
        this.testCaseDir = testCase.getTestCaseName();
    }

    public void generate() {
        Path path = Paths.get(resultsDir + testCaseDir + "/" + REPORT_FILENAME);
        String htmlReport = generateTestReport();
        try {
            Files.write(path, htmlReport.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateTestReport() {
        String htmlReport = "<html>";
        htmlReport += includeCSS();
        htmlReport += includeHeader();

        htmlReport += "<body>";
        htmlReport += "<div class=\"container-main\">";
        htmlReport += getHeader();

        htmlReport += "<div class=\"container-testcase\">";
        htmlReport += getTestCaseHeader(this.testCase);
        htmlReport += getTestCaseSteps(this.testCase);
        htmlReport += "</div>";

        htmlReport += "</div></body>";
        htmlReport += includeJS();
        htmlReport += "</html>";
        return htmlReport;
    }

    private String includeCSS() {
        String materialFontLink = link()
                .withRel("stylesheet")
                .withHref(
                        "https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0")
                .render();
        String css = style(readCssFromFile("src/main/resources/styles.css")).render();
        return materialFontLink + css;
    }

    private String includeJS() {
        return script(readScriptFromFile("src/main/resources/script.js")).render();
    }

    private String getHeader() {
        return div(div(join(
                div("Test Case"),
                div("STATUS").withClass("center"),
                div("RESULT").withClass("center"),
                div("PIXEL DIFF").withClass("center"),
                div("ACTION").withClass("center"))).withClasses("container-testcase-header", "title"))
                .withClass("container-testcase").render();
    }

    private String getTestCaseHeader(TestCase tc) {
        String script = tc.getTestScriptPath();
        String status = tc.getTestStatus().toString();
        String diff = "0";
        String id = tc.getTestCaseName();
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)) {
                if (step.getArtifact() != null && step.getArtifact().getDiff() != 0) {
                    diff = Long.toString(step.getArtifact().getDiff());
                    id += step.getAction().getLineNumber();
                }
            }
        }
        String resultIcon = tc.getTestResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = tc.getTestResult() == TestResult.PASS ? "pass" : "fail";
        String buttonHidden = tc.getTestResult() == TestResult.PASS || diff == "0" ? "hidden" : "";

        return div(join(
                div(div(script).withClass("testcase-name")),
                div(status).withClass("center"),
                div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center", resultStyle),
                div(diff).withClass("center"),
                div(button("APPROVE")).withClasses("center", buttonHidden))).withClass("container-testcase-header")
                .attr("onclick", "toggleDisplay('" + id + "')").renderFormatted();

    }

    private String getTestCaseSteps(TestCase tc) {
        return div(join(
                each(tc.getTestSteps(), step -> getTestStepDiv(step)))).withClass("container-teststeps")
                .renderFormatted();
    }

    private DivTag getTestStepDiv(TestStep step) {
        String actionText = step.getAction().getLineNumber()
                + ": "
                + step.getAction().getOriginalAction();
        String diff = "0";
        if (step.getArtifact() != null && step.getArtifact().getDiff() != 0) {
            diff = Long.toString(step.getArtifact().getDiff());
        }
        String resultIcon = step.getResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = step.getResult() == TestResult.PASS ? "pass" : "fail";
        return div(join(
                div(
                        join(
                                div(actionText).withClass("step-name"),
                                div(step.getStatus().toString()).withClass("center"),
                                div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center",
                                        resultStyle),
                                div(diff).withClass("center"),
                                div()))
                        .withClass("container-teststep"),
                getTestStepImages(step)));
    }

    private String getTestStepImages(TestStep step) {
        TestStep lastStep = step;

        if (lastStep.getArtifact() != null) {
            String screenshotName = lastStep.getAction().getOptions().get("default");
            if (screenshotName == null) {
                screenshotName = lastStep.getAction().getOptions().getOrDefault("fileName",
                        "screenshot-" + lastStep.getAction().getLineNumber());
            }
            String browserDesc = lastStep.getAction().getContext().getDoozerDriver().getBrowserDesc();
            screenshotName += "-" + browserDesc;
            return getContainerTestCaseImages(lastStep.getArtifact(), screenshotName,
                    step.getAction().getLineNumber().toString()).render();
        }
        return "";
    }

    private DivTag getContainerTestCaseImages(TestArtifact ta, String screenshotName, String id) {
        return div(join(
                div(img().withSrc("../../../" + ta.getGoldensPath() + screenshotName + ".png")).withClass("card"),
                div(img().withSrc("./" + screenshotName + ".DIFF.png")).withClass("card"),
                div(img().withSrc("./" + screenshotName + ".png")).withClass("card")))
                .withClasses("container-testcase-images").withId(id);

    }

    private String includeHeader() {
        return div(h1("Doozer Test Report")).withClass("header").renderFormatted();
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
