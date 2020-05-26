package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.util.opposite

class OutletBoundary(
    position: BoundaryPosition, lattice: LatticeD2<*>,
    x0: Int, x1: Int, y0: Int, y1: Int,
    val inletVelocity: Double
) : BoundaryCondition(position, lattice, x0, x1, y0, y1) {

    override fun getType(): BoundaryType {
        return BoundaryType.OUTLET
    }

    override fun streamOutgoing(i: Int, j: Int) {
        val r = reflectionProbability(lattice.cells[i][j].computeRhoU())
        for (f in position.outgoing) {
            lattice.cells[i][j].fBuf[opposite[f]] = r * lattice.cells[i][j].f[f]
        }
    }

    override fun boundaryStream() {
        for (i in x0..x1) {
            for (j in y0..y1) {
                for (f in position.inside) {
                    lattice.cells[i + DescriptorD2Q9.c[f][0]][j + DescriptorD2Q9.c[f][1]].fBuf[f] =
                        lattice.cells[i][j].f[f]
                }

                val r = reflectionProbability(lattice.cells[i][j].computeRhoU())
                for (f in position.outgoing) {
                    lattice.cells[i][j].fBuf[opposite[f]] = r * lattice.cells[i][j].f[f]
                }
            }
        }
    }

    private fun reflectionProbability(U: DoubleArray): Double {
        val u = when (position) {
            BoundaryPosition.LEFT -> U[0]
            BoundaryPosition.TOP -> U[1]
            BoundaryPosition.RIGHT -> U[0]
            BoundaryPosition.BOTTOM -> U[1]
        }
        val r = 1.0 + 4.0 * (u - inletVelocity) / (1.0 - 2.0 * u)
        return if (r > 1.0) 1.0 else if (r < 0.0) 0.0 else r
    }

    override fun defineBoundaryRhoU(rho: Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, U)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, U(i, j))
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), U)
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), U(i, j))
            }
        }
    }

}