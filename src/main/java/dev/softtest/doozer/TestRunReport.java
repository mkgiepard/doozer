package dev.softtest.doozer;

import java.util.*;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

import j2html.tags.specialized.DivTag;

import static j2html.TagCreator.*;

/** The class for HTML report generation for the whole run. */
public class TestRunReport extends TestReport {
    private final Collection<TestCase> testCases;

    public TestRunReport(String resultsDir, Collection<TestCase> testCases) {
        super(resultsDir);
        this.testCases = testCases;
    }

    public void generate() {
        Path path = Paths.get(resultsDir + REPORT_FILENAME);
        String htmlReport = generateTestReport();    
        try {
            if (Files.notExists(path, LinkOption.NOFOLLOW_LINKS)) {
                Files.createDirectories(Paths.get(resultsDir));
            }
            Files.write(path, htmlReport.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String generateTestReport() {
        String htmlReport = "<html>";
        htmlReport += includeCSS();
        htmlReport += includePageHeader("Doozer Test Run Report");
        
        htmlReport += "<body>";
        htmlReport += "<div class=\"container-main\">";
        htmlReport += getHeaderRow();

        Comparator<TestCase> comparator = Comparator.comparing(tc -> tc.getTestResult());
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Comparator.comparing(tc -> tc.getTestCaseName()));
    
        for (TestCase tc : testCases.stream().parallel()
                .sorted(comparator)
                .collect(Collectors.toList())) {
            htmlReport += "<div class=\"container-testcase\">";
            htmlReport += getTestCaseSummaryAndStep(tc);
            htmlReport += getTestCaseImages(tc);
            htmlReport += "</div>";
        }
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

    private String getTestCaseSummaryAndStep(TestCase tc) {
        String script = tc.getTestScriptPath().toString();
        String diff = "-";
        String id = tc.getTestCaseName();
        String actionText = "";
        String goldenPath = "";
        String resultPath = "";
        for (TestStep step : tc.getTestSteps()) {
            if (step.getResult().equals(TestResult.FAIL)) {
                actionText = step.getAction().getSourceFileName() + "> "
                    + step.getAction().getLineNumber()
                    + ": "
                    + step.getAction().getOriginalAction();
                if (step.getArtifact() != null && step.getArtifact().getDiff() != 0 ) {
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

        return join(
            div(
                join(
                    div(script).withClass("testcase-name").attr("onclick", "toggleDisplay('" + id + "')"),
                    div(span(resultIcon).withClass("material-symbols-outlined")).withClasses("center", resultStyle),
                    div(diff).withClass("center"),
                    div(button("APPROVE").attr("onclick", "approve('" + id + "', '" + resultPath + "', '" + goldenPath + "')").withClass(buttonHidden)).withClass("center"),
                    div(a(span("open_in_new").withClass("material-symbols-outlined")).withHref("./" + tc.getContext().getTestResultPath().toString().substring(resultsDir.length()) + "/doozer-report.html")).withClass("center")))
            .withClass("container-testcase-summary"),
            div(actionText).withClasses("step-name", "hidden").withId(id+"step")).render();
    }

    private String getTestCaseImages(TestCase tc) {
        if (tc.getTestSteps().size() == 0) return "";
        TestStep lastStep = tc.getTestSteps().get(tc.getTestSteps().size() - 1);

        if (lastStep.getResult() == TestResult.FAIL && lastStep.getArtifact() != null) {
            String id = tc.getTestCaseName() + lastStep.getAction().getLineNumber();
            return getContainerTestCaseImages(lastStep.getArtifact(), id).render();
        }
        return "";
    }

    private DivTag getContainerTestCaseImages(TestArtifact ta, String id) {
        Path resultsRootPath = Paths.get("target/doozer-tests/");
        String parentDir = ta.getResultsPath().getParent().toAbsolutePath().toString();
        String screenshotName = ta.getName();
        String diffName = screenshotName.substring(0, screenshotName.lastIndexOf(".png")) + ".DIFF.png";
        String goldenSrc = resultsRootPath.toAbsolutePath().relativize(ta.getGoldensPath().toAbsolutePath()).toString();
        String diffSrc = resultsRootPath.toAbsolutePath().relativize(Paths.get(parentDir + File.separator + diffName).toAbsolutePath()).toString();
        String testSrc = resultsRootPath.toAbsolutePath().relativize(ta.getResultsPath().toAbsolutePath()).toString();

        String goldenSrcEscapedChars = goldenSrc;

        if (System.getProperty("os.name").contains("Windows")) {
            goldenSrcEscapedChars = goldenSrc.replaceAll("\\\\", "\\\\\\\\");
            diffSrc = diffSrc.replaceAll("\\\\", "\\\\\\\\");
            testSrc = testSrc.replaceAll("\\\\", "\\\\\\\\");
        }

        return div(join(
            div(img().withSrc(goldenSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '" + testSrc + "', 0)")).withClass("card"),
            div(img().withSrc(diffSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '"  + testSrc + "', 1)")).withClass("card"),
            div(img().withSrc(testSrc).attr("onclick", "openModal('" + goldenSrcEscapedChars + "', '" + diffSrc + "', '" + testSrc + "', 2)")).withClass("card")
        )).withClasses("container-teststep-images", "hidden").withId(id);
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
