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
// TODO: some kind of statistics, output, etc
// TODO: units
// TODO?: boundary conditions
// TODO?: solids, etc
// TODO: decouple corner handling
class LatticeD2Q9(val LX: Int, val LY: Int, val dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Double?>>) {

    val cells = Array(LX, { column -> Array(LY, { cell -> CellD2Q9() }) })

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

        //purePeriodicCorners()
        automaticCorners()
        //fullPeriodicCorners() // TODO: does not work? wtf?
        //slipAndPeriodicCorners()
    }

    fun iniEquilibrium(Rho: Double, U: DoubleArray): Unit { // constant U for the whole lattice
        for (i in 0..LX - 1) {
            for (j in 1..LY - 2) { // TODO performance?
                dynamics.iniEquilibrium(cells[i][j], Rho, U)
            }
        }
        // TODO: all below - necessary?
        if (topBoundary.getType() == BoundaryType.SLIDING || topBoundary.getType() == BoundaryType.ZHOU_HE_UX) {
            val UU = doubleArrayOf(topBoundary.getParam()!!, 0.0)
            for (i in 0..LX - 1) {
                dynamics.iniEquilibrium(cells[i][LY-1], Rho, UU)
            }
        } else {
            for (i in 0..LX - 1) {
                dynamics.iniEquilibrium(cells[i][LY-1], Rho, U)
            }
        }
        if (bottomBoundary.getType() == BoundaryType.SLIDING || bottomBoundary.getType() == BoundaryType.ZHOU_HE_UX) {
            val UU = doubleArrayOf(bottomBoundary.getParam()!!, 0.0)
            for (i in 0..LX - 1) {
                dynamics.iniEquilibrium(cells[i][0], Rho, UU)
            }
        } else {
            for (i in 0..LX - 1) {
                dynamics.iniEquilibrium(cells[i][0], Rho, U)
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

    private fun automaticCorners() {
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
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
        }

        when (bottomBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
            BoundaryType.SLIDING -> {
                val q = bottomBoundary.getParam()!! / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0]) // TODO: correct as approximation to rho_w * u_w / (2.0 * f_8 - f_7)?
                val p = 1.0 - q

                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = p * cells[i][j].f[7] + q * cells[i][j].f[8];
                cells[i][j].fBuf[6] = q * cells[i][j].f[7] + p * cells[i][j].f[8];
            }
            BoundaryType.ZHOU_HE_UX -> {
                val rho = cells[i][j].f[0] + cells[i][j].f[1] + cells[i][j].f[3] +
                        + 2.0 * (cells[i][j].f[4] + cells[i][j].f[7] + cells[i][j].f[8])

                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7] - 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) + 0.5 * rho * bottomBoundary.getParam()!!;
                cells[i][j].fBuf[6] = cells[i][j].f[8] + 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) - 0.5 * rho * bottomBoundary.getParam()!!;
            }
        }

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
            }
        }

        when (bottomBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
            BoundaryType.SLIDING -> {
                val q = bottomBoundary.getParam()!! / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0]) // TODO: correct as approximation to rho_w * u_w / (2.0 * f_8 - f_7)?
                val p = 1.0 - q

                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = p * cells[i][j].f[7] + q * cells[i][j].f[8];
                cells[i][j].fBuf[6] = q * cells[i][j].f[7] + p * cells[i][j].f[8];
            }
            BoundaryType.ZHOU_HE_UX -> {
                val rho = cells[i][j].f[0] + cells[i][j].f[1] + cells[i][j].f[3] +
                        + 2.0 * (cells[i][j].f[4] + cells[i][j].f[7] + cells[i][j].f[8])

                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7] - 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) + 0.5 * rho * bottomBoundary.getParam()!!;
                cells[i][j].fBuf[6] = cells[i][j].f[8] + 0.5 * (cells[i][j].f[1] - cells[i][j].f[3]) - 0.5 * rho * bottomBoundary.getParam()!!;
            }
        }

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
            }
        }

        when (topBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
            BoundaryType.SLIDING -> {
                val q = topBoundary.getParam()!! / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0])
                val p = 1.0 - q

                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = p * cells[i][j].f[5] + q * cells[i][j].f[6];
                cells[i][j].fBuf[8] = q * cells[i][j].f[5] + p * cells[i][j].f[6];
            }
        }

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];

        when (topBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
            BoundaryType.SLIDING -> {
                val q = topBoundary.getParam()!! / (2.0 * cells[i][j].computeRhoU(cells[i][j].f)[0])
                val p = 1.0 - q

                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = p * cells[i][j].f[5] + q * cells[i][j].f[6];
                cells[i][j].fBuf[8] = q * cells[i][j].f[5] + p * cells[i][j].f[6];
            }
        }

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
        }
    }

    private fun automaticCornersWrongBeautiful() {
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
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
        }

        when (bottomBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
        }

        // right bottom
        i = LX - 1
        j = 0
        iPlus = 0
        iSub = i - 1
        jPlus = j + 1
        jSub = LY - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                cells[iPlus][jSub].fBuf[5] = cells[i][j].f[5];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
            }
        }

        when (bottomBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jSub].fBuf[4] = cells[i][j].f[4];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[2] = cells[i][j].f[4];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
        }

        // left top
        i = 0
        j = LY - 1
        iPlus = i + 1
        iSub = LX - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

        when (leftBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iSub][j].fBuf[3] = cells[i][j].f[3];
                cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[1] = cells[i][j].f[3];
                cells[i][j].fBuf[5] = cells[i][j].f[7];
            }
        }

        when (topBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
        }

        // right top
        i = LX - 1
        j = LY - 1
        iPlus = 0
        iSub = i - 1
        jPlus = 0
        jSub = j - 1

        cells[i][j].fBuf[0] = cells[i][j].f[0];
        cells[iSub][j].fBuf[3] = cells[i][j].f[3];
        cells[i][jSub].fBuf[4] = cells[i][j].f[4];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];

        when (topBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[i][jPlus].fBuf[2] = cells[i][j].f[2];
                cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
                cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[4] = cells[i][j].f[2];
                cells[i][j].fBuf[7] = cells[i][j].f[5];
                cells[i][j].fBuf[8] = cells[i][j].f[6];
            }
        }

        when (rightBoundary.getType()) {
            BoundaryType.PERIODIC -> {
                cells[iPlus][j].fBuf[1] = cells[i][j].f[1];
                cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
            }
            BoundaryType.NO_SLIP -> {
                cells[i][j].fBuf[3] = cells[i][j].f[1];
                cells[i][j].fBuf[6] = cells[i][j].f[8];
            }
        }
    }

    private fun purePeriodicCorners() {
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
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];

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
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
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
        cells[iPlus][jPlus].fBuf[5] = cells[i][j].f[5];
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
        cells[iSub][jPlus].fBuf[6] = cells[i][j].f[6];
        cells[iSub][jSub].fBuf[7] = cells[i][j].f[7];
        cells[iPlus][jSub].fBuf[8] = cells[i][j].f[8];
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

    private fun slipAndPeriodicCorners() { // top and bottom - no-slip, left and right - periodic
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