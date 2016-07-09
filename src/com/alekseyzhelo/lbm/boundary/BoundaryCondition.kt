package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import java.util.*

/**
 * @author Aleks on 18-06-2016.
 */
enum class BoundaryPosition(out1: Int, out2: Int, out3: Int) {
    LEFT(3, 6, 7), TOP(2, 5, 6), RIGHT(1, 5, 8), BOTTOM(4, 7, 8);

    val outgoing = listOf(out1, out2, out3)
    val inside: List<Int>

    init {
        val tmp = ArrayList<Int>()
        for (f in 0..DescriptorD2Q9.Q - 1) {
            if (!outgoing.contains(f)) {
                tmp.add(f)
            }
        }
        inside = tmp
    }
}

enum class BoundaryType {
    PERIODIC, NO_SLIP, SLIDING, ZHOU_HE_UX
}

abstract class BoundaryCondition(protected val lattice: LatticeD2Q9,
                                 val x0: Int, val x1: Int, val y0: Int, val y1: Int) {

    abstract fun boundaryStream()
    abstract fun getType(): BoundaryType
    abstract fun getParam(): Double?
    abstract fun streamOutgoing(i: Int, j: Int)

}