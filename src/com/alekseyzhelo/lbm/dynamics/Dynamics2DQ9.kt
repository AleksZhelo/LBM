package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.momenta.BulkMomenta

/**
 * @author Aleks on 18-05-2016.
 */
interface Dynamics2DQ9 {

    fun collide(cell: CellD2Q9): Unit

    fun computeRho(cell: CellD2Q9) = BulkMomenta.computeRho(cell)
    fun computeU(cell: CellD2Q9, rho: Double) = BulkMomenta.computeU(cell, rho)
    fun computeRhoU(cell: CellD2Q9) = BulkMomenta.computeRhoU(cell)

    fun computeBufferRho(cell: CellD2Q9) = BulkMomenta.computeBufferRho(cell)
    fun computeBufferU(cell: CellD2Q9, rho: Double) = BulkMomenta.computeBufferU(cell, rho)
    fun computeBufferRhoU(cell: CellD2Q9) = BulkMomenta.computeBufferRhoU(cell)

    fun defineRho(cell: CellD2Q9, rho: Double) = BulkMomenta.defineRho(cell, rho)
    fun defineU(cell: CellD2Q9, U: DoubleArray) = BulkMomenta.defineU(cell, U)
    fun defineRhoU(cell: CellD2Q9, rho: Double, U: DoubleArray) = BulkMomenta.defineRhoU(cell, rho, U)

}