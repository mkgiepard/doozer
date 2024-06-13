package dev.softtest.doozer;

import java.nio.file.Path;

/** Stores an artifact of DoozerAction execution, for example a screenshot with comparison stats. */
public class TestArtifact {
    private final TestArtifactType type;
    private final String name;
    private final Path goldensPath;
    private final Path resultsPath;
    private final long diff;
    private final double percentDiff;
    private final long diffThreshold;
    private final double percentDiffThreshold;

    private TestArtifact(Builder builder) {
        this.type = builder.type;
        this.name = builder.name;
        this.goldensPath = builder.goldenPath;
        this.resultsPath = builder.resultPath;
        this.diff = builder.diff;
        this.percentDiff = builder.percentDiff;
        this.diffThreshold = builder.diffThreshold;
        this.percentDiffThreshold = builder.percentDiffThreshold;
    }
    public TestArtifactType getType() {
        return type;
    }
    public String getName() {
        return name;
    }
    public Path getGoldensPath() {
        return goldensPath;
    }
    public Path getResultsPath() {
        return resultsPath;
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
        private String name;
        private Path goldenPath;
        private Path resultPath;
        private long diff;
        private double percentDiff;
        private long diffThreshold;
        private double percentDiffThreshold;

        public Builder(TestArtifactType type, String name) {
            this.type = type;
            this.name = name;
        }

        public Builder goldenPath(Path goldenPath) {
            this.goldenPath = goldenPath;
            return this;
        }

        public Builder resultPath(Path resultPath) {
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
