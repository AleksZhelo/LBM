package com.alekseyzhelo.lbm.momenta

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.statistics.LatticeStatistics

/**
 * @author Aleks on 09-07-2016.
 */
interface Momenta {

    fun computeRho(cell: CellD2Q9): Double
    fun computeU(cell: CellD2Q9, rho: Double): DoubleArray
    fun computeRhoU(cell: CellD2Q9): DoubleArray

    fun computeBufferRho(cell: CellD2Q9): Double
    fun computeBufferU(cell: CellD2Q9, rho: Double): DoubleArray
    fun computeBufferRhoU(cell: CellD2Q9): DoubleArray

    // fun defineRho(cell: CellD2Q9, rho: Double)
    // fun defineU(cell: CellD2Q9, U: DoubleArray)
    // fun defineRhoU(cell: CellD2Q9, rho: Double, U: DoubleArray)

}

object BulkMomenta : Momenta {

    /**
     * computeRho and computeU can be further optimized by computing them simultaneously.
     * (See OpenLB lbHelpers2D.h:132+)
     */

    override fun computeRho(cell: CellD2Q9): Double {
        return cell[0] + cell[1] + cell[2] + cell[3] + cell[4] + cell[5] + cell[6] + cell[7] + cell[8]
    }

    override fun computeU(cell: CellD2Q9, rho: Double): DoubleArray {
        if(rho == 0.0){ // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell[1] + cell[5] + cell[8] - cell[3] - cell[6] - cell[7]) / rho
        cell.U[1] = (cell[2] + cell[5] + cell[6] - cell[4] - cell[7] - cell[8]) / rho
        LatticeStatistics.gatherMaxVel(cell.U)
        return cell.U
    }

    override fun computeRhoU(cell: CellD2Q9): DoubleArray {
        val rho = cell[0] + cell[1] + cell[2] + cell[3] + cell[4] + cell[5] + cell[6] + cell[7] + cell[8]
        if(rho == 0.0){ // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell[1] + cell[5] + cell[8] - cell[3] - cell[6] - cell[7]) / rho
        cell.U[1] = (cell[2] + cell[5] + cell[6] - cell[4] - cell[7] - cell[8]) / rho
        LatticeStatistics.gatherMaxVel(cell.U)
        return cell.U
    }

    override fun computeBufferRho(cell: CellD2Q9): Double {
        return cell.fBuf[0] + cell.fBuf[1] + cell.fBuf[2] + cell.fBuf[3] + cell.fBuf[4] + cell.fBuf[5] + cell.fBuf[6] + cell.fBuf[7] + cell.fBuf[8]
    }

    override fun computeBufferU(cell: CellD2Q9, rho: Double): DoubleArray {
        if(rho == 0.0){ // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell.fBuf[1] + cell.fBuf[5] + cell.fBuf[8] - cell.fBuf[3] - cell.fBuf[6] - cell.fBuf[7]) / rho
        cell.U[1] = (cell.fBuf[2] + cell.fBuf[5] + cell.fBuf[6] - cell.fBuf[4] - cell.fBuf[7] - cell.fBuf[8]) / rho
        LatticeStatistics.gatherMaxVel(cell.U)
        return cell.U
    }

    override fun computeBufferRhoU(cell: CellD2Q9): DoubleArray {
        val rho = cell.fBuf[0] + cell.fBuf[1] + cell.fBuf[2] + cell.fBuf[3] + cell.fBuf[4] + cell.fBuf[5] + cell.fBuf[6] + cell.fBuf[7] + cell.fBuf[8]
        if(rho == 0.0){ // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell.fBuf[1] + cell.fBuf[5] + cell.fBuf[8] - cell.fBuf[3] - cell.fBuf[6] - cell.fBuf[7]) / rho
        cell.U[1] = (cell.fBuf[2] + cell.fBuf[5] + cell.fBuf[6] - cell.fBuf[4] - cell.fBuf[7] - cell.fBuf[8]) / rho
        LatticeStatistics.gatherMaxVel(cell.U)
        return cell.U
    }

}