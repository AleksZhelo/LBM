package com.alekseyzhelo.lbm.simpleguiapp.algs4

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.util.norm
import com.alekseyzhelo.lbm.util.normalize
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

    var color = colorMemo[corrected]
    if (color == null) {
        val b = 255 - corrected
        val r = 255 - b
        val g = 0

//    var rgb = r;
//    rgb = (rgb shl 8) + g;
//    rgb = (rgb shl 8) + b;

        color = Color (r, g, b)
        colorMemo.put(corrected, color)
    }
    return color
}

internal fun cellColor(normalized: Double): Color {
    return blueRedGradient((normalized * 255).toInt())
}

// TODO: fix for non-square lattice
internal fun drawScalarValue(value: Double, i: Int, j: Int, N: Int,
                             minValue: Double, maxValue: Double): Unit {
    val color = cellColor(normalize(value, minValue, maxValue))
    FasterStdDraw.setPenColor(color);
    //FasterStdDraw.deferredFilledSquare(j - 0.5, N - i + 0.5, 0.45);
    FasterStdDraw.deferredFilledSquare(j - 0.5, N - i + 0.5, 1.0); // double r
}

// TODO: fix for non-square lattice
fun LatticeD2Q9.drawDensityTable(minDensity: Double, maxDensity: Double): Unit {
    val N = (cells[0].size + cells.size) / 2 // (rows + cols) / 2

    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            drawScalarValue(cells[i][j].computeRho(cells[i][j].f), i + 1, j + 1, N, minDensity, maxDensity)
        }
    }
}

// TODO: fix for non-square lattice
fun LatticeD2Q9.drawVelocityNormTable(minVelocityNorm: Double, maxVelocityNorm: Double): Unit {
    val N = (cells[0].size + cells.size) / 2 // (rows + cols) / 2

    for (j in cells[0].size - 1 downTo 0) {
        for (i in cells.indices) {
            drawScalarValue(
                    norm(cells[i][j].computeRhoU(cells[i][j].f)),
                    i + 1, j + 1, N, minVelocityNorm, maxVelocityNorm
            )
        }
    }
}