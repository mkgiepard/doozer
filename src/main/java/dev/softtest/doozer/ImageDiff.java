package dev.softtest.doozer;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ImageDiff {
    protected static final Logger logger = LogManager.getLogger();
    
    private static final double DEFAULT_THRESHOLD = 0.01;
    private final String goldenImgPath;
    private final String resultImgPath;
    private final double threshold;
    BufferedImage goldenImg;
    BufferedImage resultImg;
    BufferedImage diffImg;
    private double diffRatio = 0;
    private long diffPixel = 0;


    public ImageDiff(String goldenImgPath, String resultImgPath) {
        this(goldenImgPath, resultImgPath, DEFAULT_THRESHOLD);
    }

    public ImageDiff(String goldenImgPath, String resultImgPath, double threshold) {
        this.goldenImgPath = goldenImgPath;
        this.resultImgPath = resultImgPath;
        this.threshold = threshold;
    }

    public void compare() throws Exception {
        File fileA = new File(goldenImgPath);
        File fileB = new File(resultImgPath);

        goldenImg = ImageIO.read(fileA);
        resultImg = ImageIO.read(fileB);

        if (goldenImg != null && resultImg != null) {
            int goldenWidth = goldenImg.getWidth();
            int resultWidth = resultImg.getWidth();
            int goldenHeight = goldenImg.getHeight();
            int resultHeight = resultImg.getHeight();

            diffImg = new BufferedImage(goldenWidth, goldenHeight, goldenImg.getType());

            if ((goldenWidth != resultWidth) || (goldenHeight != resultHeight)) {
                logger.error("Image comparison failed, images dimensions mismatch!");
                logger.info(goldenWidth + "x" + goldenHeight + " -- " + resultWidth + "x" + resultHeight);
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

                logger.info("Number of different pixels: " + diffPixel);
                logger.info("Percent of different pixels: " + diffRatio 
                    + "% with acceptable difference threshold set to: " + threshold + "%");

                generateDiffImg(diffImg);
                if (diffRatio > 0 && diffRatio > threshold) {
                    logger.error("Image comparison failed!");
                    throw new Exception("Image comparison failed!");
                }

            }
        }
    }

    public void generateDiffImg(BufferedImage img) {
        try {
            File f = new File(resultImgPath.substring(0, resultImgPath.lastIndexOf(".png")) + ".DIFF.png");
            ImageIO.write(img, "png", f);
        } catch (IOException e) {
            System.out.println(e);
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
}
