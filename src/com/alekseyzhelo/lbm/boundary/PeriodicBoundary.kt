package com.alekseyzhelo.lbm.boundary;

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

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