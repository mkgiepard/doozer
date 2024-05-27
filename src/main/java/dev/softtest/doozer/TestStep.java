package dev.softtest.doozer;

public class TestStep {
    private final DoozerAction action;
    private TestResult result;
    private TestStatus status;
    private String error;
    private TestArtifact artifact;

    public TestStep(DoozerAction action) {
        this.action = action;
    }

    public DoozerAction getAction() {
        return action;
    }

    public void setResult(TestResult result) {
        this.result = result;
    }

    public TestResult getResult() {
        return result;
    }

    public void setStatus(TestStatus status) {
        this.status = status;
    }

    public TestStatus getStatus() {
        return status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setArtifact(TestArtifact artifact) {
        this.artifact = artifact;
    }

    public TestArtifact getArtifact() {
        return artifact;
    }
}
