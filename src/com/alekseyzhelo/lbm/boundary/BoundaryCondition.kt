package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

/**
 * @author Aleks on 18-06-2016.
 */
enum class BoundaryPosition {
    LEFT, TOP, RIGHT, BOTTOM
}

enum class BoundaryType {
    PERIODIC, NO_SLIP, SLIDING, ZHOU_HE_UX
}

// TODO: handle corners automatically!
abstract class BoundaryCondition(protected val lattice: LatticeD2Q9,
                                 val x0: Int, val x1: Int, val y0: Int, val y1: Int) {

    abstract fun boundaryStream()
    abstract fun getType(): BoundaryType
    abstract fun getParam(): Double?

}