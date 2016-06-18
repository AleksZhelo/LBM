package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.boundary.D2BoundaryFactory
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import java.util.stream.IntStream

/**
 * @author Aleks on 17-05-2016.
 */
// TODO: initialization
// TODO: some kind of statistics, output, etc
// TODO: units
// TODO?: boundary conditions
// TODO?: solids, etc
class LatticeD2Q9(val LX: Int, val LY: Int, val dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, BoundaryType>) {

    val cells = Array(LX, { column -> Array(LY, { cell -> CellD2Q9() }) })

    private val leftBoundary: BoundaryCondition
    private val topBoundary: BoundaryCondition
    private val rightBoundary: BoundaryCondition
    private val bottomBoundary: BoundaryCondition

    init {
        val maxX = LX - 1
        val maxY = LY - 1

        leftBoundary = D2BoundaryFactory.create(
                BoundaryPosition.LEFT,
                boundaries[BoundaryPosition.LEFT]!!, this,
                //0, 0, 0, maxY
                0, 0, 1, maxY - 1
        )
        topBoundary = D2BoundaryFactory.create(
                BoundaryPosition.TOP,
                boundaries[BoundaryPosition.TOP]!!, this,
                1, maxX - 1, maxY, maxY
        )
        rightBoundary = D2BoundaryFactory.create(
                BoundaryPosition.RIGHT,
                boundaries[BoundaryPosition.RIGHT]!!, this,
                maxX, maxX, 1, maxY - 1
        )
        bottomBoundary = D2BoundaryFactory.create(
                BoundaryPosition.BOTTOM,
                boundaries[BoundaryPosition.BOTTOM]!!, this,
                1, maxX - 1, 0, 0
        )
    }

    // TODO here the lattice velocity is hardcoded to be 1
    private fun innerStream(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        for (i in x0..x1) {
            for (j in y0..y1) {
                // TODO if (!is_interior_solid_node[i][j]) {
                doStream(i, i + 1, i - 1, j, j + 1, j - 1)
                // TODO }
            }
        }
    }

    fun doStream(i: Int, iPlus: Int, iSub: Int, j: Int, jPlus: Int, jSub: Int) {
        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
    }

    fun bulkCollide(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        for (i in x0..x1) {
            for (j in y0..y1) { // TODO performance?
                dynamics.collide(cells[i][j])
            }
        }
    }

    fun bulkCollideParallel(x0: Int, x1: Int, y0: Int, y1: Int): Unit {
        IntStream.range(x0, x1 + 1)
                .parallel()
                .forEach { i ->
                    IntStream.range(y0, y1 + 1)
                            .forEach { j -> dynamics.collide(cells[i][j]) }
                }
    }

    fun stream(): Unit {
        innerStream(1, LX - 2, 1, LY - 2)
        leftBoundary.boundaryStream()
        topBoundary.boundaryStream()
        rightBoundary.boundaryStream()
        bottomBoundary.boundaryStream()

        //fullPeriodicCorners()
        slipAndPeriodicCorners()
    }

    fun iniEquilibrium(Rho: Double, U: DoubleArray): Unit { // constant U for the whole lattice
        for (i in cells.indices) {
            for (j in cells[i].indices) { // TODO performance?
                dynamics.iniEquilibrium(cells[i][j], Rho, U)
            }
        }
    }

    fun iniEquilibrium(Rho: Double, U: (i: Int, j: Int) -> DoubleArray): Unit { // U as a function of the cell's location
        for (i in cells.indices) {
            for (j in cells[i].indices) { // TODO performance?
                dynamics.iniEquilibrium(cells[i][j], Rho, U(i, j))
            }
        }
    }

    fun iniEquilibrium(Rho: (i: Int, j: Int) -> Double, U: DoubleArray): Unit { // Rho as a function of the cell's location
        for (i in cells.indices) {
            for (j in cells[i].indices) { // TODO performance?
                dynamics.iniEquilibrium(cells[i][j], Rho(i, j), U)
            }
        }
    }

    fun iniEquilibrium(Rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray): Unit { // Rho and U as functions of the cell's location
        for (i in cells.indices) {
            for (j in cells[i].indices) { // TODO performance?
                dynamics.iniEquilibrium(cells[i][j], Rho(i, j), U(i, j))
            }
        }
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
                total += cells[i][j].computeRho(cells[i][j].f)
            }
        }

        return total
    }

// TEST END

    override fun toString(): String {
        return buildString {
            appendln("LX: $LX, LY: $LY")
            appendln("Dynamics: $dynamics")
        }
    }

    private fun fullPeriodicCorners() {
        //left bottom
        var i = 0
        var j = 0
        var iPlus = i + 1
        var iSub = LX - 1
        var jPlus = j + 1
        var jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] += cells[i][j].f[8];

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] += cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jPlus].fBuf[5] += cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] += cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
    }

    private fun slipAndPeriodicCorners() {
        // left bottom
        var i = 0
        var j = 0
        var iPlus = i + 1
        var iSub = LX - 1
        var jPlus = j + 1
        var jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

        cells[i][j].fBuf[2] = cells[i][j].f[4];
        cells[i][j].fBuf[5] = cells[i][j].f[7];
        cells[i][j].fBuf[6] = cells[i][j].f[8];

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

        cells[i][j].fBuf[2] = cells[i][j].f[4];
        cells[i][j].fBuf[5] = cells[i][j].f[7];
        cells[i][j].fBuf[6] = cells[i][j].f[8];

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        cells[i][j].fBuf[4] = cells[i][j].f[2];
        cells[i][j].fBuf[7] = cells[i][j].f[5];
        cells[i][j].fBuf[8] = cells[i][j].f[6];

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        cells[i][j].fBuf[4] = cells[i][j].f[2];
        cells[i][j].fBuf[7] = cells[i][j].f[5];
        cells[i][j].fBuf[8] = cells[i][j].f[6];
    }
}