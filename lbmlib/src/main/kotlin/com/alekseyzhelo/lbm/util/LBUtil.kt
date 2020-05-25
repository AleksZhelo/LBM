package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9

/**
 * @author Aleks on 09-07-2016.
 */
fun computeEquilibrium(i: Int, rho: Double, U: DoubleArray, uSqr: Double): Double {
    val c_u = DescriptorD2Q9.c[i][0] * U[0] + DescriptorD2Q9.c[i][1] * U[1]
    return rho * DescriptorD2Q9.w[i] * (1 + 3.0 * c_u + 4.5 * c_u * c_u - 1.5 * uSqr)
}

fun computeFneq(cell: CellD2Q9, rho: Double, U: DoubleArray): DoubleArray {
    val uSqr = normSquare(U)
    val fNeq = DoubleArray(DescriptorD2Q9.Q)
    for (i in 0..DescriptorD2Q9.Q - 1) {
        fNeq[i] = cell[i] - computeEquilibrium(i, rho, U, uSqr)
    }
    return fNeq
}