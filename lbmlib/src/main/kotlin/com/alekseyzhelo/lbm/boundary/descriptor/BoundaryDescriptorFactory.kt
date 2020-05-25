package com.alekseyzhelo.lbm.boundary.descriptor

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType

/**
 * @author Aleks on 16-07-2016.
 */
// TODO: try with complete up/bottom instead of left/right?
object BoundaryDescriptorFactory {

    fun createLeftBoundary(
        type: BoundaryType,
        doubleParam: Double?,
        doubleArrayParam: DoubleArray?,
        LX: Int, LY: Int
    ): BoundaryDescriptor {
        val maxY = LY - 1
        return BoundaryDescriptor(
            BoundaryPosition.LEFT, type, doubleParam, doubleArrayParam,
            0, 0, 1, maxY - 1
        )
    }

    fun createTopBoundary(
        type: BoundaryType,
        doubleParam: Double?,
        doubleArrayParam: DoubleArray?,
        LX: Int, LY: Int
    ): BoundaryDescriptor {
        val maxX = LX - 1
        val maxY = LY - 1
        return BoundaryDescriptor(
            BoundaryPosition.TOP, type, doubleParam, doubleArrayParam,
            1, maxX - 1, maxY, maxY
        )
    }

    fun createRightBoundary(
        type: BoundaryType,
        doubleParam: Double?,
        doubleArrayParam: DoubleArray?,
        LX: Int, LY: Int
    ): BoundaryDescriptor {
        val maxX = LX - 1
        val maxY = LY - 1
        return BoundaryDescriptor(
            BoundaryPosition.RIGHT, type, doubleParam, doubleArrayParam,
            maxX, maxX, 1, maxY - 1
        )
    }

    fun createBottomBoundary(
        type: BoundaryType,
        doubleParam: Double?,
        doubleArrayParam: DoubleArray?,
        LX: Int, LY: Int
    ): BoundaryDescriptor {
        val maxX = LX - 1
        return BoundaryDescriptor(
            BoundaryPosition.BOTTOM, type, doubleParam, doubleArrayParam,
            1, maxX - 1, 0, 0
        )
    }

}