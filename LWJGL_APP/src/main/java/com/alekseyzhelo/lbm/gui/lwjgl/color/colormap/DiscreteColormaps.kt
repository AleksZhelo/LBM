package com.alekseyzhelo.lbm.gui.lwjgl.color.colormap

import com.alekseyzhelo.lbm.gui.lwjgl.color.FloatColor
import com.alekseyzhelo.lbm.gui.lwjgl.util.ResourcesUtil

/**
 * @author Aleks on 03-07-2016.
 */

object CoolwarmDiscreteColormap : Colormap {

    private val r: FloatArray
    private val g: FloatArray
    private val b: FloatArray
    private val range: Int

    init {
        val fileEntries = ResourcesUtil.loadCSVResource("/colormaps/CoolWarmFloat257.csv")
        val skippedFirst = fileEntries.subList(1, fileEntries.size)
        range = skippedFirst.size - 1
        r = FloatArray(range + 1)
        g = FloatArray(range + 1)
        b = FloatArray(range + 1)
        for (i in 0..range) {
            r[i] = skippedFirst[i][1].toFloat()
            g[i] = skippedFirst[i][2].toFloat()
            b[i] = skippedFirst[i][3].toFloat()
        }
    }

    override fun getColor(normalized: Float): FloatColor {
        val index = (normalized * range).toInt()
        return FloatColor(r[index], g[index], b[index])
    }

    override fun getName(): String {
        return "coolwarm"
    }

}