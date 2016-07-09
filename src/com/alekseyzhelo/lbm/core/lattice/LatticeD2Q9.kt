package com.alekseyzhelo.lbm.core.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryCondition
import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.boundary.D2BoundaryFactory
import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.dynamics.BoundaryBGK_D2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import java.util.stream.IntStream

/**
 * @author Aleks on 17-05-2016.
 */
// TODO: some kind of statistics, output, etc
// TODO: units
// TODO?: boundary conditions
// TODO?: solids, etc
class LatticeD2Q9(val LX: Int, val LY: Int, omega: Double, dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Double?>>) {

    // TODO: SOFT-BLOCKER figure out proper dynamics on boundaries
    val cells = Array(LX, { x ->
        Array(LY, {
            y ->
            {
//                val tParams = boundaries[BoundaryPosition.TOP]!!
//                val mDynamics = if (y == LY - 1 && (x > 0 || x < LX - 1)) {
//                    if (tParams.first == BoundaryType.SLIDING)
//                        BoundaryBGK_D2Q9(omega, BoundaryPosition.TOP)
//                    else
//                        dynamics
//                } else
//                    dynamics
                val mDynamics = dynamics
                CellD2Q9(mDynamics)
            }()
        })
    })

    private val leftBoundary: BoundaryCondition
    private val topBoundary: BoundaryCondition
    private val rightBoundary: BoundaryCondition
    private val bottomBoundary: BoundaryCondition

    init {
        val maxX = LX - 1
        val maxY = LY - 1

        val lParams = boundaries[BoundaryPosition.LEFT]!!
        leftBoundary = D2BoundaryFactory.create(
                BoundaryPosition.LEFT,
                lParams.component1(), this,
                //0, 0, 0, maxY
                0, 0, 1, maxY - 1,
                lParams.component2()
        )
        val tParams = boundaries[BoundaryPosition.TOP]!!
        topBoundary = D2BoundaryFactory.create(
                BoundaryPosition.TOP,
                tParams.component1(), this,
                1, maxX - 1, maxY, maxY,
                tParams.component2()
        )
        val rParams = boundaries[BoundaryPosition.RIGHT]!!
        rightBoundary = D2BoundaryFactory.create(
                BoundaryPosition.RIGHT,
                rParams.component1(), this,
                maxX, maxX, 1, maxY - 1,
                rParams.component2()
        )
        val bParams = boundaries[BoundaryPosition.BOTTOM]!!
        bottomBoundary = D2BoundaryFactory.create(
                BoundaryPosition.BOTTOM,
                bParams.component1(), this,
                1, maxX - 1, 0, 0,
                bParams.component2()
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

    fun stream(): Unit {
        innerStream(1, LX - 2, 1, LY - 2)
        leftBoundary.boundaryStream()
        topBoundary.boundaryStream()
        rightBoundary.boundaryStream()
        bottomBoundary.boundaryStream()

        automaticCorners()
    }

    fun iniEquilibrium(rho: Double, U: DoubleArray): Unit { // constant U for the whole lattice
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho, U)
            }
        }

        leftBoundary.defineBoundaryRhoU(rho, U)
        topBoundary.defineBoundaryRhoU(rho, U)
        rightBoundary.defineBoundaryRhoU(rho, U)
        bottomBoundary.defineBoundaryRhoU(rho, U)

        cells[0][0].defineRhoU(rho, U)
        cells[LX - 1][0].defineRhoU(rho, U)
        cells[0][LY - 1].defineRhoU(rho, U)
        cells[LX - 1][LY - 1].defineRhoU(rho, U)
    }

    fun iniEquilibrium(rho: Double, U: (i: Int, j: Int) -> DoubleArray): Unit { // U as a function of the cell's location
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho, U(i, j))
            }
        }

        leftBoundary.defineBoundaryRhoU(rho, U)
        topBoundary.defineBoundaryRhoU(rho, U)
        rightBoundary.defineBoundaryRhoU(rho, U)
        bottomBoundary.defineBoundaryRhoU(rho, U)

        cells[0][0].defineRhoU(rho, U(0, 0))
        cells[LX - 1][0].defineRhoU(rho, U(LX - 1, 0))
        cells[0][LY - 1].defineRhoU(rho, U(0, LY - 1))
        cells[LX - 1][LY - 1].defineRhoU(rho, U(LX - 1, LY - 1))
    }

    fun iniEquilibrium(rho: (i: Int, j: Int) -> Double, U: DoubleArray): Unit { // rho as a function of the cell's location
        for (i in 1..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho(i, j), U)
            }
        }

        leftBoundary.defineBoundaryRhoU(rho, U)
        topBoundary.defineBoundaryRhoU(rho, U)
        rightBoundary.defineBoundaryRhoU(rho, U)
        bottomBoundary.defineBoundaryRhoU(rho, U)

        cells[0][0].defineRhoU(rho(0, 0), U)
        cells[LX - 1][0].defineRhoU(rho(LX - 1, 0), U)
        cells[0][LY - 1].defineRhoU(rho(0, LY - 1), U)
        cells[LX - 1][LY - 1].defineRhoU(rho(LX - 1, LY - 1), U)
    }

    fun iniEquilibrium(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray): Unit { // rho and U as functions of the cell's location
        for (i in 0..LX - 2) {
            for (j in 1..LY - 2) {
                cells[i][j].defineRhoU(rho(i, j), U(i, j))
            }
        }

        leftBoundary.defineBoundaryRhoU(rho, U)
        topBoundary.defineBoundaryRhoU(rho, U)
        rightBoundary.defineBoundaryRhoU(rho, U)
        bottomBoundary.defineBoundaryRhoU(rho, U)

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

    // TODO: improve further
    private fun automaticCorners() {
// left bottom
        var i = 0
        var j = 0
        var iPlus = i + 1
        var iSub = LX - 1
        var jPlus = j + 1
        var jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[8] = cells[i][j].f[6]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        bottomBoundary.streamOutgoing(i, j)

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2]
        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6]

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[7] = cells[i][j].f[5]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        bottomBoundary.streamOutgoing(i, j)

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3]
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3]
                cells[i][j].fBuf[5] = cells[i][j].f[7]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }

        topBoundary.streamOutgoing(i, j)

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0]
        cells[iSub][j].fBuf[3] = cells[i][j].f[3]
        cells[i][jSub].fBuf[4] = cells[i][j].f[4]
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7]

        topBoundary.streamOutgoing(i, j)

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1]
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8]
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1]
                cells[i][j].fBuf[6] = cells[i][j].f[8]
            }
            else -> {
                throw UnsupportedOperationException("not implemented yet")
            }
        }
    }

}