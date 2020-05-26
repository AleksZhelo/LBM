package com.alekseyzhelo.lbm.core.lattice;

/**
 * @author Aleks on 17-05-2016.
 */
public final class DescriptorD2Q9 {

    public static final int D = 2;
    public static final int Q = 9;
    public static final int[][] c = new int[][]{ //speeds
            {0, 0},                           // 0
            {1, 0}, {0, 1}, {-1, 0}, {0, -1}, // 1, 2, 3, 4
            {1, 1}, {-1, 1}, {-1, -1}, {1, -1} // 5, 6, 7, 8
    };
    private static final double W_0 = 4.0 / 9.0;
    private static final double W_1 = 1.0 / 9.0;
    private static final double W_2 = 1.0 / 36.0;
    public static final double[] w = new double[]{ //weights
            W_0,                // 0
            W_1, W_1, W_1, W_1, // 1, 2, 3, 4
            W_2, W_2, W_2, W_2  // 5, 6, 7, 8
    };

    private DescriptorD2Q9() {

    }

}
