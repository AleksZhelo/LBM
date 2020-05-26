package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptor
import com.alekseyzhelo.lbm.core.cell.Material
import com.alekseyzhelo.lbm.core.cell.MaterialCellD2Q9
import com.alekseyzhelo.lbm.core.cell.streamable
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import com.alekseyzhelo.lbm.dynamics.NoDynamics
import com.alekseyzhelo.lbm.dynamics.VoidDynamics
import com.alekseyzhelo.lbm.util.MaterialUtil
import java.awt.image.BufferedImage

/**
 * @author Aleks on 17-05-2016.
 */
class MaterialsLatticeD2Q9(
    private val imageSource: BufferedImage,
    dynamics: Dynamics2DQ9
) : LatticeD2<MaterialCellD2Q9>(imageSource.width, imageSource.height, emptyList(), dynamics) {

    override fun initBoundaries(boundaries: List<BoundaryDescriptor>): Array<BoundaryCondition> {
        return emptyArray()
    }

    override fun initCells(dynamics: Dynamics2DQ9): Array<Array<MaterialCellD2Q9>> {
        return Array(LX) { x ->
            Array(LY) { y ->
                createCell(dynamics, x, y)
            }
        }
    }

    private fun createCell(dynamics: Dynamics2DQ9, x: Int, y: Int): MaterialCellD2Q9 {
        return when (imageSource.getRGB(x, y)) {
            Material.FLOW.color -> MaterialCellD2Q9(Material.FLOW, dynamics)
            Material.INFLOW.color -> MaterialCellD2Q9(Material.INFLOW, NoDynamics)
            Material.OUTFLOW.color -> MaterialCellD2Q9(Material.OUTFLOW, NoDynamics)
            Material.NOTHING.color -> MaterialCellD2Q9(Material.NOTHING, VoidDynamics)
            Material.SOLID.color -> MaterialCellD2Q9(Material.SOLID, VoidDynamics)
            else -> {
                println("Material ${imageSource.getRGB(x, y)} not supported.")
                MaterialCellD2Q9(Material.NOTHING, VoidDynamics)
            }
        }
    }

    override fun stream() {
        innerStream(1, LX - 1, 1, LY - 1)
    }

    override fun innerStream(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        for (i in x0..x1) {
            for (j in y0..y1) {
                if (streamable.contains((cells[i][j]).material)) {
                    doStream(i, i + 1, i - 1, j, j + 1, j - 1)
                }
            }
        }
    }

    override fun doStream(i: Int, iPlus: Int, iSub: Int, j: Int, jPlus: Int, jSub: Int) {
        val cell = cells[i][j]

        cells[i][j].fBuf[0] = cells[i][j].f[0]

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (cell.material) {
            Material.FLOW -> {
                MaterialUtil.streamFlow(cell, cells[iPlus][j], 1)
                MaterialUtil.streamFlow(cell, cells[i][jPlus], 2)
                MaterialUtil.streamFlow(cell, cells[iSub][j], 3)
                MaterialUtil.streamFlow(cell, cells[i][jSub], 4)
                MaterialUtil.streamFlow(cell, cells[iPlus][jPlus], 5)
                MaterialUtil.streamFlow(cell, cells[iSub][jPlus], 6)
                MaterialUtil.streamFlow(cell, cells[iSub][jSub], 7)
                MaterialUtil.streamFlow(cell, cells[iPlus][jSub], 8)
            }
            Material.INFLOW -> {
                MaterialUtil.streamInflow(cell, cells[iPlus][j], 1)
                MaterialUtil.streamInflow(cell, cells[i][jPlus], 2)
                MaterialUtil.streamInflow(cell, cells[iSub][j], 3)
                MaterialUtil.streamInflow(cell, cells[i][jSub], 4)
                MaterialUtil.streamInflow(cell, cells[iPlus][jPlus], 5)
                MaterialUtil.streamInflow(cell, cells[iSub][jPlus], 6)
                MaterialUtil.streamInflow(cell, cells[iSub][jSub], 7)
                MaterialUtil.streamInflow(cell, cells[iPlus][jSub], 8)
            }
            Material.OUTFLOW -> {
                MaterialUtil.streamOutflow(cell, cells[iPlus][j], 1)
                MaterialUtil.streamOutflow(cell, cells[i][jPlus], 2)
                MaterialUtil.streamOutflow(cell, cells[iSub][j], 3)
                MaterialUtil.streamOutflow(cell, cells[i][jSub], 4)
                MaterialUtil.streamOutflow(cell, cells[iPlus][jPlus], 5)
                MaterialUtil.streamOutflow(cell, cells[iSub][jPlus], 6)
                MaterialUtil.streamOutflow(cell, cells[iSub][jSub], 7)
                MaterialUtil.streamOutflow(cell, cells[iPlus][jSub], 8)
            }
        }
    }

}