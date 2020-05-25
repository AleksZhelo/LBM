package com.alekseyzhelo.lbm.core.cell

import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9

/**
 * @author Aleks on 17-07-2016.
 */

// FAR AWAY TODO: Should be possible to optimize memory usage by having barebones cells for non-streamable materials
class MaterialCellD2Q9(val material: Material, dynamics: Dynamics2DQ9): CellD2Q9(dynamics) {

}