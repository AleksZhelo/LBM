package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2

/**
 * @author Aleks on 18-06-2016.
 */

object D2BoundaryFactory {

    fun create(
        position: BoundaryPosition, type: BoundaryType, lattice: LatticeD2,
        x0: Int, x1: Int, y0: Int, y1: Int,
        doubleParam: Double? = null, doubleArrayParam: DoubleArray? = null
    ): BoundaryCondition {
        return when (type) {
            BoundaryType.PERIODIC -> PeriodicBoundary(position, lattice, x0, x1, y0, y1)
            BoundaryType.NO_SLIP -> NoSlipBoundary(position, lattice, x0, x1, y0, y1)
            BoundaryType.SLIDING -> SlidingBoundary(position, lattice, x0, x1, y0, y1, doubleArrayParam!!)
            BoundaryType.ZHOU_HE_UX -> ZhouHeUXBoundary(position, lattice, x0, x1, y0, y1, doubleArrayParam!!)
            BoundaryType.INLET -> InletBoundary(position, lattice, x0, x1, y0, y1, doubleParam!!, doubleArrayParam!!)
            BoundaryType.OUTLET -> OutletBoundary(position, lattice, x0, x1, y0, y1, doubleParam!!)
        }
    }

}