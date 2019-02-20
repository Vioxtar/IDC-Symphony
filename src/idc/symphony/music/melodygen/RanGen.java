package idc.symphony.music.melodygen;

import java.util.Random;

/**
 * Random wrapper for semi-determinism.
 */

public class RanGen {

    public RanGen(long seed) {
        resetGen(seed);
    }

    Random gen = new Random();
    public void setRanSeed(long seed) {
        gen.setSeed(seed);
    }
    public void resetGen(long seed) {
        gen = new Random();
        gen.setSeed(seed);
    }

    /**
     * Returns a uniform-random integer between any two numbers.
     * @param a
     * @param b
     * @return
     */
    public int ranRange(int a, int b){
        if (a == b) {
            return a;
        }
        int min = Math.min(a, b);
        int max = Math.max(a, b);
        double ranBase = gen.nextDouble();
        int diff = max - min + 1;
        int ran = (int)((ranBase * diff) + min);
        return ran;
    }

    /**
     * Returns a uniform-random double between any two numbers.
     * @param a
     * @param b
     * @return
     */
    public double ranRange(double a, double b){
        if (a == b) {
            return a;
        }
        double min = Math.min(a, b);
        double max = Math.max(a, b);
        double ranBase = gen.nextDouble();
        double diff = max - min;
        double ran = (ranBase * diff) + min;
        return ran;
    }

}
