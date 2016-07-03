package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

/**
 * @author Aleks on 18-06-2016.
 */

object D2BoundaryFactory {

    fun create(position: BoundaryPosition, type: BoundaryType, lattice: LatticeD2Q9,
               x0: Int, x1: Int, y0: Int, y1: Int, wallVelocity: Double? = null): BoundaryCondition {
        return when (type) {
            BoundaryType.PERIODIC -> PeriodicBoundary(lattice, x0, x1, y0, y1)
            BoundaryType.NO_SLIP -> NoSlipBoundary(position, lattice, x0, x1, y0, y1)
            BoundaryType.SLIDING -> SlidingBoundary(position, lattice, x0, x1, y0, y1, wallVelocity!!)
            BoundaryType.ZHOU_HE_UX -> ZhouHeUXBoundary(position, lattice, x0, x1, y0, y1, wallVelocity!!)
        }
    }

}