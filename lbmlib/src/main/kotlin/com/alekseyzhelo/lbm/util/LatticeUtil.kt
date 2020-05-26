package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.lattice.LatticeD2

/**
 * @author Aleks on 29-05-2016.
 */
fun LatticeD2<*>.minDensity(): Double {
    var min = Double.MAX_VALUE
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho()
            if (Rho < min) min = Rho
        }
    }

    return min
}

fun LatticeD2<*>.maxDensity(): Double {
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho()
            if (Rho > max) max = Rho
        }
    }

    return max
}

fun LatticeD2<*>.minVelocityNorm(): Double {
    var min = Double.MAX_VALUE
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val norm = norm(cells[i][j].computeRhoU())
            if (norm < min) min = norm
        }
    }

    return min
}

fun LatticeD2<*>.maxVelocityNorm(): Double {
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val norm = norm(cells[i][j].computeRhoU())
            if (norm > max) max = norm
        }
    }

    return max
}