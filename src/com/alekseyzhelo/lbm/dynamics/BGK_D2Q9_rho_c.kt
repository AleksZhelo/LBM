package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9

/**
 * @author Aleks on 18-05-2016.
 */

/**
 * To use with rho + 1 and no ifs in U calculation
 */
// TODO: finish or remove
abstract class BGKDynamicsD2Q9_rho_c(val omega: Double) : Dynamics2DQ9 {

    // TODO correct? use?
    fun computeEquilibrium(i: Int, Rho: Double, U: DoubleArray, uSqr: Double): Double {
        val c_u = DescriptorD2Q9.c[i][0] * U[0] + DescriptorD2Q9.c[i][1] * U[1]
        return Rho * DescriptorD2Q9.w[i] * (1 + 3.0 * c_u + 4.5 * c_u * c_u - 1.5 * uSqr)
    }

    // TODO field vs local var vs .. performance effect?
    val eqMult1 = 3.0
    val eqMult2 = 9.0 / 2.0
    val eqMult3 = 3.0 / 2.0

    // TODO experiment with optimizations here
    override fun collide(cell: CellD2Q9): Unit {
        val rho = cell.computeRho()
        val U = cell.computeU(rho)

        val ux = U[0]
        val uy = U[1]
        val uxSqr = ux * ux
        val uySqr = uy * uy
        val uSqr_3_2 = (uxSqr + uySqr) * eqMult3

        var rho_c = 4.0 / 9.0 * (rho - 1)
        var rho_ = 4.0 / 9.0 * rho

        cell[0] = (1 - omega) * cell.fBuf[0] +
                omega * (rho_c + rho_ * (-uSqr_3_2))

        rho_c = 1.0 / 9.0 * (rho - 1)
        rho_ = 1.0 / 9.0 * rho

        cell[1] = (1 - omega) * cell.fBuf[1] +
                omega * (rho_c + rho_ * (eqMult1 * ux + eqMult2 * uxSqr - uSqr_3_2))
        cell[2] = (1 - omega) * cell.fBuf[2] +
                omega * (rho_c + rho_ * (eqMult1 * uy + eqMult2 * uySqr - uSqr_3_2))
        cell[3] = (1 - omega) * cell.fBuf[3] +
                omega * (rho_c + rho_ * (eqMult1 * ux + eqMult2 * uxSqr - uSqr_3_2))
        cell[4] = (1 - omega) * cell.fBuf[4] +
                omega * (rho_c + rho_ * (eqMult1 * uy + eqMult2 * uySqr - uSqr_3_2))

        rho_c = 1.0 / 36.0 * (rho - 1)
        rho_ = 1.0 / 36.0 * rho

        cell[5] = (1 - omega) * cell.fBuf[5] +
                omega * (rho_c + rho_ * (eqMult1 * (ux + uy) + eqMult2 * (ux + uy) * (ux + uy) - uSqr_3_2))
        cell[6] = (1 - omega) * cell.fBuf[6] +
                omega * (rho_c + rho_ * (eqMult1 * (-ux + uy) + eqMult2 * (-ux + uy) * (-ux + uy) - uSqr_3_2))
        cell[7] = (1 - omega) * cell.fBuf[7] +
                omega * (rho_c + rho_ * (eqMult1 * (-ux - uy) + eqMult2 * (-ux - uy) * (-ux - uy) - uSqr_3_2))
        cell[8] = (1 - omega) * cell.fBuf[8] +
                omega * (rho_c + rho_ * (eqMult1 * (ux - uy) + eqMult2 * (ux - uy) * (ux - uy) - uSqr_3_2))
    }

    override fun toString(): String {
        return buildString { appendln("BGK dynamics. Omega: $omega") }
    }
}