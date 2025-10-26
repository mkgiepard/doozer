package dev.softtest.doozer;


import java.util.Comparator;
import java.util.Formatter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

/** The class for TXT report generation for the whole run. */
public class TestRunSummary {
    private final String OKBLUE = "\033[94m";
    private final String OKMAGENTA = "\033[95m";
    private final String OKGREEN = "\033[92m";
    private final String FAIL = "\033[91m";
    private final String ENDC = "\033[0m";

    private final Collection<TestCase> testCases;
    private Formatter f = new Formatter(new StringBuilder());

    public TestRunSummary(Collection<TestCase> testCases) {
        this.testCases = testCases;
    }

    public void generate() throws Exception {
        if (testCases == null || testCases.isEmpty()) return;

        Comparator<TestCase> comparator = Comparator.comparing(tc -> tc.getTestResult());
        comparator = comparator.reversed();
        comparator = comparator.thenComparing(Comparator.comparing(tc -> tc.getTestCaseName()));
        String bar = "\u2588".repeat(120);
        f.format("\n%s\n", bar);
        f.format("%s%n%-100s %10s   %-20s%n%s", OKBLUE, "TestCase", "Result", "Error", ENDC);
        f.format("%s%-100s %10s   %-20s%n%s", OKBLUE, "--------", "------", "-----", ENDC);
    
        for (TestCase tc : testCases.stream().parallel()
                .sorted(comparator)
                .collect(Collectors.toList())) {
            String tcPath = tc.getTestScriptPath().toString();
            String err = tc.getTestSteps().size() != 0 ? tc.getTestSteps().getLast().getError() : null;
            err = err == null ? "" : err;
            err = err.contains("\n") ? err.substring(0, err.indexOf("\n")) : err;
            f.format("%-100.80s %s%10s%s   %-20s%n",
              tcPath.length() > 80 ? tcPath.substring(tcPath.length() - 80) : tcPath,
              tc.getTestResult().equals(TestResult.PASS) ? OKGREEN : FAIL,
              tc.getTestResult(),
              ENDC,
              err);
        }

        f.format("%s%-100s %10s   %-20s%n%s", OKBLUE, "--------", "------", "-----", ENDC);
        f.format("\n%s\n", bar);
        Files.write(
            Paths.get("target/doozer-tests/doozer-report.txt"),
            f.toString().getBytes());
        System.out.println(f.toString());

        String htmlReportPath = 
            "file://" + Paths.get("target/doozer-tests/doozer-report.html").toAbsolutePath().toString();

        if (System.getProperty("os.name").contains("Windows")) {
            htmlReportPath = 
                "file:///" + Paths.get("target\\doozer-tests\\doozer-report.html").toAbsolutePath().toString();
        }

        System.out.println(OKMAGENTA + "Doozer Test Run Report: " + htmlReportPath + ENDC);
        System.out.println("");
    }
    
}
