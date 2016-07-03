package com.alekseyzhelo.lbm.util.sampling

import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import java.util.*

/**
 * @author Aleks on 19-06-2016.
 */

fun sampleAverageXSpeed(lattice: LatticeD2Q9, atY: Int): Double {
    var speed = 0.0
    for (x in lattice.cells.indices) {
        speed += lattice.cells[x][atY].computeRhoU(lattice.cells[x][atY].f)[0];
    }
    speed /= lattice.cells.size

    return speed
}

fun sampleXSpeed(lattice: LatticeD2Q9, atX: Int): DoubleArray {
    val speed = ArrayList<Double>()
    for (y in lattice.cells[atX].indices) {
        speed.add(lattice.cells[atX][y].computeRhoU(lattice.cells[atX][y].f)[0])
    }

    return speed.toDoubleArray()
}


fun sampleXSpeedAveragedByX(lattice: LatticeD2Q9): DoubleArray {
    val speed = DoubleArray(lattice.LY)
    for (x in lattice.cells.indices) {
        for (y in lattice.cells[x].indices) {
            speed[y] += (lattice.cells[x][y].computeRhoU(lattice.cells[x][y].f)[0])
        }
    }

    for (y in lattice.cells[0].indices) {
        speed[y] = speed[y] / lattice.LX
    }

    return speed
}

fun sampleVectorField(lattice: LatticeD2Q9): Array<DoubleArray> {
    val vectorField = Array(lattice.LX * lattice.LY, { x -> DoubleArray(4) })
    var k = 0
    for (x in lattice.cells.indices) {
        for (y in lattice.cells[x].indices) {
            val U = lattice.cells[x][y].computeRhoU(lattice.cells[x][y].f)
            vectorField[k][0] = x.toDouble()
            vectorField[k][1] = y.toDouble()
            vectorField[k][2] = U[0]
            vectorField[k++][3] = U[1]
        }
    }

    return vectorField
}
