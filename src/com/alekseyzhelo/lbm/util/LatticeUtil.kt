package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

/**
 * @author Aleks on 29-05-2016.
 */
fun LatticeD2Q9.minDensity(): Double { // TODO can be called without a lattice? WTF?
    var min = Double.MAX_VALUE
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho(cells[i][j].f)
            if (Rho < min) min = Rho
        }
    }

    return min
}

fun LatticeD2Q9.maxDensity(): Double { // TODO can be called without a lattice? WTF?
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho(cells[i][j].f)
            if (Rho > max) max = Rho
        }
    }

    return max
}

fun LatticeD2Q9.maxVelocityNorm(): Double { // TODO can be called without a lattice? WTF?
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val norm = norm(cells[i][j].computeRhoU(cells[i][j].f))
            if (norm > max) max = norm
        }
    }

    return max
}