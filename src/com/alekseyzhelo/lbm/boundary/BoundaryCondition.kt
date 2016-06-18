package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

/**
 * @author Aleks on 18-06-2016.
 */
enum class BoundaryPosition {
    LEFT, TOP, RIGHT, BOTTOM
}

enum class BoundaryType {
    PERIODIC, NO_SLIP
}

// TODO: handle corners automatically!
abstract class BoundaryCondition(protected val lattice: LatticeD2Q9,
                                 val x0: Int, val x1: Int, val y0: Int, val y1: Int) {

    abstract fun boundaryStream()

}

class PeriodicBoundary(lattice: LatticeD2Q9,
                       x0: Int, x1: Int, y0: Int, y1: Int) : BoundaryCondition(lattice, x0, x1, y0, y1) {

    override fun boundaryStream() {
        val LX = lattice.LX
        val LY = lattice.LY

        for (i in x0..x1) {
            val iSub = if (i > 0) (i - 1) else (LX - 1)
            val iPlus = if (i < LX - 1) (i + 1) else (0)
            for (j in y0..y1) {
                val jSub = if (j > 0) (j - 1) else (LY - 1)
                val jPlus = if (j < LY - 1) (j + 1) else (0)
                // TODO if (!is_interior_solid_node[i][j]) {
                lattice.doStream(i, iPlus, iSub, j, jPlus, jSub)
                // TODO }
            }
        }
    }

}

class NoSlipBoundary(val position: BoundaryPosition, lattice: LatticeD2Q9,
                     x0: Int, x1: Int, y0: Int, y1: Int) : BoundaryCondition(lattice, x0, x1, y0, y1) {
    val cells = lattice.cells

    override fun boundaryStream() { // TOP only!!
        when (position) {
            BoundaryPosition.TOP -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jSub = j - 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0];
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

                        cells[i][j].fBuf[4] = cells[i][j].f[2];
                        cells[i][j].fBuf[7] = cells[i][j].f[5];
                        cells[i][j].fBuf[8] = cells[i][j].f[6];
                    }
                }
            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0];
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

                        cells[i][j].fBuf[2] = cells[i][j].f[4];
                        cells[i][j].fBuf[5] = cells[i][j].f[7];
                        cells[i][j].fBuf[6] = cells[i][j].f[8];
                    }
                }
            }
        }

    }

}