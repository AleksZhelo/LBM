package com.alekseyzhelo.lbm.boundary.descriptor

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType

/**
 * @author Aleks on 16-07-2016.
 */

// TODO: necessary at all now with the new boundary creation logic?
data class BoundaryDescriptor(
        val position: BoundaryPosition,
        val type: BoundaryType,
        val doubleParam: Double?,
        val doubleArrayParam: DoubleArray?,
        val x0: Int,
        val x1: Int,
        val y0: Int,
        val y1: Int
)