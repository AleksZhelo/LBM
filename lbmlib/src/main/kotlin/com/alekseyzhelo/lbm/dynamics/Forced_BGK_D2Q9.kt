package com.alekseyzhelo.lbm.dynamics

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9

/**
 * @author Aleks on 19-06-2016.
 */


class ConstantXForce_BGK_D2Q9(omega: Double, val K: Double): BGKDynamicsD2Q9(omega) {

    val mult = 1.0 / 6.0

    override fun collide(cell: CellD2Q9) {
        super.collide(cell)
        for (i in 0..DescriptorD2Q9.Q - 1) {
            cell[i] += DescriptorD2Q9.c[i][0]  * mult * K
        }
    }

}