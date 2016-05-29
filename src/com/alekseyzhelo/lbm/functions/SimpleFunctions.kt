package com.alekseyzhelo.lbm.functions

/**
 * @author Aleks on 29-05-2016.
 */

// TODO: or functors?
val pressureWaveRho: (lx: Int, ly: Int, waveCenterRho: Double) -> (i: Int, j: Int) -> Double
        =
        { lx, ly, waveCenterRho ->
            val balancedRho = 1.0 - (waveCenterRho - 1.0) / (lx * ly)
            { i: Int, j: Int ->
                if (i == lx / 2 && j == ly / 2) {
                    waveCenterRho
                } else {
                    balancedRho
                }
            }
        }

val multiplePressureWaveRho: (lx: Int, ly: Int, waveX: Int, waveY: Int, waveCenterRho: Double) -> (i: Int, j: Int) -> Double
        =
        { lx, ly, waveX, waveY, waveCenterRho ->
            val balancedRho = 1.0 - (waveX * waveY * (waveCenterRho - 1.0)) / (lx * ly)
            val centerX = lx / 2
            val centerY = ly / 2
            { i: Int, j: Int ->
                when {
                    (i >= centerX - waveX / 2) && (i <= centerX + waveX / 2)
                            && (j >= centerY - waveY / 2) && (j <= centerY + waveY / 2) -> waveCenterRho
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
