package com.alekseyzhelo.lbm.gui.lwjgl.color.colormap

import com.alekseyzhelo.lbm.gui.lwjgl.color.FloatColor

/**
 * @author Aleks on 03-07-2016.
 */


// NOTODO: fix Mach bands from interpolating in RGB (by interpolating in Msh?)
// apparently that's not so easy to do, I don't want to go too deep into this
object CoolwarmRGBInterpolatedColormap: Colormap {
    private val x = floatArrayOf(0.0f, 0.5f, 1.0f)
    private val r = floatArrayOf(0.230f, 0.865f, 0.706f)
    private val g = floatArrayOf(0.299f, 0.865f, 0.016f)
    private val b = floatArrayOf(0.754f, 0.865f, 0.150f)

    override fun getColor(normalized: Float): FloatColor {
        val low = when {
            normalized < 0.50f -> 0
            else -> 1
        }
        val high = low + 1
        val p = (normalized - x[low]) / (x[high] - x[low])
        val oneMinP = 1.0f - p
        return FloatColor(oneMinP * r[low] + p * r[high], oneMinP * g[low] + p * g[high], oneMinP * b[low] + p * b[high])
    }

    override fun getName(): String {
        return "coolwarm-rgb-interpolated"
    }

}

object BluerJetColormap: Colormap {
    private val x = floatArrayOf(0.0f, 0.15f, 0.4f, 0.5f, 0.65f, 0.8f, 1.0f)
    private val r = floatArrayOf(0.0f, 0.0f, 0.0f, 0.56470588f, 1.0f, 1.0f, 0.54509804f)
    private val g = floatArrayOf(0.0f, 0.0f, 1.0f, 0.93333333f, 1.0f, 0.0f, 0.0f)
    private val b = floatArrayOf(0.54509804f, 1.0f, 1.0f, 0.56470588f, 0.0f, 0.0f, 0.0f)

    override fun getColor(normalized: Float): FloatColor {
        val low = when {
            normalized < 0.15f -> 0
            normalized < 0.40f -> 1
            normalized < 0.50f -> 2
            normalized < 0.65f -> 3
            normalized < 0.80f -> 4
            else -> 5
        }
        val high = low + 1
        val p = (normalized - x[low]) / (x[high] - x[low])
        val oneMinP = 1.0f - p
        return FloatColor(oneMinP * r[low] + p * r[high], oneMinP * g[low] + p * g[high], oneMinP * b[low] + p * b[high])
    }

    override fun getName(): String {
        return "bluerJet"
    }

}

object BlueRedColormap : Colormap {
    override fun getColor(normalized: Float): FloatColor {
        return FloatColor(normalized, 0.0f, 1.0f - normalized)
    }

    override fun getName(): String {
        return "blueRed"
    }

}