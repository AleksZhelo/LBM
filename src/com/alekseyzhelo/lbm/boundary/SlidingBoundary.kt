package com.alekseyzhelo.lbm.boundary;

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

class SlidingBoundary(val position: BoundaryPosition, lattice: LatticeD2Q9,
                      x0: Int, x1: Int, y0: Int, y1: Int,
                      val slideVelocity: Double) : BoundaryCondition(lattice, x0, x1, y0, y1) {
    val cells = lattice.cells

    override fun getType(): BoundaryType {
        return BoundaryType.SLIDING
    }

    override fun getParam(): Double? {
        return slideVelocity
    }

    override fun boundaryStream() {
        when (position) {
            BoundaryPosition.TOP -> {
                //var once = true
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jSub = j - 1

                        val q = slideVelocity / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0])
                        val p = 1.0 - q
//                        if(once){
//                            println("p: $p, q: $q, slideVel: $slideVelocity")
//                            once = false
//                        }

                        cells[i][j].fBuf[0] = cells[i][j].f[0];
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

                        cells[i][j].fBuf[4] = cells[i][j].f[2];
                        cells[i][j].fBuf[7] = p * cells[i][j].f[5] + q * cells[i][j].f[6];
                        cells[i][j].fBuf[8] = q * cells[i][j].f[5] + p * cells[i][j].f[6];
                    }
                }
            }

            BoundaryPosition.BOTTOM -> {
                for (i in x0..x1) {
                    val iPlus = i + 1
                    val iSub = i - 1
                    for (j in y0..y1) {
                        val jPlus = j + 1

                        val q = slideVelocity / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0]) // TODO: correct as approximation to rho_w * u_w / (2.0 * f_8 - f_7)?
                        val p = 1.0 - q

                        cells[i][j].fBuf[0] = cells[i][j].f[0];
                        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

                        cells[i][j].fBuf[2] = cells[i][j].f[4];
                        cells[i][j].fBuf[5] = p * cells[i][j].f[7] + q * cells[i][j].f[8];
                        cells[i][j].fBuf[6] = q * cells[i][j].f[7] + p * cells[i][j].f[8];
                    }
                }
            }
        }

    }

}