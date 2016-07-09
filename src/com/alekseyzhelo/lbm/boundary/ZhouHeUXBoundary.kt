package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import javax.naming.OperationNotSupportedException

// TODO: does not work
class ZhouHeUXBoundary(val position: BoundaryPosition, lattice: LatticeD2Q9,
                       x0: Int, x1: Int, y0: Int, y1: Int,
                       val u_x: Double) : BoundaryCondition(lattice, x0, x1, y0, y1) {

    val cells = lattice.cells

    override fun defineBoundaryRhoU(rho: Double, U: DoubleArray) {
        val boundaryU = when (position) {
            BoundaryPosition.TOP -> doubleArrayOf(u_x, 0.0)
            BoundaryPosition.BOTTOM -> doubleArrayOf(u_x, 0.0)
            else -> throw OperationNotSupportedException()
        }

        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, boundaryU)
            }
        }
    }

    override fun getType(): BoundaryType {
        return BoundaryType.SLIDING
    }

    override fun getParam(): Double? {
        return u_x
    }

    override fun streamOutgoing(i: Int, j: Int) {
        when (position) {
            BoundaryPosition.BOTTOM -> {
                val rho = cells[i][j].f[0] + cells[i][j].f[1] + cells[i][j].f[3] +
                        +2.0 * (cells[i][j].f[4] + cells[i][j].f[7] + cells[i][j].f[8])

                cells[i][j].fBuf[2] = cells[i][j].f[4]
                cells[i][j].fBuf[5] = cells[i][j].f[7] - 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) + 0.5 * rho * u_x
                cells[i][j].fBuf[6] = cells[i][j].f[8] + 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) - 0.5 * rho * u_x
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }
    }

    override fun boundaryStream() {
        when (position) {
//            BoundaryPosition.TOP -> {
//                //var once = true
//                for (i in x0..x1) {
//                    val iPlus = i + 1
//                    val iSub = i - 1
//                    for (j in y0..y1) {
//                        val jSub = j - 1
//
//                        val q = u_x / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0])
//                        val p = 1.0 - q
////                        if(once){
////                            println("p: $p, q: $q, slideVel: $slideVelocity")
////                            once = false
////                        }
//
//                        cells[i][j].fBuf[0] = cells[i][j].f[0]
//                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
//                        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
//                        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
//                        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]
//                        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]
//
//                        cells[i][j].fBuf[4] = cells[i][j].f[2]
//                        cells[i][j].fBuf[7] = p * cells[i][j].f[5] + q * cells[i][j].f[6]
//                        cells[i][j].fBuf[8] = q * cells[i][j].f[5] + p * cells[i][j].f[6]
//                    }
//                }
//            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1

                        val rho = cells[i][j].f[0] + cells[i][j].f[1] + cells[i][j].f[3] +
                                +2.0 * (cells[i][j].f[4] + cells[i][j].f[7] + cells[i][j].f[8])

                        // all wrong?
                        cells[i][j].fBuf[0] = cells[i][j].f[0]
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
                        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]

                        cells[i][j].fBuf[2] = cells[i][j].f[4]
                        cells[i][j].fBuf[5] = cells[i][j].f[7] - 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) + 0.5 * rho * u_x
                        cells[i][j].fBuf[6] = cells[i][j].f[8] + 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) - 0.5 * rho * u_x
                    }
                }
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

    }

}