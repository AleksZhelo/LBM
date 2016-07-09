package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.util.normSquare

/**
 * @author Aleks on 18-05-2016.
 */
interface Dynamics2DQ9 {

    fun iniEquilibrium(cell: CellD2Q9, Rho: Double, U: DoubleArray): Unit {
        val uSqr = normSquare(U)
        for (i in 0..DescriptorD2Q9.Q - 1) {
            cell[i] = computeEquilibrium(i, Rho, U, uSqr)
        }
    }

    fun computeEquilibrium(i: Int, Rho: Double, U: DoubleArray, uSqr: Double): Double
    fun collide(cell: CellD2Q9): Unit

    fun computeRho(cell: CellD2Q9): Double
    fun computeU(cell: CellD2Q9, rho: Double): DoubleArray
    fun computeRhoU(cell: CellD2Q9): DoubleArray

    fun computeBufferRho(cell: CellD2Q9): Double
    fun computeBufferU(cell: CellD2Q9, rho: Double): DoubleArray
    fun computeBufferRhoU(cell: CellD2Q9): DoubleArray

}