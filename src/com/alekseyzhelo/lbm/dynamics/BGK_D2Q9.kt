package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9.c
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9.w
import com.alekseyzhelo.lbm.momenta.BulkMomenta

/**
 * @author Aleks on 18-05-2016.
 */


open class BGKDynamicsD2Q9(val omega: Double) : Dynamics2DQ9 {

    // TODO field vs local var vs .. performance effect?
    val eqMult1 = 3.0
    val eqMult2 = 9.0 / 2.0
    val eqMult3 = 3.0 / 2.0

    // TODO experiment with optimizations here
    override fun collide(cell: CellD2Q9): Unit {
        val rho = computeBufferRho(cell)
        val U = computeBufferU(cell, rho)

        val ux = U[0]
        val uy = U[1]
        val uxSqr = ux * ux
        val uySqr = uy * uy
        val uSqr_3_2 = (uxSqr + uySqr) * eqMult3

        var rho_ = 4.0 / 9.0 * rho

        cell[0] = (1 - omega) * cell.fBuf[0] +
                omega * (rho_ * (1 - uSqr_3_2))

        rho_ = 1.0 / 9.0 * rho

        cell[1] = (1 - omega) * cell.fBuf[1] +
                omega * (rho_ * (1 + eqMult1 * ux + eqMult2 * uxSqr - uSqr_3_2))
        cell[2] = (1 - omega) * cell.fBuf[2] +
                omega * (rho_ * (1 + eqMult1 * uy + eqMult2 * uySqr - uSqr_3_2))
        cell[3] = (1 - omega) * cell.fBuf[3] +
                omega * (rho_ * (1 - eqMult1 * ux + eqMult2 * uxSqr - uSqr_3_2))
        cell[4] = (1 - omega) * cell.fBuf[4] +
                omega * (rho_ * (1 - eqMult1 * uy + eqMult2 * uySqr - uSqr_3_2))

        rho_ = 1.0 / 36.0 * rho

        cell[5] = (1 - omega) * cell.fBuf[5] +
                omega * (rho_ * (1 + eqMult1 * (ux + uy) + eqMult2 * (ux + uy) * (ux + uy) - uSqr_3_2))
        cell[6] = (1 - omega) * cell.fBuf[6] +
                omega * (rho_ * (1 + eqMult1 * (-ux + uy) + eqMult2 * (-ux + uy) * (-ux + uy) - uSqr_3_2))
        cell[7] = (1 - omega) * cell.fBuf[7] +
                omega * (rho_ * (1 + eqMult1 * (-ux - uy) + eqMult2 * (-ux - uy) * (-ux - uy) - uSqr_3_2))
        cell[8] = (1 - omega) * cell.fBuf[8] +
                omega * (rho_ * (1 + eqMult1 * (ux - uy) + eqMult2 * (ux - uy) * (ux - uy) - uSqr_3_2))
    }

    override fun toString(): String {
        return buildString { appendln("BGK dynamics. Omega: $omega") }
    }
}