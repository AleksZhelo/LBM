package com.alekseyzhelo.lbm.simpleguiapp.algs4

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.util.normalize
import edu.princeton.cs.algs4.StdDraw
import java.awt.Color
import java.util.*

/**
 * @author Aleks on 29-05-2016.
 */

internal val colorMemo = HashMap<Int, Color>()

// TODO: need blue-white-red nonlinear(?) gradient here! Consult Jan's video.
internal fun blueRedGradient(n: Int): Color {
    val corrected = when {
        n > 255 -> 255
        n < 0 -> 0
        else -> n
    }
    val b = 255 - corrected
    val r = 255 - b
    val g = 0

    var rgb = r;
    rgb = (rgb shl 8) + g;
    rgb = (rgb shl 8) + b;

    var color = colorMemo[rgb]
    if (color == null) {
        color = Color (r, g, b)
        colorMemo.put(rgb, color)
    }
    return color
}

internal fun cellColor(normalized: Double): Color {
    return blueRedGradient((normalized * 255).toInt())
}

// TODO: fix for non-square lattice
fun CellD2Q9.drawDensity(i: Int, j: Int, N: Int,
                         minDensity: Double, maxDensity: Double): Unit {
    val Rho = computeRho(f)
    val color = cellColor(normalize(Rho, minDensity, maxDensity))
    StdDraw.setPenColor(color);
    StdDraw.filledSquare(j - 0.5, N - i + 0.5, 0.45);
}

// TODO: fix for non-square lattice
fun LatticeD2Q9.drawDensityTable(minDensity: Double, maxDensity: Double): Unit {
    val N = (cells[0].size + cells.size) / 2 // (rows + cols) / 2

    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            cells[i][j].drawDensity(i + 1, j + 1, N, minDensity, maxDensity)
        }
    }
}