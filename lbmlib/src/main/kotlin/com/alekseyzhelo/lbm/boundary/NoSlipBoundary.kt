package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.util.opposite

class NoSlipBoundary(
    position: BoundaryPosition, lattice: LatticeD2,
    x0: Int, x1: Int, y0: Int, y1: Int
) : BoundaryCondition(position, lattice, x0, x1, y0, y1) {

    override fun getType(): BoundaryType {
        return BoundaryType.NO_SLIP
    }

    override fun streamOutgoing(i: Int, j: Int) {
        for (f in position.outgoing) {
            lattice.cells[i][j].fBuf[opposite[f]] = lattice.cells[i][j].f[f]
        }
    }

    override fun boundaryStream() {
        for (i in x0..x1) {
            for (j in y0..y1) {
                for (f in position.inside) {
                    lattice.cells[i + DescriptorD2Q9.c[f][0]][j + DescriptorD2Q9.c[f][1]].fBuf[f] =
                        lattice.cells[i][j].f[f]
                }
                for (f in position.outgoing) {
                    lattice.cells[i][j].fBuf[opposite[f]] = lattice.cells[i][j].f[f]
                }
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, doubleArrayOf(0.0, 0.0))
            }
        }
    }

    override fun defineBoundaryRhoU(rho: Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, doubleArrayOf(0.0, 0.0))
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), doubleArrayOf(0.0, 0.0))
            }
        }
    }

    override fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), doubleArrayOf(0.0, 0.0))
            }
        }
    }

}