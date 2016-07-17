package com.alekseyzhelo.lbm.core.cell

import java.util.*

/**
 * @author Aleks on 17-07-2016.
 */

val streamable = EnumSet.of(Material.FLOW, Material.INFLOW, Material.OUTFLOW)

enum class Material(val color: Int) {
    NOTHING(0xFFFFFFFF.toInt()),
    FLOW(0xFF000000.toInt()),
    SOLID(0xFFC83200.toInt()),
    INFLOW(0xFF0000FF.toInt()),
    OUTFLOW(0xFF00FF00.toInt())
}

