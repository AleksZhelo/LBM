package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2

// TODO: does not work
class ZhouHeUXBoundary(position: BoundaryPosition, lattice: LatticeD2,
                       x0: Int, x1: Int, y0: Int, y1: Int,
                       val wallVelocity: DoubleArray) : BoundaryCondition(position, lattice, x0, x1, y0, y1) {

    override fun getType(): BoundaryType {
        return BoundaryType.ZHOU_HE_UX
    }

    override fun streamOutgoing(i: Int, j: Int) {
        when (position) {
            BoundaryPosition.BOTTOM -> {
                val rho = lattice.cells[i][j].f[0] + lattice.cells[i][j].f[1] + lattice.cells[i][j].f[3] +
                        +2.0 * (lattice.cells[i][j].f[4] + lattice.cells[i][j].f[7] + lattice.cells[i][j].f[8])

                lattice.cells[i][j].fBuf[2] = lattice.cells[i][j].f[4]
                lattice.cells[i][j].fBuf[5] = lattice.cells[i][j].f[7] - 0.5 * (lattice.cells[i][j].f[1] - lattice.cells[i][j].f[3]) + 0.5 * rho * wallVelocity[0]
                lattice.cells[i][j].fBuf[6] = lattice.cells[i][j].f[8] + 0.5 * (lattice.cells[i][j].f[1] - lattice.cells[i][j].f[3]) - 0.5 * rho * wallVelocity[0]
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
//                        val q = u_x / (2.0 * lattice.cells[i][j].computeRhoU(lattice.cells[i][j].f)[0])
//                        val p = 1.0 - q
////                        if(once){
////                            println("p: $p, q: $q, slideVel: $slideVelocity")
////                            once = false
////                        }
//
//                        lattice.cells[i][j].fBuf[0] = lattice.cells[i][j].f[0]
//                        lattice.cells[iPlus][j].fBuf[1] = lattice.cells[i][j].f[1]
//                        lattice.cells[iSub][j].fBuf[3] = lattice.cells[i][j].f[3]
//                        lattice.cells[i][jSub].fBuf[4] = lattice.cells[i][j].f[4]
//                        lattice.cells[iSub][jSub].fBuf[7] = lattice.cells[i][j].f[7]
//                        lattice.cells[iPlus][jSub].fBuf[8] = lattice.cells[i][j].f[8]
//
//                        lattice.cells[i][j].fBuf[4] = lattice.cells[i][j].f[2]
//                        lattice.cells[i][j].fBuf[7] = p * lattice.cells[i][j].f[5] + q * lattice.cells[i][j].f[6]
//                        lattice.cells[i][j].fBuf[8] = q * lattice.cells[i][j].f[5] + p * lattice.cells[i][j].f[6]
//                    }
//                }
//            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1

                        val rho = lattice.cells[i][j].f[0] + lattice.cells[i][j].f[1] + lattice.cells[i][j].f[3] +
                                +2.0 * (lattice.cells[i][j].f[4] + lattice.cells[i][j].f[7] + lattice.cells[i][j].f[8])

                        // all wrong?
                        lattice.cells[i][j].fBuf[0] = lattice.cells[i][j].f[0]
                        lattice.cells[iPlus][j].fBuf[1] = lattice.cells[i][j].f[1]
                        lattice.cells[i][jPlus].fBuf[2] = lattice.cells[i][j].f[2]
                        lattice.cells[iSub][j].fBuf[3] = lattice.cells[i][j].f[3]
                        lattice.cells[iPlus][jPlus].fBuf[5] = lattice.cells[i][j].f[5]
                        lattice.cells[iSub][jPlus].fBuf[6] = lattice.cells[i][j].f[6]

                        lattice.cells[i][j].fBuf[2] = lattice.cells[i][j].f[4]
                        lattice.cells[i][j].fBuf[5] = lattice.cells[i][j].f[7] - 0.5 * (lattice.cells[i][j].f[1] - lattice.cells[i][j].f[3]) + 0.5 * rho * wallVelocity[0]
                        lattice.cells[i][j].fBuf[6] = lattice.cells[i][j].f[8] + 0.5 * (lattice.cells[i][j].f[1] - lattice.cells[i][j].f[3]) - 0.5 * rho * wallVelocity[0]
                    }
                }
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, wallVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, wallVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), wallVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), wallVelocity)
            }
        }
    }

}