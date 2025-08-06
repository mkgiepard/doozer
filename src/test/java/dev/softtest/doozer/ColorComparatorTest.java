package dev.softtest.doozer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

// source of conversion data: https://www.easyrgb.com/en/convert.php
// source of delta_e results: https://colormine.org/delta-e-calculator
public class ColorComparatorTest {
    public ColorComparator comparator = new ColorComparator();
    double OK_DIFF = 0.05;
    double OK_DIFF_DELTA_E = 0.5;
    
    @Test
    public void test_rgb2xyz_000() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {0, 0, 0});
        assertEquals(0, xyz[0], OK_DIFF);
        assertEquals(0, xyz[1], OK_DIFF);
        assertEquals(0, xyz[2], OK_DIFF);
    }

    @Test
    public void test_rgb2xyz_255255255() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {255, 255, 255});
        assertEquals(95.047, xyz[0], OK_DIFF);
        assertEquals(100.000, xyz[1], OK_DIFF);
        assertEquals(108.883, xyz[2], OK_DIFF); 
    }

    @Test
    public void test_rgb2xyz_128128128() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {128, 128, 128});
        assertEquals(20.517, xyz[0], OK_DIFF);
        assertEquals(21.586, xyz[1], OK_DIFF);
        assertEquals(23.504, xyz[2], OK_DIFF);
    }

    @Test
    public void test_rgb2xyz_255000000() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {255, 0, 0});
        assertEquals(41.246, xyz[0], OK_DIFF);
        assertEquals(21.267, xyz[1], OK_DIFF);
        assertEquals(1.933, xyz[2], OK_DIFF);
    }

    @Test
    public void test_rgb2xyz_000255000() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {0, 255, 0});
        assertEquals(35.758, xyz[0], OK_DIFF);
        assertEquals(71.515, xyz[1], OK_DIFF);
        assertEquals(11.919, xyz[2], OK_DIFF);      
    }

    @Test
    public void test_rgb2xyz_000000255() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {0, 0, 255});
        assertEquals(18.044, xyz[0], OK_DIFF);
        assertEquals(7.217, xyz[1], OK_DIFF);
        assertEquals(95.040, xyz[2], OK_DIFF);            
    }

    @Test
    public void test_rgb2xyz_066133244() throws Exception {
        double[] xyz = comparator.rgb2xyz(new int[] {66, 133, 244});
        assertEquals(26.958, xyz[0], OK_DIFF);
        assertEquals(24.462, xyz[1], OK_DIFF);
        assertEquals(88.871 , xyz[2], OK_DIFF);     
    }

    @Test
    public void test_rgb2cielab_000() {
        double[] cielab = comparator.rgb2cielab(new int[] {0, 0, 0});
        assertEquals(0, cielab[0], OK_DIFF);
        assertEquals(0, cielab[1], OK_DIFF);
        assertEquals(0 , cielab[2], OK_DIFF);  
    }

    @Test
    public void test_rgb2cielab_255255255() {
        double[] cielab = comparator.rgb2cielab(new int[] {255, 255, 255});
        assertEquals(100, cielab[0], OK_DIFF);
        assertEquals(0, cielab[1], OK_DIFF);
        assertEquals(0 , cielab[2], OK_DIFF);  
    }

    @Test
    public void test_rgb2cielab_128128128() {
        double[] cielab = comparator.rgb2cielab(new int[] {128, 128, 128});
        assertEquals(53.585, cielab[0], OK_DIFF);
        assertEquals(0, cielab[1], OK_DIFF);
        assertEquals(0 , cielab[2], OK_DIFF);  
    }

    @Test
    public void test_rgb2cielab_066133244() throws Exception {
        double[] cielab = comparator.rgb2cielab(new int[] {66, 133, 244});
        assertEquals(56.547, cielab[0], OK_DIFF);
        assertEquals( 15.807, cielab[1], OK_DIFF);
        assertEquals(-61.827, cielab[2], OK_DIFF);
    }

    @Test
    public void test_compareColors_000_000() {
        int[] a = new int[] {0, 0, 0};
        int[] b = new int[] {0, 0, 0};
        assertEquals(0, comparator.compareColors(a, b));
    }

    @Test
    public void test_compareColors_000_255255255() {
        int[] a = new int[] {0, 0, 0};
        int[] b = new int[] {255, 255, 255};
        assertEquals(100, comparator.compareColors(a, b), OK_DIFF_DELTA_E);
    }

    @Test
    public void test_compareColors_128128128_255255255() {
        int[] a = new int[] {128, 128, 128};
        int[] b = new int[] {255, 255, 255};
        assertEquals(46.15, comparator.compareColors(a, b), OK_DIFF_DELTA_E);
    }
    
    @Test
    public void test_compareColors_123244012_031011177() {
        int[] a = new int[] {123, 244, 12};
        int[] b = new int[] {31, 11, 177};
        assertEquals(212.40, comparator.compareColors(a, b), OK_DIFF_DELTA_E);
    }     
}
