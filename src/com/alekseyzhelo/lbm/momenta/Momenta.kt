package com.alekseyzhelo.lbm.momenta

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.computeEquilibrium
import com.alekseyzhelo.lbm.util.computeFneq
import com.alekseyzhelo.lbm.util.normSquare

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

    fun defineRho(cell: CellD2Q9, rho: Double) {
        val oldRho = computeRho(cell)
        val U = computeU(cell, oldRho)
        val uSqr = normSquare(U)
        val fNeq = computeFneq(cell, oldRho, U)
        for (i in 0..DescriptorD2Q9.Q - 1) {
            cell[i] = computeEquilibrium(i, rho, U, uSqr) + fNeq[i]
        }
    }

    fun defineU(cell: CellD2Q9, U: DoubleArray) {
        val rho = computeRho(cell)
        val oldU = computeU(cell, rho)
        val uSqr = normSquare(U)
        val fNeq = computeFneq(cell, rho, oldU)
        for (i in 0..DescriptorD2Q9.Q - 1) {
            cell[i] = computeEquilibrium(i, rho, U, uSqr) + fNeq[i]
        }
    }

    fun defineRhoU(cell: CellD2Q9, rho: Double, U: DoubleArray) {
        val oldRho = computeRho(cell)
        val oldU = computeU(cell, rho)
        val uSqr = normSquare(U)
        val fNeq = computeFneq(cell, oldRho, oldU)
        for (i in 0..DescriptorD2Q9.Q - 1) {
            cell[i] = computeEquilibrium(i, rho, U, uSqr) + fNeq[i]
        }
    }

}

object BulkMomenta : Momenta {

    /**
     * computeRho and computeU can be further optimized by computing them simultaneously.
     * (See OpenLB lbHelpers2D.h:132+)
     */

    override fun computeRho(cell: CellD2Q9): Double {
        val rho = cell[0] + cell[1] + cell[2] + cell[3] + cell[4] + cell[5] + cell[6] + cell[7] + cell[8]
        LatticeStatistics.gatherMinMaxDensity(rho)
        return rho
    }

