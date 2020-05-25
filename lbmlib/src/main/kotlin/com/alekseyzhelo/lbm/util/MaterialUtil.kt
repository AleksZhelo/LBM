package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.cell.Material
import com.alekseyzhelo.lbm.core.cell.MaterialCellD2Q9

/**
 * @author Aleks on 17-07-2016.
 */

object MaterialUtil {

    private var inletRho: Double = 0.0
    private var inletVelocity: DoubleArray = doubleArrayOf()
    private var uSqr: Double = 0.0

    fun configure(inletRho: Double, inletVelocity: DoubleArray) {
        this.inletRho = inletRho
        this.inletVelocity = inletVelocity
        this.uSqr = normSquare(inletVelocity)
    }

    fun streamFlow(cell: MaterialCellD2Q9, nextCell: MaterialCellD2Q9, f: Int) {
        when (nextCell.material) {
            Material.NOTHING -> cell.fBuf[opposite[f]] = cell[f] // shouldn't happen
            Material.FLOW -> nextCell.fBuf[f] = cell[f]
            Material.SOLID -> cell.fBuf[opposite[f]] = cell[f]
            Material.INFLOW -> nextCell.fBuf[f] = cell[f]
            Material.OUTFLOW -> nextCell.fBuf[f] = cell[f]
        }
    }

    fun streamInflow(cell: MaterialCellD2Q9, nextCell: MaterialCellD2Q9, f: Int) {
        when (nextCell.material) {
            Material.NOTHING -> Unit // do nothing, right?
            Material.FLOW -> {
                nextCell.fBuf[f] = cell[f]
                cell[f] = computeEquilibrium(f, inletRho, inletVelocity, uSqr)
            }
            Material.SOLID -> cell.fBuf[opposite[f]] = cell[f]
            Material.INFLOW -> nextCell.fBuf[f] = cell[f]
            Material.OUTFLOW -> nextCell.fBuf[f] = cell[f] // shouldn't happen
        }
    }

    fun streamOutflow(cell: MaterialCellD2Q9, nextCell: MaterialCellD2Q9, f: Int) {
        when (nextCell.material) {
            Material.NOTHING -> cell.fBuf[opposite[f]] = reflectionProbability(cell.computeRhoU()) * cell[f]
            Material.FLOW -> nextCell.fBuf[f] = cell[f]
            Material.SOLID -> cell.fBuf[opposite[f]] = cell[f]
            Material.INFLOW -> nextCell.fBuf[f] = cell[f] // shouldn't happen
            Material.OUTFLOW -> nextCell.fBuf[f] = cell[f]
        }
    }


    fun reflectionProbability(U: DoubleArray): Double {
        val u = U[1]
        val r = 1.0 + 4.0 * (u - inletVelocity[0]) / (1.0 - 2.0 * u)
        return if (r > 1.0) 1.0 else if (r < 0.0) 0.0 else r
    }
}