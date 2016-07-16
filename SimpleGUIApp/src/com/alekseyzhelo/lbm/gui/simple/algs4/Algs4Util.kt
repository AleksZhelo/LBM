package com.alekseyzhelo.lbm.gui.simple.algs4

import com.alekseyzhelo.lbm.core.lattice.LatticeD2
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

internal fun drawScalarValue(value: Double, i: Int, j: Int,
                             minValue: Double, maxValue: Double): Unit {
    val color = cellColor(normalize(value, minValue, maxValue))
    FasterStdDraw.setPenColor(color)
    // TODO: why is j + 1 necessary? j leaves an empty row at the top..
    FasterStdDraw.deferredFilledSquareTest((i).toDouble(), (j + 1).toDouble(), 1.0) // double r
}

internal fun drawVectorValue(value: DoubleArray, i: Int, j: Int,
                             minValue: Double, maxValue: Double): Unit {
    val color = cellColor(normalize(norm(value), minValue, maxValue))
    val ort = normalize(value)
    FasterStdDraw.setPenColor(color)
    FasterStdDraw.drawArrowLineTest((i).toDouble(), (j).toDouble(), i + ort[0], j + ort[1], 0.3, 0.2)
}

// TODO: how bad is this from an object-oriented design perspective?
fun LatticeD2.drawDensityTable(minDensity: Double, maxDensity: Double): Unit {
    for (i in cells.indices) {
        for (j in cells[0].indices) {
            drawScalarValue(cells[i][j].computeRho(), i, j, minDensity, maxDensity)
        }
    }
}

fun LatticeD2.drawVelocityNormTable(minVelocityNorm: Double, maxVelocityNorm: Double): Unit {
    for (i in cells.indices) {
        for (j in cells[0].indices) {
            drawScalarValue(
                    norm(cells[i][j].computeRhoU()),
                    i, j, minVelocityNorm, maxVelocityNorm
            )
        }
    }
}

fun LatticeD2.drawVelocityVectorTable(minVelocityNorm: Double, maxVelocityNorm: Double): Unit {
    for (i in cells.indices) {
        for (j in cells[0].indices) {
            drawVectorValue(
                    cells[i][j].computeRhoU(),
                    i, j, minVelocityNorm, maxVelocityNorm
            )
        }
    }
}