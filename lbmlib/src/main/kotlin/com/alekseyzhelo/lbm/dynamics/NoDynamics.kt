package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9

/**
 * @author Aleks on 18-05-2016.
 */


object NoDynamics : Dynamics2DQ9 {
    override fun collide(cell: CellD2Q9): Unit {
        val rho = computeBufferRho(cell)
        computeBufferU(cell, rho)
    }

    override fun toString(): String {
        return buildString { appendln("No dynamics.") }
    }
}