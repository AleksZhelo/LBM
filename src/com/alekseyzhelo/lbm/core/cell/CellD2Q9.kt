package com.alekseyzhelo.lbm.core.cell

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9.Q
import com.alekseyzhelo.lbm.core.statistics.LatticeStatistics

/**
 * @author Aleks on 18-05-2016.
 */
// TODO: add 1 to Rho for improved numerical stability? What are the other modifications to make that work properly?
class CellD2Q9 {

    // NB: Streaming is possible with a single cells array
    // (see http://optilb.com/openlb/wp-content/uploads/2011/12/olb-tr1.pdf)

    var f = DoubleArray(Q)
        private set

    var fBuf = DoubleArray(Q)
        private set

     val U = DoubleArray(DescriptorD2Q9.D) // TODO: this takes too much memory?

    operator fun get(index: Int): Double {
        return f[index]
    }

    operator fun set(index: Int, value: Double): Unit {
        f[index] = value
    }

    /**
     * ComputeRho and computeU can be further optimized by computing them simultaneously.
     * (See OpenLB lbHelpers2D.h:132+)
     */

    // TODO: default parameters' effect on performance?
    /**
     * Added 1.0 to correctly handle zero density in other calculations.
     */
    fun computeRho(F: DoubleArray = fBuf): Double {
        return F[0] + F[1] + F[2] + F[3] + F[4] + F[5] + F[6] + F[7] + F[8]
    }

    fun computeU(Rho: Double, F: DoubleArray = fBuf): DoubleArray {
        if(Rho == 0.0){ // TODO clutch, find a way to remove
            U[0] = 0.0
            U[1] = 0.0
            return U
        }
        U[0] = (F[1] + F[5] + F[8] - F[3] - F[6] - F[7]) / Rho
        U[1] = (F[2] + F[5] + F[6] - F[4] - F[7] - F[8]) / Rho
        LatticeStatistics.gatherMaxVel(U)
        return U
    }

    fun computeRhoU(F: DoubleArray = fBuf): DoubleArray {
        val Rho = F[0] + F[1] + F[2] + F[3] + F[4] + F[5] + F[6] + F[7] + F[8]
        if(Rho == 0.0){ // TODO clutch, find a way to remove
            U[0] = 0.0
            U[1] = 0.0
            return U
        }
        U[0] = (F[1] + F[5] + F[8] - F[3] - F[6] - F[7]) / Rho
        U[1] = (F[2] + F[5] + F[6] - F[4] - F[7] - F[8]) / Rho
        LatticeStatistics.gatherMaxVel(U)
        return U
    }

    override fun toString(): String {
        val rho = computeRho(f)
        computeU(rho, f)
        return buildString {
            appendln("Density: $rho")
            appendln("Velocity: (${U[0]}, ${U[1]})")
        }
    }

    // TEST
    fun swapBuffers(): Unit {
        val tmp = f
        f = fBuf
        fBuf = tmp
    }


}