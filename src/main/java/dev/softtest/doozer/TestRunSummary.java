package dev.softtest.doozer;


import java.util.Comparator;
import java.util.Formatter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Collectors;

/** The class for TXT report generation for the whole run. */
public class TestRunSummary {
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
        f.format("%n%-100s %10s   %-20s%n", "TestCase", "Result", "Error");
        f.format("%-100s %10s   %-20s%n", "--------", "------", "-----");
    
        for (TestCase tc : testCases.stream().parallel()
                .sorted(comparator)
                .collect(Collectors.toList())) {
            String tcPath = tc.getTestScriptPath().toString();
            String err = tc.getTestSteps().size() != 0 ? tc.getTestSteps().getLast().getError() : null;
            err = err == null ? "" : err;
            err = err.contains("\n") ? err.substring(0, err.indexOf("\n")) : err;
            f.format("%-100.80s %10s   %-20s%n",
              tcPath.length() > 80 ? tcPath.substring(tcPath.length() - 80) : tcPath,
              tc.getTestResult(),
              err);
        }

        f.format("%-100s %10s   %-20s%n", "--------", "------", "-----");

        Files.write(
            Paths.get("target/doozer-tests/doozer-report.txt"),
            f.toString().getBytes());
        System.out.println(f.toString());
    }
    
}
