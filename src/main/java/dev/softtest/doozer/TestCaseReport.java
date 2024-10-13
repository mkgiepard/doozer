package dev.softtest.doozer;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import j2html.tags.specialized.DivTag;

import static j2html.TagCreator.*;

/** The class for HTML report generation per test case. */
public class TestCaseReport extends TestReport {
    private final String testCaseDir;
    protected final TestCase testCase;

    public TestCaseReport(String resultsDir, TestCase testCase) {
        super(resultsDir);
        this.testCase = testCase;
        this.testCaseDir = testCase.getTestCaseName();
    }

    public void generate() {
        Path path = Paths.get(testCase.getContext().getTestResultPath().toString()  + "/" + REPORT_FILENAME);
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
        htmlReport += includePageHeader("Doozer Test Report");

        htmlReport += "<body>";
        htmlReport += "<div class=\"container-main\">";
        htmlReport += getHeaderRow();

        htmlReport += "<div class=\"container-testcase\">";
        htmlReport += getTestCaseSummary(this.testCase);
        htmlReport += getTestCaseSteps(this.testCase);
        htmlReport += "</div>";

        htmlReport += "<div class=\"container-commands\">"; 
        htmlReport += getCommandTextArea();
        htmlReport += "<button onclick=\"clearCommands()\">Clear</button>";
        htmlReport += "<button onclick=\"copy()\">Copy to clipboard</button>";
        htmlReport += "</div>";
        htmlReport += "</div>";
        htmlReport += getModal();
        htmlReport += "</body>";
        htmlReport += includeJS();
        htmlReport += "</html>";
        return htmlReport;
    }

    private String getTestCaseSummary(TestCase tc) {
        String script = tc.getTestScriptPath().toString();
        String diff = "-";
        String id = tc.getTestCaseName();
        String goldenPath = "";
        String resultPath = "";
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)) {
                if (step.getArtifact() != null && step.getArtifact().getDiff() != 0) {
                    diff = Long.toString(step.getArtifact().getDiff());
                    id += step.getAction().getLineNumber();
                    goldenPath = step.getArtifact().getGoldensPath().toAbsolutePath().toString();
                    resultPath = step.getArtifact().getResultsPath().toAbsolutePath().toString();
                    if (System.getProperty("os.name").contains("Windows")) {
                        goldenPath = goldenPath.replaceAll("\\\\", "\\\\\\\\");
                        resultPath = resultPath.replaceAll("\\\\", "\\\\\\\\");
                    }
                }
            }
        }
        String resultIcon = tc.getTestResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = tc.getTestResult() == TestResult.PASS ? "pass" : "fail";
        String buttonHidden = tc.getTestResult() == TestResult.PASS || diff == "-" ? "hidden" : "";

        return div(join(
                div(div(script).withClass("testcase-name")),
                div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center", resultStyle),
                div(diff).withClass("center"),
                div())
               ).withClass("container-testcase-summary")
                .attr("onclick", "toggleDisplay('" + id + "')")
                .renderFormatted();

    }

    private String getTestCaseSteps(TestCase tc) {
        return div(join(
                each(tc.getTestSteps(), step -> getTestStepDiv(step)))).withClass("container-teststeps")
                .renderFormatted();
    }

    private DivTag getTestStepDiv(TestStep step) {
        String actionText = step.getAction().getSourceFileName() + "> "
                + step.getAction().getLineNumber()
                + ": "
                + step.getAction().getOriginalAction();
        String diff = "0";
        String id = step.getAction().getSourceFileName() + "_" + step.getAction().getLineNumber();
        String goldenPath = "";
        String resultPath = "";
        
        if (step.getArtifact() != null && step.getArtifact().getDiff() != 0) {
            diff = Long.toString(step.getArtifact().getDiff());
            goldenPath = step.getArtifact().getGoldensPath().toAbsolutePath().toString();
            resultPath = step.getArtifact().getResultsPath().toAbsolutePath().toString();
            if (System.getProperty("os.name").contains("Windows")) {
                goldenPath = goldenPath.replaceAll("\\\\", "\\\\\\\\");
                resultPath = resultPath.replaceAll("\\\\", "\\\\\\\\");
            }
        }
        String resultIcon = step.getResult() == TestResult.PASS ? "check" : "cancel";
        String resultStyle = step.getResult() == TestResult.PASS ? "pass" : "fail";
        String buttonHidden = diff == "-" || diff == "0" ? "hidden" : "";
        return div(join(
                    div(
                        join(
                            div(actionText).withClass("step-name"),
                            div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center",
                                resultStyle),
                            div(diff).withClass("center"),
                            div(button("APPROVE").attr("onclick", "approve('" + id + "', '" + resultPath + "', '" + goldenPath + "')")).withClasses("center", buttonHidden))
                    ).withClass("container-teststep-summary"),
                    getTestStepImages(step))
                ).withClass("container-teststep");
    }

    private String getTestStepImages(TestStep step) {
        TestStep lastStep = step;

        if (lastStep.getArtifact() != null) {
            return getContainerTestCaseImages(lastStep.getArtifact(),
                    step.getAction().getLineNumber().toString()).render();
        }
        return "";
    }

    private DivTag getContainerTestCaseImages(TestArtifact ta, String id) {
        Path testResultRootPath = ta.getResultsPath().getParent().toAbsolutePath();
        String screenshotName = ta.getName();
        String diffName = screenshotName.substring(0, screenshotName.lastIndexOf(".png")) + ".DIFF.png";
        String goldenSrc = testResultRootPath.toAbsolutePath().relativize(ta.getGoldensPath().toAbsolutePath()).toString();
        String diffSrc = "./" + diffName;
        String testSrc = "./" + ta.getResultsPath().getFileName().toString();

        String goldenSrcEscapedChars = goldenSrc;

        if (System.getProperty("os.name").contains("Windows")) {
            goldenSrcEscapedChars = goldenSrc.replaceAll("\\\\", "\\\\\\\\");
            diffSrc = diffSrc.replaceAll("\\\\", "\\\\\\\\");
            testSrc = testSrc.replaceAll("\\\\", "\\\\\\\\");
        }

        return div(join(
                div(img().withSrc(goldenSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '" + testSrc + "', 0)")).withClass("card"),
                div(img().withSrc(diffSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '" + testSrc + "', 1)")).withClass("card"),
                div(img().withSrc(testSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '" + testSrc + "', 2)")).withClass("card")))
                .withClasses("container-teststep-images").withId(id);
    }

    private String getCommandTextArea() {
        return textarea()
            .withPlaceholder("Commands to update approved goldens")
            .withId("command-container")
            .withRows("5")
            .withCols("5")
            .withClass("commands").toString();
    }
}
