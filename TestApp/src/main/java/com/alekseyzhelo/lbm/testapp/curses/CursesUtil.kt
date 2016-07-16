package com.alekseyzhelo.lbm.testapp.curses

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
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


internal fun cellColor(normalized: Double): CharColor {
    when {
        normalized >= 0.9 -> return red
        normalized >= 0.8 -> return magenta
        normalized >= 0.6 -> return yellow
        normalized >= 0.4 -> return green
        normalized >= 0.2 -> return cyan
        else -> return blue
    }
}

fun CellD2Q9.drawPressureRatedValue(i: Int, j: Int,
                                    strLen: Int, minPressure: Double, maxPressure: Double,
                                    func: (Rho: Double) -> String): Unit {
    val Rho = computeRho()
    val color = cellColor(normalize(Rho, minPressure, maxPressure))
    Toolkit.printString(func(Rho), i * (strLen + 1), j, strLen, 1, color)
    Toolkit.printString("|", i * (strLen + 1) + strLen, j, 1, 1, white)
}

fun CellD2Q9.drawVelocityRatedValue(i: Int, j: Int,
                                    strLen: Int, minUNorm: Double, maxUNorm: Double,
                                    func: (U: DoubleArray) -> String): Unit {
    val U = computeRhoU()
    val color = cellColor(normalize(norm(U), minUNorm, maxUNorm))
    Toolkit.printString(func(U), i * (strLen + 1), j, strLen, 1, color)
    Toolkit.printString("|", i * (strLen + 1) + strLen, j, 1, 1, white)
}

// TODO: better names for these functions
fun LatticeD2.drawPressureRatedTable(minPressure: Double, maxPressure: Double,
                                       func: (Rho: Double) -> String): Unit {
    val strLen = func(-0.001).length
    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            cells[i][j].drawPressureRatedValue(i, j, strLen, minPressure, maxPressure, func)
        }
    }
}

// TODO: better names for these functions
fun LatticeD2.drawVelocityRatedTable(minUNorm: Double, maxUNorm: Double,
                                       func: (U: DoubleArray) -> String): Unit {
    val strLen = func(doubleArrayOf(1.0, -1.0)).length
    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            cells[i][j].drawVelocityRatedValue(i, j, strLen, minUNorm, maxUNorm, func)
        }
    }
}