    override fun computeU(cell: CellD2Q9, rho: Double): DoubleArray {
        if (rho == 0.0) { // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell[1] + cell[5] + cell[8] - cell[3] - cell[6] - cell[7]) / rho
        cell.U[1] = (cell[2] + cell[5] + cell[6] - cell[4] - cell[7] - cell[8]) / rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeRhoU(cell: CellD2Q9): DoubleArray {
        val rho = cell[0] + cell[1] + cell[2] + cell[3] + cell[4] + cell[5] + cell[6] + cell[7] + cell[8]
        LatticeStatistics.gatherMinMaxDensity(rho)
        if (rho == 0.0) { // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell[1] + cell[5] + cell[8] - cell[3] - cell[6] - cell[7]) / rho
        cell.U[1] = (cell[2] + cell[5] + cell[6] - cell[4] - cell[7] - cell[8]) / rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeBufferRho(cell: CellD2Q9): Double {
        val rho = cell.fBuf[0] + cell.fBuf[1] + cell.fBuf[2] + cell.fBuf[3] + cell.fBuf[4] + cell.fBuf[5] + cell.fBuf[6] + cell.fBuf[7] + cell.fBuf[8]
        LatticeStatistics.gatherMinMaxDensity(rho)
        return rho
    }

    override fun computeBufferU(cell: CellD2Q9, rho: Double): DoubleArray {
        if (rho == 0.0) { // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell.fBuf[1] + cell.fBuf[5] + cell.fBuf[8] - cell.fBuf[3] - cell.fBuf[6] - cell.fBuf[7]) / rho
        cell.U[1] = (cell.fBuf[2] + cell.fBuf[5] + cell.fBuf[6] - cell.fBuf[4] - cell.fBuf[7] - cell.fBuf[8]) / rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeBufferRhoU(cell: CellD2Q9): DoubleArray {
        val rho = cell.fBuf[0] + cell.fBuf[1] + cell.fBuf[2] + cell.fBuf[3] + cell.fBuf[4] + cell.fBuf[5] + cell.fBuf[6] + cell.fBuf[7] + cell.fBuf[8]
        LatticeStatistics.gatherMinMaxDensity(rho)
        if (rho == 0.0) { // TODO clutch, find a way to remove
            cell.U[0] = 0.0
            cell.U[1] = 0.0
            return cell.U
        }
        cell.U[0] = (cell.fBuf[1] + cell.fBuf[5] + cell.fBuf[8] - cell.fBuf[3] - cell.fBuf[6] - cell.fBuf[7]) / rho
        cell.U[1] = (cell.fBuf[2] + cell.fBuf[5] + cell.fBuf[6] - cell.fBuf[4] - cell.fBuf[7] - cell.fBuf[8]) / rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

}

// TODO: does not work :(
class BoundaryMomenta(val position: BoundaryPosition) : Momenta {

    /**
     * computeRho and computeU can be further optimized by computing them simultaneously.
     * (See OpenLB lbHelpers2D.h:132+)
     */

    override fun computeRho(cell: CellD2Q9): Double {
        var rho = 0.0
        for (f in position.inside) {
            rho += cell[f]
        }
        LatticeStatistics.gatherMinMaxDensity(rho)
        return rho
    }

    override fun computeU(cell: CellD2Q9, rho: Double): DoubleArray {
        cell.U[0] = 0.0
        cell.U[1] = 0.0
        if (rho == 0.0) { // TODO clutch, find a way to remove
            return cell.U
        }
        for (f in position.inside) {
            cell.U[0] += cell[f] * DescriptorD2Q9.c[f][0]
            cell.U[1] += cell[f] * DescriptorD2Q9.c[f][1]
        }
        cell.U[0] /= rho
        cell.U[1] /= rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeRhoU(cell: CellD2Q9): DoubleArray {
        var rho = 0.0
        for (f in position.inside) {
            rho += cell[f]
        }
        LatticeStatistics.gatherMinMaxDensity(rho)

        cell.U[0] = 0.0
        cell.U[1] = 0.0
        if (rho == 0.0) { // TODO clutch, find a way to remove
            return cell.U
        }
        for (f in position.inside) {
            cell.U[0] += cell[f] * DescriptorD2Q9.c[f][0]
            cell.U[1] += cell[f] * DescriptorD2Q9.c[f][1]
        }
        cell.U[0] /= rho
        cell.U[1] /= rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeBufferRho(cell: CellD2Q9): Double {
        var rho = 0.0
        for (f in position.inside) {
            rho += cell.fBuf[f]
        }
        LatticeStatistics.gatherMinMaxDensity(rho)
        return rho
    }

    override fun computeBufferU(cell: CellD2Q9, rho: Double): DoubleArray {
        cell.U[0] = 0.0
        cell.U[1] = 0.0
        if (rho == 0.0) { // TODO clutch, find a way to remove
            return cell.U
        }
        for (f in position.inside) {
            cell.U[0] += cell.fBuf[f] * DescriptorD2Q9.c[f][0]
            cell.U[1] += cell.fBuf[f] * DescriptorD2Q9.c[f][1]
        }
        cell.U[0] /= rho
        cell.U[1] /= rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

    override fun computeBufferRhoU(cell: CellD2Q9): DoubleArray {
        var rho = 0.0
        for (f in position.inside) {
            rho += cell.fBuf[f]
        }
        LatticeStatistics.gatherMinMaxDensity(rho)

        cell.U[0] = 0.0
        cell.U[1] = 0.0
        if (rho == 0.0) { // TODO clutch, find a way to remove
            return cell.U
        }
        for (f in position.inside) {
            cell.U[0] += cell.fBuf[f] * DescriptorD2Q9.c[f][0]
            cell.U[1] += cell.fBuf[f] * DescriptorD2Q9.c[f][1]
        }
        cell.U[0] /= rho
        cell.U[1] /= rho
        LatticeStatistics.gatherMaxVelocity(cell.U)
        return cell.U
    }

}