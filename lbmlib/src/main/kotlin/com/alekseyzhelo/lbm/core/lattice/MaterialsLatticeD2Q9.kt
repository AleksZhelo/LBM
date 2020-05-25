package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptor
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
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
class MaterialsLatticeD2Q9(val imageSource: BufferedImage, omega: Double, dynamics: Dynamics2DQ9) :
    LatticeD2(imageSource.width, imageSource.height, emptyList<BoundaryDescriptor>(), dynamics, imageSource) {

    init {

    }

    override fun initBoundaries(boundaries: List<BoundaryDescriptor>): Array<BoundaryCondition> {
        return emptyArray()
    }

    override fun initCells(dynamics: Dynamics2DQ9): Array<Array<CellD2Q9>> {
        return Array(LX) { x ->
            Array(LY) { y ->
                createCell(dynamics, x, y)
            }
        }
    }

    private fun createCell(dynamics: Dynamics2DQ9, x: Int, y: Int): CellD2Q9 {
        return when (hack!!.getRGB(x, y)) {
            Material.FLOW.color -> MaterialCellD2Q9(Material.FLOW, dynamics)
            Material.INFLOW.color -> MaterialCellD2Q9(Material.INFLOW, NoDynamics)
            Material.OUTFLOW.color -> MaterialCellD2Q9(Material.OUTFLOW, NoDynamics)
            Material.NOTHING.color -> MaterialCellD2Q9(Material.NOTHING, VoidDynamics)
            Material.SOLID.color -> MaterialCellD2Q9(Material.SOLID, VoidDynamics)
            else -> {
                println("Material ${hack.getRGB(x, y)} not supported.")
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
                if (streamable.contains((cells[i][j] as MaterialCellD2Q9).material)) {
                    doStream(i, i + 1, i - 1, j, j + 1, j - 1)
                }
            }
        }
    }

    override fun doStream(i: Int, iPlus: Int, iSub: Int, j: Int, jPlus: Int, jSub: Int) {
        val cell = cells[i][j] as MaterialCellD2Q9

        cells[i][j].fBuf[0] = cells[i][j].f[0]

        @Suppress("NON_EXHAUSTIVE_WHEN")
        when (cell.material) {
            Material.FLOW -> {
                MaterialUtil.streamFlow(cell, cells[iPlus][j] as MaterialCellD2Q9, 1)
                MaterialUtil.streamFlow(cell, cells[i][jPlus] as MaterialCellD2Q9, 2)
                MaterialUtil.streamFlow(cell, cells[iSub][j] as MaterialCellD2Q9, 3)
                MaterialUtil.streamFlow(cell, cells[i][jSub] as MaterialCellD2Q9, 4)
                MaterialUtil.streamFlow(cell, cells[iPlus][jPlus] as MaterialCellD2Q9, 5)
                MaterialUtil.streamFlow(cell, cells[iSub][jPlus] as MaterialCellD2Q9, 6)
                MaterialUtil.streamFlow(cell, cells[iSub][jSub] as MaterialCellD2Q9, 7)
                MaterialUtil.streamFlow(cell, cells[iPlus][jSub] as MaterialCellD2Q9, 8)
            }
            Material.INFLOW -> {
                MaterialUtil.streamInflow(cell, cells[iPlus][j] as MaterialCellD2Q9, 1)
                MaterialUtil.streamInflow(cell, cells[i][jPlus] as MaterialCellD2Q9, 2)
                MaterialUtil.streamInflow(cell, cells[iSub][j] as MaterialCellD2Q9, 3)
                MaterialUtil.streamInflow(cell, cells[i][jSub] as MaterialCellD2Q9, 4)
                MaterialUtil.streamInflow(cell, cells[iPlus][jPlus] as MaterialCellD2Q9, 5)
                MaterialUtil.streamInflow(cell, cells[iSub][jPlus] as MaterialCellD2Q9, 6)
                MaterialUtil.streamInflow(cell, cells[iSub][jSub] as MaterialCellD2Q9, 7)
                MaterialUtil.streamInflow(cell, cells[iPlus][jSub] as MaterialCellD2Q9, 8)
            }
            Material.OUTFLOW -> {
                MaterialUtil.streamOutflow(cell, cells[iPlus][j] as MaterialCellD2Q9, 1)
                MaterialUtil.streamOutflow(cell, cells[i][jPlus] as MaterialCellD2Q9, 2)
                MaterialUtil.streamOutflow(cell, cells[iSub][j] as MaterialCellD2Q9, 3)
                MaterialUtil.streamOutflow(cell, cells[i][jSub] as MaterialCellD2Q9, 4)
                MaterialUtil.streamOutflow(cell, cells[iPlus][jPlus] as MaterialCellD2Q9, 5)
                MaterialUtil.streamOutflow(cell, cells[iSub][jPlus] as MaterialCellD2Q9, 6)
                MaterialUtil.streamOutflow(cell, cells[iSub][jSub] as MaterialCellD2Q9, 7)
                MaterialUtil.streamOutflow(cell, cells[iPlus][jSub] as MaterialCellD2Q9, 8)
            }
        }
    }

}