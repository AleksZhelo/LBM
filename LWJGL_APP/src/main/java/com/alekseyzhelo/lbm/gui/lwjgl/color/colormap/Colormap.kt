package com.alekseyzhelo.lbm.gui.lwjgl.color.colormap

import com.alekseyzhelo.lbm.gui.lwjgl.color.FloatColor

/**
 * @author Aleks on 03-07-2016.
 */

interface Colormap {
    fun getColor(normalized: Float): FloatColor
    fun getName(): String
}

internal val checkIsNan = false // TODO: bad practice?