package com.alekseyzhelo.lbm.boundary

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2
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
    PERIODIC, NO_SLIP, SLIDING, ZHOU_HE_UX, INLET, OUTLET
}

abstract class BoundaryCondition(
    val position: BoundaryPosition, protected val lattice: LatticeD2<*>,
    val x0: Int, val x1: Int, val y0: Int, val y1: Int
) {

    abstract fun boundaryStream()
    abstract fun getType(): BoundaryType
    abstract fun streamOutgoing(i: Int, j: Int)

    fun contains(i: Int, j: Int): Boolean {
        return i >= x0 && i <= x1 && j >= y0 && j <= y1
    }

    // TODO: optimize setting boundary rho, U

    open fun defineBoundaryRhoU(rho: Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, U)
            }
        }
    }

    open fun defineBoundaryRhoU(rho: Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho, U(i, j))
            }
        }
    }

    open fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), U)
            }
        }
    }

    open fun defineBoundaryRhoU(rho: (i: Int, j: Int) -> Double, U: (i: Int, j: Int) -> DoubleArray) {
        for (i in x0..x1) {
            for (j in y0..y1) {
                lattice.cells[i][j].defineRhoU(rho(i, j), U(i, j))
            }
        }
    }
}