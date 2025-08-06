package dev.softtest.doozer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Compares images, pixel by pixel, creates a DIFF image and comparison
 * statistics.
 */
public class ImageDiff {
    protected static final Logger LOG = LogManager.getLogger();

    private static final double DEFAULT_THRESHOLD = 0.01;
    private static final double DEFAULT_DELTA_E_THRESHOLD = 5.0;

    private final Path goldenImgPath;
    private final Path resultImgPath;
    private final double threshold;
    BufferedImage goldenImg;
    BufferedImage resultImg;
    BufferedImage diffImg;
    private double diffRatio = 0;
    private long diffPixel = 0;

    public ImageDiff(Path goldenImgPath, Path resultImgPath) {
        this(goldenImgPath, resultImgPath, DEFAULT_THRESHOLD);
    }

    public ImageDiff(Path goldenImgPath, Path resultImgPath, double threshold) {
        this.goldenImgPath = goldenImgPath;
        this.resultImgPath = resultImgPath;
        this.threshold = threshold;
    }

    public void compare() throws Exception {
        try {
            File fileA = goldenImgPath.toFile();
            goldenImg = ImageIO.read(fileA);
        } catch (IOException e) {
            LOG.warn("Error while reading: " + goldenImgPath + "\n" + e);
        }
        try {
            File fileB = resultImgPath.toFile();
            resultImg = ImageIO.read(fileB);
        } catch (IOException e) {
            throw new ImageDiffIOException("Error while reading: " + resultImgPath + "\n" + e);
        }

        if (resultImg != null) {
            if (goldenImg == null) {
                goldenImg = new BufferedImage(resultImg.getWidth(), resultImg.getHeight(), resultImg.getType());
            }

            int goldenWidth = goldenImg.getWidth();
            int resultWidth = resultImg.getWidth();
            int goldenHeight = goldenImg.getHeight();
            int resultHeight = resultImg.getHeight();

            long goldenSize = goldenHeight * goldenWidth;
            long resultSize = resultHeight * resultWidth;

            int white = (255 << 24) | (255 << 16) | (255 << 8) | 255;
            int red = (255 << 24) | (255 << 16) | (0 << 8) | 0;

            int diffWidth = Math.max(goldenWidth, resultWidth);
            int diffHeight = Math.max(goldenHeight, resultHeight);

            diffImg = new BufferedImage(
                    diffWidth,
                    diffHeight,
                    goldenImg.getType());

            if ((goldenWidth != resultWidth) || (goldenHeight != resultHeight)) {
                LOG.error("Image comparison failed, images dimensions mismatch!");
                LOG.info(goldenWidth + "x" + goldenHeight + " -- " + resultWidth + "x" + resultHeight);

                for (int y = 0; y < diffHeight; y++) {
                    for (int x = 0; x < diffWidth; x++) {
                        if (y > Math.min(goldenHeight, resultHeight) || x > Math.min(goldenWidth, resultWidth)) {
                            diffImg.setRGB(x, y, red);
                        } else {
                            diffImg.setRGB(x, y, white);
                        }
                    }
                }

                generateDiffImg(diffImg);
                diffPixel = Math.abs(resultSize - goldenSize);
                throw new ImageDiffException("Image comparison failed, images dimensions mismatch! " + diffPixel);
            } else {
                for (int y = 0; y < goldenHeight; y++) {
                    for (int x = 0; x < goldenWidth; x++) {
                        int goldenRgb = goldenImg.getRGB(x, y);
                        int resultRgb = resultImg.getRGB(x, y);

                        int goldenBlue = goldenRgb & 0xff;
                        int goldenGreen = (goldenRgb & 0xff00) >> 8;
                        int goldenRed = (goldenRgb & 0xff0000) >> 16;
                        int goldenAlpha = (goldenRgb & 0xff000000) >>> 24;

                        int resultBlue = resultRgb & 0xff;
                        int resultGreen = (resultRgb & 0xff00) >> 8;
                        int resultRed = (resultRgb & 0xff0000) >> 16;
                        int resultAlpha = (resultRgb & 0xff000000) >>> 24;

                        ColorComparator comparator = new ColorComparator();
                        double deltaE = comparator.compareColors(
                            new int[] { goldenRed, goldenGreen, goldenBlue },
                            new int[] { resultRed, resultGreen, resultBlue }
                        );

                        // accept a 1 bit difference per color, to address the flakiness coming from
                        // rendering
                        if (deltaE > DEFAULT_DELTA_E_THRESHOLD) {
                            diffPixel++;
                            diffImg.setRGB(x, y, red);
                        } else {
                            diffImg.setRGB(x, y, white);
                        }
                    }
                }
                double totalPixels = goldenWidth * goldenHeight;
                diffRatio = (diffPixel / totalPixels) * 100;

                LOG.info("Number of different pixels: " + diffPixel);
                LOG.info("Percent of different pixels: " + diffRatio
                        + "% with acceptable difference threshold set to: " + threshold + "%");

                generateDiffImg(diffImg);
                if (diffRatio > 0 && diffRatio > threshold) {
                    LOG.error("Image comparison failed!");
                    throw new ImageDiffException("Image comparison failed!");
                }

            }
        }
    }

    public void generateDiffImg(BufferedImage img) throws IOException {
        try {
            String path = resultImgPath.toString();
            Path diffImgPath = Paths.get(path.substring(0, path.lastIndexOf(".png")) + ".DIFF.png");
            ImageIO.write(img, "png", diffImgPath.toFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public double getDiffRatio() {
        return diffRatio;
    }

    public long getDiffPixel() {
        return diffPixel;
    }

    public double getThreshold() {
        return threshold;
    }

    public class ImageDiffException extends Exception {
        public ImageDiffException(String errorMessage) {
            super(errorMessage);
            LOG.error(errorMessage);
        }
    }

    public class ImageDiffIOException extends Exception {
        public ImageDiffIOException(String errorMessage) {
            super(errorMessage);
            LOG.error(errorMessage);
        }
    }
}
