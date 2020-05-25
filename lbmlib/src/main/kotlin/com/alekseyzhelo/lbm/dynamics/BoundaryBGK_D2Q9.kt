package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.momenta.BoundaryMomenta

/**
 * @author Aleks on 09-07-2016.
 */
class BoundaryBGK_D2Q9(omega: Double, position: BoundaryPosition) : BGKDynamicsD2Q9(omega) {

    val momenta = BoundaryMomenta(position)

    override fun computeRho(cell: CellD2Q9) = momenta.computeRho(cell)

    override fun computeU(cell: CellD2Q9, rho: Double) = momenta.computeU(cell, rho)

    override fun computeRhoU(cell: CellD2Q9) = momenta.computeRhoU(cell)

    override fun computeBufferRho(cell: CellD2Q9) = momenta.computeBufferRho(cell)

    override fun computeBufferU(cell: CellD2Q9, rho: Double) = momenta.computeBufferU(cell, rho)

    override fun computeBufferRhoU(cell: CellD2Q9) = momenta.computeBufferRhoU(cell)

    override fun defineRho(cell: CellD2Q9, rho: Double) = momenta.defineRho(cell, rho)

    override fun defineU(cell: CellD2Q9, U: DoubleArray) = momenta.defineU(cell, U)

    override fun defineRhoU(cell: CellD2Q9, rho: Double, U: DoubleArray) = momenta.defineRhoU(cell, rho, U)

}