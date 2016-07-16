package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptor
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import com.alekseyzhelo.lbm.dynamics.NoDynamics

/**
 * @author Aleks on 17-05-2016.
 */
// TODO: some kind of statistics, output, etc
// TODO: units
class LatticeD2Q9(LX: Int, LY: Int, omega: Double, dynamics: Dynamics2DQ9,
                  boundaries: List<BoundaryDescriptor>)
: LatticeD2(LX, LY, boundaries, dynamics) {

    private val leftBoundary = findBoundary(BoundaryPosition.LEFT)
    private val topBoundary = findBoundary(BoundaryPosition.TOP)
    private val rightBoundary = findBoundary(BoundaryPosition.RIGHT)
    private val bottomBoundary = findBoundary(BoundaryPosition.BOTTOM)

    override fun initCells(dynamics: Dynamics2DQ9): Array<Array<CellD2Q9>> {
        return Array(LX, { x ->
            Array(LY, {
                y ->
                createCell(dynamics, x, y)
            })
        })
    }

    private fun createCell(dynamics: Dynamics2DQ9, x: Int, y: Int): CellD2Q9 {
        val mDynamics = when (boundaryContains(x, y)?.getType()) {
            BoundaryType.INLET -> NoDynamics
            BoundaryType.OUTLET -> NoDynamics
            else -> dynamics
        }
        return CellD2Q9(mDynamics)
    }

    override fun stream() {
        super.stream()

        automaticCorners()
    }

    private fun findBoundary(position: BoundaryPosition): BoundaryCondition {
        // TODO: add check for duplicate boundaries
        for (boundary in boundaries) {
            if (boundary.position == position) {
                return boundary
            }
        }

        // TODO: improve error message
        throw IllegalStateException("Regular lattice should have all boundaries specified.")
    }

    // TODO: improve further, especially with inflow/outflow
    private fun automaticCorners() {
// left bottom
        var i = 0
        var j = 0
        var iPlus = i + 1
        var iSub = LX - 1
        var jPlus = j + 1
        var jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[8] = cells[i][j].f[6]
            }
            BoundaryType.INLET -> {
                // TODO: do nothing or do no slip?
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[8] = cells[i][j].f[6]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        bottomBoundary.streamOutgoing(i, j)

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[7] = cells[i][j].f[5]
            }
            BoundaryType.OUTLET -> {
                // TODO: do nothing or do no slip?
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[7] = cells[i][j].f[5]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        bottomBoundary.streamOutgoing(i, j)

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[5] = cells[i][j].f[7]
            }
            BoundaryType.INLET -> {
                // TODO: do nothing or do no slip?
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[5] = cells[i][j].f[7]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        topBoundary.streamOutgoing(i, j)

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]

        topBoundary.streamOutgoing(i, j)

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[6] = cells[i][j].f[8]
            }
            BoundaryType.OUTLET -> {
                // TODO: do nothing or do no slip?
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[6] = cells[i][j].f[8]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }
    }

}