package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2

class SlidingBoundary(position: BoundaryPosition, lattice: LatticeD2,
                      x0: Int, x1: Int, y0: Int, y1: Int,
                      val slideVelocity: DoubleArray) : BoundaryCondition(position, lattice, x0, x1, y0, y1) {

    override fun getType(): BoundaryType {
        return BoundaryType.SLIDING
    }

    override fun streamOutgoing(i: Int, j: Int) {
        val coordVel = when(position) {
            BoundaryPosition.LEFT -> slideVelocity[1]
            BoundaryPosition.TOP -> slideVelocity[1]
            BoundaryPosition.RIGHT -> slideVelocity[1]
            BoundaryPosition.BOTTOM -> slideVelocity[1]
        }
        val q = coordVel / (2.0 * lattice.cells[i][j].computeRhoU()[0])
        val p = 1.0 - q

        when (position) {
            BoundaryPosition.TOP -> {
                lattice.cells[i][j].fBuf[4] = lattice.cells[i][j].f[2]
                lattice.cells[i][j].fBuf[7] = p * lattice.cells[i][j].f[5] + q * lattice.cells[i][j].f[6]
                lattice.cells[i][j].fBuf[8] = q * lattice.cells[i][j].f[5] + p * lattice.cells[i][j].f[6]
            }
            BoundaryPosition.BOTTOM -> {
                lattice.cells[i][j].fBuf[2] = lattice.cells[i][j].f[4]
                lattice.cells[i][j].fBuf[5] = p * lattice.cells[i][j].f[7] + q * lattice.cells[i][j].f[8]
                lattice.cells[i][j].fBuf[6] = q * lattice.cells[i][j].f[7] + p * lattice.cells[i][j].f[8]
            }
            else -> { throw UnsupportedOperationException("not implemented yet")}
        }
    }

    override fun boundaryStream() {
        when (position) {
            BoundaryPosition.TOP -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jSub = j - 1

                        // TODO: try: only 4, 7, 8 are inside and should be considered for rho and U
                        val q = slideVelocity[0] / (2.0 * lattice.cells[i][j].computeRhoU()[0])
                        val p = 1.0 - q

                        lattice.cells[i][j].fBuf[0] = lattice.cells[i][j].f[0]
                        lattice.cells[iPlus][j].fBuf[1] = lattice.cells[i][j].f[1]
                        lattice.cells[iSub][j].fBuf[3] = lattice.cells[i][j].f[3]
                        lattice.cells[i][jSub].fBuf[4] = lattice.cells[i][j].f[4]
                        lattice.cells[iSub][jSub].fBuf[7] = lattice.cells[i][j].f[7]
                        lattice.cells[iPlus][jSub].fBuf[8] = lattice.cells[i][j].f[8]

                        lattice.cells[i][j].fBuf[4] = lattice.cells[i][j].f[2]
                        lattice.cells[i][j].fBuf[7] = p * lattice.cells[i][j].f[5] + q * lattice.cells[i][j].f[6]
                        lattice.cells[i][j].fBuf[8] = q * lattice.cells[i][j].f[5] + p * lattice.cells[i][j].f[6]
                    }
                }
            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1

                        val q = slideVelocity[0] / (2.0 * lattice.cells[i][j].computeRhoU()[0]) // TODO: correct as approximation to rho_w * u_w / (2.0 * f_8 - f_7)?
                        val p = 1.0 - q

                        lattice.cells[i][j].fBuf[0] = lattice.cells[i][j].f[0]
                        lattice.cells[iPlus][j].fBuf[1] = lattice.cells[i][j].f[1]
                        lattice.cells[i][jPlus].fBuf[2] = lattice.cells[i][j].f[2]
                        lattice.cells[iSub][j].fBuf[3] = lattice.cells[i][j].f[3]
                        lattice.cells[iPlus][jPlus].fBuf[5] = lattice.cells[i][j].f[5]
                        lattice.cells[iSub][jPlus].fBuf[6] = lattice.cells[i][j].f[6]

                        lattice.cells[i][j].fBuf[2] = lattice.cells[i][j].f[4]
                        lattice.cells[i][j].fBuf[5] = p * lattice.cells[i][j].f[7] + q * lattice.cells[i][j].f[8]
                        lattice.cells[i][j].fBuf[6] = q * lattice.cells[i][j].f[7] + p * lattice.cells[i][j].f[8]
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
                lattice.cells[i][j].defineRhoU(rho, slideVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, slideVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), slideVelocity)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), slideVelocity)
            }
        }
    }

}