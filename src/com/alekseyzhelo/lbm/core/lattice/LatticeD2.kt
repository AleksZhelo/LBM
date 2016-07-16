package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.D2BoundaryFactory
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptor
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import java.util.stream.IntStream

/**
 * @author Aleks on 16-07-2016.
 */


abstract class LatticeD2(val LX: Int, val LY: Int,
                         boundaries: List<BoundaryDescriptor>, dynamics: Dynamics2DQ9) {
    // TODO: SOFT-BLOCKER figure out proper dynamics on boundaries
    val cells: Array<Array<CellD2Q9>>
    val boundaries: Array<BoundaryCondition>

    init {
        this.boundaries = initBoundaries(boundaries)
        cells = initCells(dynamics)
    }

    open protected fun initBoundaries(boundaries: List<BoundaryDescriptor>): Array<BoundaryCondition> {
        return Array(boundaries.size, { i: Int ->
            val desc = boundaries[i]
            D2BoundaryFactory.create(
                    desc.position, desc.type, this,
                    desc.x0, desc.x1, desc.y0, desc.y1,
                    desc.doubleParam, desc.doubleArrayParam
            )
        })
    }

    abstract protected fun initCells(dynamics: Dynamics2DQ9): Array<Array<CellD2Q9>>

    // TODO: slow?
    protected fun boundaryContains(i: Int, j: Int): BoundaryCondition? {
        for (boundary in boundaries) {
            if (boundary.contains(i, j)) {
                return boundary
            }
        }

        return null
    }

    fun doStream(i: Int, iPlus: Int, iSub: Int, j: Int, jPlus: Int, jSub: Int) {
        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]
    }

    // TODO here the lattice velocity is hardcoded to be 1
    open protected fun innerStream(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        for (i in x0..x1) {
            for (j in y0..y1) {
                // TODO if (!is_interior_solid_node[i][j]) {
                doStream(i, i + 1, i - 1, j, j + 1, j - 1)
                // TODO }
            }
        }
    }

    fun bulkCollide(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        for (i in x0..x1) {
            for (j in y0..y1) { // TODO performance?
                cells[i][j].collide()
            }
        }
    }

    fun bulkCollideParallel(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        IntStream.range(x0, x1 + 1)
                .parallel()
                .forEach { i ->
                    IntStream.range(y0, y1 + 1)
                            .forEach { j -> cells[i][j].collide() }
                }
    }

    open fun stream(): Unit {
        innerStream(1, LX - 2, 1, LY - 2)
        for (boundary in boundaries) {
            boundary.boundaryStream()
        }
    }

    fun iniEquilibrium(rho: Double, U: DoubleArray): Unit { // constant U for the whole lattice
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho, U)
            }
        }

        for (boundary in boundaries) {
            boundary.defineBoundaryRhoU(rho, U)
        }

        cells[0][0].defineRhoU(rho, U)
        cells[LX - 1][0].defineRhoU(rho, U)
        cells[0][LY - 1].defineRhoU(rho, U)
        cells[LX - 1][LY - 1].defineRhoU(rho, U)
    }

    fun iniEquilibrium(rho: Double, U: (Int, Int) -> DoubleArray): Unit { // U as a function of the cell's location
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho, U(i, j))
            }
        }

        for (boundary in boundaries) {
            boundary.defineBoundaryRhoU(rho, U)
        }

        cells[0][0].defineRhoU(rho, U(0, 0))
        cells[LX - 1][0].defineRhoU(rho, U(LX - 1, 0))
        cells[0][LY - 1].defineRhoU(rho, U(0, LY - 1))
        cells[LX - 1][LY - 1].defineRhoU(rho, U(LX - 1, LY - 1))
    }

    fun iniEquilibrium(rho: (Int, Int) -> Double, U: DoubleArray): Unit { // rho as a function of the cell's location
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho(i, j), U)
            }
        }

        for (boundary in boundaries) {
            boundary.defineBoundaryRhoU(rho, U)
        }

        cells[0][0].defineRhoU(rho(0, 0), U)
        cells[LX - 1][0].defineRhoU(rho(LX - 1, 0), U)
        cells[0][LY - 1].defineRhoU(rho(0, LY - 1), U)
        cells[LX - 1][LY - 1].defineRhoU(rho(LX - 1, LY - 1), U)
    }

    fun iniEquilibrium(rho: (Int, Int) -> Double, U: (Int, Int) -> DoubleArray): Unit { // rho and U as functions of the cell's location
        for (i in 0..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho(i, j), U(i, j))
            }
        }

        for (boundary in boundaries) {
            boundary.defineBoundaryRhoU(rho, U)
        }

        cells[0][0].defineRhoU(rho(0, 0), U(0, 0))
        cells[LX - 1][0].defineRhoU(rho(LX - 1, 0), U(LX - 1, 0))
        cells[0][LY - 1].defineRhoU(rho(0, LY - 1), U(0, LY - 1))
        cells[LX - 1][LY - 1].defineRhoU(rho(LX - 1, LY - 1), U(LX - 1, LY - 1))
    }

    // TEST

    fun swapCellBuffers(): Unit {
        for (i in cells.indices) {
            for (j in cells[i].indices) {
                cells[i][j].swapBuffers()
            }
        }
    }

    fun totalDensity(): Double {
        var total = 0.0
        for (i in cells.indices) {
            for (j in cells[i].indices) {
                total += cells[i][j].computeRho()
            }
        }

        return total
    }

    // TEST END

    override fun toString(): String {
        return buildString {
            appendln("LX: $LX, LY: $LY")
        }
    }

}


