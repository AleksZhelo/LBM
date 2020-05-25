package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9

/**
 * @author Aleks on 18-05-2016.
 */


object VoidDynamics : Dynamics2DQ9 {

    private val voidSpeed = doubleArrayOf(0.0, 0.0)

    override fun collide(cell: CellD2Q9) = Unit

    override fun computeRho(cell: CellD2Q9) = 0.0
    override fun computeU(cell: CellD2Q9, rho: Double) = voidSpeed
    override fun computeRhoU(cell: CellD2Q9) = voidSpeed

    override fun computeBufferRho(cell: CellD2Q9) = 0.0
    override fun computeBufferU(cell: CellD2Q9, rho: Double) = voidSpeed
    override fun computeBufferRhoU(cell: CellD2Q9) = voidSpeed

    override fun defineRho(cell: CellD2Q9, rho: Double) = Unit
    override fun defineU(cell: CellD2Q9, U: DoubleArray) = Unit
    override fun defineRhoU(cell: CellD2Q9, rho: Double, U: DoubleArray) = Unit

    override fun toString(): String {
        return buildString { appendln("Void dynamics.") }
    }

}