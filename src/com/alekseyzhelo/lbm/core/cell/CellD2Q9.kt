package com.alekseyzhelo.lbm.core.cell

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9.Q
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9

/**
 * @author Aleks on 18-05-2016.
 */
// TODO: add 1 to Rho for improved numerical stability? What are the other modifications to make that work properly?
class CellD2Q9(val dynamics: Dynamics2DQ9) {

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

    fun computeRho() = dynamics.computeRho(this)

    fun computeU(rho: Double) = dynamics.computeU(this, rho)

    fun computeRhoU() = dynamics.computeRhoU(this)

    fun computeBufferRho() = dynamics.computeBufferRho(this)

    fun computeBufferU(rho: Double) = dynamics.computeBufferU(this, rho)

    fun computeBufferRhoU() = dynamics.computeBufferRhoU(this)

    override fun toString(): String {
        val rho = computeRho()
        computeU(rho)
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