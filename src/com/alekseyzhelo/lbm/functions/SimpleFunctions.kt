package com.alekseyzhelo.lbm.functions

/**
 * @author Aleks on 29-05-2016.
 */

// TODO: or functors?
val pressureWaveRho: (lx: Int, ly: Int) -> (i: Int, j: Int) -> Double
        =
        { lx, ly ->
            val balancedRho = 1.0 - 0.4 / (lx * ly)
            { i: Int, j: Int ->
                if (i == lx / 2 && j == ly / 2) {
                    1.4
                } else {
                    balancedRho
                }
            }
        }

val triplePressureWaveRho: (lx: Int, ly: Int) -> (i: Int, j: Int) -> Double
        =
        { lx, ly ->
            val balancedRho = 1.0 - 3 * 0.4 / lx * ly
            { i: Int, j: Int ->
                when {
                    i == 5 && j == 5 -> 1.4
                    i == 3 && j == 7 -> 1.4
                    i == 7 && j == 3 -> 1.4
                    else -> balancedRho
                }
            }
        }

val diagonalVelocity = { i: Int, j: Int ->
    if ((i <= 7 && i >= 4) && (j <= 7 && j >= 4)) {
        doubleArrayOf(0.5, -0.5)
    } else {
        doubleArrayOf(0.0, 0.0)
    }
}
