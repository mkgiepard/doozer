package dev.softtest.doozer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Compares images, pixel by pixel, creates a DIFF image and comparison statistics. */
public class ImageDiff {
    protected static final Logger LOG = LogManager.getLogger();
    
    private static final double DEFAULT_THRESHOLD = 0.01;
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
            throw new IOException("Error while reading: " + goldenImgPath + "\n" + e);
        }
        try {
            File fileB = resultImgPath.toFile();
            resultImg = ImageIO.read(fileB);
        } catch (IOException e) {
            throw new IOException("Error while reading: " + resultImgPath + "\n" + e);
        }

        if (goldenImg != null && resultImg != null) {
            int goldenWidth = goldenImg.getWidth();
            int resultWidth = resultImg.getWidth();
            int goldenHeight = goldenImg.getHeight();
            int resultHeight = resultImg.getHeight();

            diffImg = new BufferedImage(goldenWidth, goldenHeight, goldenImg.getType());

            if ((goldenWidth != resultWidth) || (goldenHeight != resultHeight)) {
                LOG.error("Image comparison failed, images dimensions mismatch!");
                LOG.info(goldenWidth + "x" + goldenHeight + " -- " + resultWidth + "x" + resultHeight);
                throw new Exception("Image comparison failed, images dimensions mismatch!");
            }
            else {
                for (int y = 0; y < goldenHeight; y++) {
                    for (int x = 0; x < goldenWidth; x++) {
                        int goldenRgb = goldenImg.getRGB(x, y);
                        int resultRgb = resultImg.getRGB(x, y);

                        int white = (255 << 24) | (255 << 16) | (255 << 8) | 255;
                        int red = (255 << 24) | (255 << 16) | (0 << 8) | 0;

                        if (goldenRgb != resultRgb) {
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
}
