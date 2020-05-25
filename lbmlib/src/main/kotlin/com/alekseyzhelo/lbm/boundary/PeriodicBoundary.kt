package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2

class PeriodicBoundary(
    position: BoundaryPosition, lattice: LatticeD2,
    x0: Int, x1: Int, y0: Int, y1: Int
) : BoundaryCondition(position, lattice, x0, x1, y0, y1) {

    override fun getType(): BoundaryType {
        return BoundaryType.PERIODIC
    }

    override fun streamOutgoing(i: Int, j: Int) {
        for (f in position.outgoing) {
            val x = Math.floorMod(i + DescriptorD2Q9.c[f][0], lattice.LX)
            val y = Math.floorMod(j + DescriptorD2Q9.c[f][1], lattice.LY)
            lattice.cells[x][y].fBuf[f] = lattice.cells[i][j].f[f]
        }
    }

    override fun boundaryStream() {
        for (i in x0..x1) {
            val iSub = if (i > 0) (i - 1) else (lattice.LX - 1)
            val iPlus = if (i < lattice.LX - 1) (i + 1) else (0)
            for (j in y0..y1) {
                val jSub = if (j > 0) (j - 1) else (lattice.LY - 1)
                val jPlus = if (j < lattice.LY - 1) (j + 1) else (0)
                // TODO if (!is_interior_solid_node[i][j]) {
                lattice.doStream(i, iPlus, iSub, j, jPlus, jSub)
                // TODO }
            }
        }
    }

}