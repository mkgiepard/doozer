package dev.softtest.doozer;

import java.lang.Math;

// source for color conversions: https://www.easyrgb.com/en/math.php
public class ColorComparator {

    protected double[] rgb2xyz(int[] rgb) {
        double[] errorPlaceholder = new double[] { 0, 0, 0 };
        if (rgb == null || rgb.length != 3)
            return errorPlaceholder;
        if (rgb[0] < 0 || rgb[1] < 0 || rgb[2] < 0)
            return errorPlaceholder;
        if (rgb[0] > 255 || rgb[1] > 255 || rgb[2] > 255)
            return errorPlaceholder;

        double normR = calculateNormN(rgb[0]);
        double normG = calculateNormN(rgb[1]);
        double normB = calculateNormN(rgb[2]);

        double x = (normR * 0.4124 + normG * 0.3576 + normB * 0.1805);
        double y = (normR * 0.2126 + normG * 0.7152 + normB * 0.0722);
        double z = (normR * 0.0193 + normG * 0.1192 + normB * 0.9504);

        return new double[] { x, y, z };
    }

    protected double[] xyz2cielab(double[] xyz) {
        double[] errorPlaceholder = new double[] { 0, 0, 0 };
        if (xyz == null || xyz.length != 3)
            return errorPlaceholder;

        double refX = xyz[0] / 95.047;
        double refY = xyz[1] / 100.000;
        double refZ = xyz[2] / 108.883;

        refX = calculateRefN(refX);
        refY = calculateRefN(refY);
        refZ = calculateRefN(refZ);

        double cieL = (116 * refY) - 16;
        double cieA = 500 * (refX - refY);
        double cieB = 200 * (refY - refZ);

        return new double[] { cieL, cieA, cieB };
    }

    protected double[] rgb2cielab(int[] rgb) {
        double[] errorPlaceholder = new double[] { 0, 0, 0 };
        if (rgb == null || rgb.length != 3)
            return errorPlaceholder;
        if (rgb[0] < 0 || rgb[1] < 0 || rgb[2] < 0)
            return errorPlaceholder;
        if (rgb[0] > 255 || rgb[1] > 255 || rgb[2] > 255)
            return errorPlaceholder;

        return xyz2cielab(rgb2xyz(rgb));
    }

    public double compareColors(int[] a, int[] b) {
        if (a == null || a.length != 3)
            return 100;
        if (b == null || b.length != 3)
            return 100;

        double[] cielabA = rgb2cielab(a);
        double[] cielabB = rgb2cielab(b);

        double result = Math.sqrt(
            Math.pow((cielabA[0] - cielabB[0]), 2)
            + Math.pow((cielabA[1] - cielabB[1]), 2)
            + Math.pow((cielabA[2] - cielabB[2]), 2)
        );
        return result;
    }

    private double calculateNormN(int n) {
        double normN = n / 255.0;
        if (normN > 0.04045)
            normN = Math.pow(((normN + 0.055) / 1.055), 2.4);
        else
            normN = normN / 12.92;
        return normN * 100;
    }

    private double calculateRefN(double n) {
        double refN;
        if (n > 0.008856)
            refN = Math.pow(n, 0.33333333333333);
        else
            refN = (7.787 * n) + (16.0 / 116.0);
        return refN;
    }
}
