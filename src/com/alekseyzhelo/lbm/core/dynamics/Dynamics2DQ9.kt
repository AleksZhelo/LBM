package com.alekseyzhelo.lbm.core.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9

/**
 * @author Aleks on 18-05-2016.
 */
interface Dynamics2DQ9 {

    fun collide(cell: CellD2Q9): Unit

}