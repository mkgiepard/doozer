package dev.softtest.doozer;

public class TestArtifact {
    private final TestArtifactType type;
    private final String goldenPath;
    private final String resultPath;
    private final long diff;
    private final double percentDiff;
    private final long diffThreshold;
    private final double percentDiffThreshold;

    private TestArtifact(Builder builder) {
        this.type = builder.type;
        this.goldenPath = builder.goldenPath;
        this.resultPath = builder.resultPath;
        this.diff = builder.diff;
        this.percentDiff = builder.percentDiff;
        this.diffThreshold = builder.diffThreshold;
        this.percentDiffThreshold = builder.percentDiffThreshold;
    }
    public TestArtifactType getType() {
        return type;
    }
    public String getGoldenPath() {
        return goldenPath;
    }
    public String getResultPath() {
        return resultPath;
    }
    public long getDiff() {
        return diff;
    }
    public double getPercentDiff() {
        return percentDiff;
    }
    public long getDiffThreshold() {
        return diffThreshold;
    }
    public double getPercentDiffThreshold() {
        return percentDiffThreshold;
    }

    public static class Builder {
        private final TestArtifactType type;
        private String goldenPath;
        private String resultPath;
        private long diff;
        private double percentDiff;
        private long diffThreshold;
        private double percentDiffThreshold;

        public Builder(TestArtifactType type) {
            this.type = type;
        }

        public Builder goldenPath(String goldenPath) {
            this.goldenPath = goldenPath;
            return this;
        }

        public Builder resultPath(String resultPath) {
            this.resultPath = resultPath;
            return this;
        }

        public Builder diff(long diff) {
            this.diff = diff;
            return this;
        }

        public Builder percentDiff(double percentDiff) {
            this.percentDiff = percentDiff;
            return this;
        }

        public Builder diffThreshold(long diffThreshold) {
            this.diffThreshold = diffThreshold;
            return this;
        }

        public Builder percentDiffThreshold(double percentDiffThreshold) {
            this.percentDiffThreshold = percentDiffThreshold;
            return this;
        }

        public TestArtifact build() {
            return new TestArtifact(this);
        }
    }


}
