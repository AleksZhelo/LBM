package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.util.opposite

class NoSlipBoundary(val position: BoundaryPosition, lattice: LatticeD2Q9,
                     x0: Int, x1: Int, y0: Int, y1: Int) : BoundaryCondition(lattice, x0, x1, y0, y1) {

    val cells = lattice.cells

    override fun getType(): BoundaryType {
        return BoundaryType.NO_SLIP
    }

    override fun getParam(): Double? {
        return null
    }

    override fun streamOutgoing(i: Int, j: Int) {
        for (f in position.outgoing) {
            cells[i][j].fBuf[opposite[f]] = cells[i][j].f[f]
        }
    }

    override fun boundaryStream() {
        for (i in x0..x1) {
            for (j in y0..y1) {
                for (f in position.inside) {
                    cells[i + DescriptorD2Q9.c[f][0]][j + DescriptorD2Q9.c[f][1]].fBuf[f] = cells[i][j].f[f]
                }
                for (f in position.outgoing) {
                    cells[i][j].fBuf[opposite[f]] = cells[i][j].f[f]
                }
            }
        }
    }

    // TODO: compare speed?
    private fun boundaryStreamOld() {
        when (position) {
            BoundaryPosition.LEFT -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    for (j in y0..y1) {
                        val jSub = j - 1
                        val jPlus = j + 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0]
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
                        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
                        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
                        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]

                        cells[i][j].fBuf[1] = cells[i][j].f[3]
                        cells[i][j].fBuf[5] = cells[i][j].f[7]
                        cells[i][j].fBuf[8] = cells[i][j].f[6]
                    }
                }
            }

            BoundaryPosition.TOP -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jSub = j - 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0]
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
                        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]
                        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]

                        cells[i][j].fBuf[4] = cells[i][j].f[2]
                        cells[i][j].fBuf[7] = cells[i][j].f[5]
                        cells[i][j].fBuf[8] = cells[i][j].f[6]
                    }
                }
            }

            BoundaryPosition.RIGHT -> {
                for (i in x0..x1) {
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jSub = j - 1
                        val jPlus = j + 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0]
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
                        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]
                        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]

                        cells[i][j].fBuf[3] = cells[i][j].f[1]
                        cells[i][j].fBuf[6] = cells[i][j].f[8]
                        cells[i][j].fBuf[7] = cells[i][j].f[5]
                    }
                }
            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1
                        cells[i][j].fBuf[0] = cells[i][j].f[0]
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
                        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]

                        cells[i][j].fBuf[2] = cells[i][j].f[4]
                        cells[i][j].fBuf[5] = cells[i][j].f[7]
                        cells[i][j].fBuf[6] = cells[i][j].f[8]
                    }
                }
            }
        }

    }

}