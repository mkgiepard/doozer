package dev.softtest.doozer;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.*;

import j2html.tags.specialized.DivTag;

import static j2html.TagCreator.*;

public class TestRunReport {
    private final String REPORT_FILENAME = "doozer-report.html";
    private final String resultsDir;
    private final Collection<TestCase> testCases;

    public TestRunReport(String resultsDir, Collection<TestCase> testCases) {
        this.resultsDir = resultsDir;
        this.testCases = testCases;
    }

    public void generate() {
        Path path = Paths.get(resultsDir + REPORT_FILENAME);
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

        for (TestCase tc : testCases.stream()
                .sorted((tc1, tc2) -> tc1.getTestCaseName().compareTo(tc2.getTestCaseName()))
                .sorted((tc1, tc2) -> tc2.getTestResult().compareTo(tc1.getTestResult()))
                .collect(Collectors.toList())) {
            htmlReport += "<div class=\"container-testcase\">";
            htmlReport += getTestCaseSummaryAndStep(tc);
            htmlReport += getTestCaseImages(tc);
            htmlReport += "</div>";
        }
        ;
        htmlReport += "</div></body>";
        htmlReport += includeJS();
        htmlReport += "</html>";
        return htmlReport;
    }

    private String includeCSS() {
        String materialFontLink = link()
            .withRel("stylesheet")
            .withHref("https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:opsz,wght,FILL,GRAD@24,400,0,0")
            .render();
        String css = style(readCssFromFile("src/main/resources/styles.css")).render();
        return materialFontLink + css;
    }

    private String includeJS() {
        return script(readScriptFromFile("src/main/resources/script.js")).render();
    }

    private String includeHeader() {
        return div(h1("Doozer Test Run Report")).withClass("header").renderFormatted();
    }

    private String getHeader() {
        return div(div(join(
            div("Test Case"),
            div("RESULT").withClass("center"),
            div("PIXEL DIFF").withClass("center"),
            div("ACTION").withClass("center")
        )).withClasses("container-testcase-header", "title")).withClass("container-testcase").render();
    }

    private String getTestCaseSummaryAndStep(TestCase tc) {
        String script = tc.getTestScriptPath();
        String diff = "0";
        String id = tc.getTestCaseName();
        String actionText = "";
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)) {
                actionText = step.getAction().getLineNumber()
                    + ": "
                    + step.getAction().getOriginalAction();
                if (step.getArtifact() != null && step.getArtifact().getDiff() != 0 ) {
                    diff = Long.toString(step.getArtifact().getDiff());
                    id += step.getAction().getLineNumber();
                }
            }
        }
        String resultIcon = tc.getTestResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = tc.getTestResult() == TestResult.PASS ? "pass" : "fail";
        String buttonHidden = tc.getTestResult() == TestResult.PASS || diff == "0" ? "hidden" : "";
        
        

        return join(
            div(
                join(
                    div(script).withClass("testcase-name"),
                    div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center", resultStyle),
                    div(diff).withClass("center"),
                    div(button("APPROVE").withClass(buttonHidden)).withClass("center"),
                    div(a(span("open_in_new").withClass("material-symbols-outlined")).withHref("./" + tc.getTestCaseName() + "/doozer-report.html")).withClass("center")))
            .withClass("container-testcase-summary").attr("onclick", "toggleDisplay('" + id + "')"),
            div(actionText).withClasses("step-name", "hidden").withId(id+"step")).render();
    }

    private String getTestCaseImages(TestCase tc) {
        TestStep lastStep = tc.getTestSteps().get(tc.getTestSteps().size() - 1);

        if (lastStep.getResult() == TestResult.FAIL && lastStep.getArtifact() != null) {
            String id = tc.getTestCaseName() + lastStep.getAction().getLineNumber();
            String screenshotName = lastStep.getAction().getOptions().get("default");
            if (screenshotName == null) {
                screenshotName = lastStep.getAction().getOptions().getOrDefault("fileName",
                        "screenshot-" + lastStep.getAction().getLineNumber());
            }
            String browserDesc = lastStep.getAction().getContext().getDoozerDriver().getBrowserDesc();
            screenshotName += "-" + browserDesc;
            return getContainerTestCaseImages(lastStep.getArtifact(), screenshotName, id).render();
        }
        return "";
    }

    private DivTag getContainerTestCaseImages(TestArtifact ta, String screenshotName, String id) {
        return div(join(
            div(img().withSrc("../../" + ta.getGoldensPath()+screenshotName+".png")).withClass("card"),
            div(img().withSrc("." + ta.getResultsPath().substring("target/doozer-tests/".length())+screenshotName + ".DIFF.png")).withClass("card"),
            div(img().withSrc("." + ta.getResultsPath().substring("target/doozer-tests/".length())+screenshotName + ".png")).withClass("card")
        )).withClasses("container-teststep-images", "hidden").withId(id);

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
