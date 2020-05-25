package com.alekseyzhelo.lbm.functions

/**
 * @author Aleks on 29-05-2016.
 */

// TODO: or functors?
// TODO: should I turn these into usual functions? What is the difference between declaring them like this
// TODO: and the usual way?
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

val columnPressureWaveRho =
        { lx: Int, ly: Int, waveXPos: Int, waveRho: Double ->
            val balancedRho = 1.0 - (ly * (waveRho - 1.0)) / (lx * ly)
            { i: Int, j: Int ->
                when {
                    (i == waveXPos) -> waveRho
                    else -> balancedRho
                }
            }
        }

val rowPressureWaveRho =
        { lx: Int, ly: Int, waveYPos: Int, waveRho: Double ->
            val balancedRho = 1.0 - (lx * (waveRho - 1.0)) / (lx * ly)
            { i: Int, j: Int ->
                when {
                    (j == waveYPos) -> waveRho
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

/**
 * @param L height of the lattice
 * @param kNum wave number k
 */
val shearWaveVelocity = { L: Double, kNum: Int, a0: Double ->
    val k = (1 + kNum) * 2.0 * Math.PI / L
    { i: Int, j: Int ->
        doubleArrayOf(a0 * Math.sin(k * j), 0.0)
    }
}

val shearWaveMaxVelocityY = { L: Double, kNum: Int ->
    val period = L / (1 + kNum).toDouble()
    (period / 4).toInt()
}
