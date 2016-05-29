package com.alekseyzhelo.lbm.testapp.curses

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.util.norm
import com.alekseyzhelo.lbm.util.normalize
import jcurses.system.CharColor
import jcurses.system.Toolkit

/**
 * @author Aleks on 29-05-2016.
 */

internal val white = CharColor(CharColor.BLACK, CharColor.WHITE)
internal val blue = CharColor(CharColor.BLACK, CharColor.BLUE)
internal val cyan = CharColor(CharColor.BLACK, CharColor.CYAN)
internal val green = CharColor(CharColor.BLACK, CharColor.GREEN)
internal val yellow = CharColor(CharColor.BLACK, CharColor.YELLOW)
internal val magenta = CharColor(CharColor.BLACK, CharColor.MAGENTA)
internal val red = CharColor(CharColor.BLACK, CharColor.RED)

fun LatticeD2Q9.minPressure(): Double { // TODO can be called without a lattice? WTF?
    var min = Double.MAX_VALUE
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho(cells[i][j].f)
            if (Rho < min) min = Rho
        }
    }

    return min
}

fun LatticeD2Q9.maxPressure(): Double { // TODO can be called without a lattice? WTF?
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val Rho = cells[i][j].computeRho(cells[i][j].f)
            if (Rho > max) max = Rho
        }
    }

    return max
}

fun LatticeD2Q9.maxVelocityNorm(): Double { // TODO can be called without a lattice? WTF?
    var max = 0.0
    for (i in cells.indices) {
        for (j in cells[i].indices) {
            val norm = norm(cells[i][j].computeRhoU(cells[i][j].f))
            if (norm > max) max = norm
        }
    }

    return max
}

fun CellD2Q9.drawPressureRatedValue(i: Int, j: Int,
                                    strLen: Int, minPressure: Double, maxPressure: Double,
                                    func: (Rho: Double) -> String): Unit {
    val Rho = computeRho(f)
    val color = rateColor(normalize(Rho, minPressure, maxPressure))
    Toolkit.printString(func(Rho), i * (strLen + 1), j, strLen, 1, color)
    Toolkit.printString("|", i * (strLen + 1) + strLen, j, 1, 1, white)
}

fun CellD2Q9.drawVelocityRatedValue(i: Int, j: Int,
                                    strLen: Int, minUNorm: Double, maxUNorm: Double,
                                    func: (U: DoubleArray) -> String): Unit {
    val U = computeRhoU(f)
    val color = rateColor(normalize(norm(U), minUNorm, maxUNorm))
    Toolkit.printString(func(U), i * (strLen + 1), j, strLen, 1, color)
    Toolkit.printString("|", i * (strLen + 1) + strLen, j, 1, 1, white)
}

internal fun rateColor(normalized: Double): CharColor {
    when {
        normalized >= 0.9 -> return red
        normalized >= 0.8 -> return magenta
        normalized >= 0.6 -> return yellow
        normalized >= 0.4 -> return green
        normalized >= 0.2 -> return cyan
        else -> return blue
    }
}

// TODO: better names for these functions
fun LatticeD2Q9.drawPressureRatedTable(minPressure: Double, maxPressure: Double,
                                       func: (Rho: Double) -> String): Unit {
    val strLen = func(-0.001).length
    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            cells[i][j].drawPressureRatedValue(i, j, strLen, minPressure, maxPressure, func)
        }
    }
}

// TODO: better names for these functions
fun LatticeD2Q9.drawVelocityRatedTable(minUNorm: Double, maxUNorm: Double,
                                       func: (U: DoubleArray) -> String): Unit {
    val strLen = func(doubleArrayOf(1.0, -1.0)).length
    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            cells[i][j].drawVelocityRatedValue(i, j, strLen, minUNorm, maxUNorm, func)
        }
    }
}