package com.alekseyzhelo.lbm.util.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptor
import com.alekseyzhelo.lbm.boundary.descriptor.BoundaryDescriptorFactory
import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import java.util.*

/**
 * @author Aleks on 17-06-2016.
 */

fun createBoundaries(
        left: BoundaryType,
        top: BoundaryType,
        right: BoundaryType,
        bottom: BoundaryType,
        lParam: Pair<Double, DoubleArray>? = null,
        tParam: Pair<Double, DoubleArray>? = null,
        rParam: Pair<Double, DoubleArray>? = null,
        bParam: Pair<Double, DoubleArray>? = null
) = mapOf(
        Pair(BoundaryPosition.LEFT, Pair(left, lParam)),
        Pair(BoundaryPosition.TOP, Pair(top, tParam)),
        Pair(BoundaryPosition.RIGHT, Pair(right, rParam)),
        Pair(BoundaryPosition.BOTTOM, Pair(bottom, bParam))
)

fun setupLattice(cli: CLISettings): LatticeD2Q9 {
    val boundaries = createBoundaries(
            BoundaryType.PERIODIC,
            BoundaryType.PERIODIC,
            BoundaryType.PERIODIC,
            BoundaryType.PERIODIC
    )
    return setupLattice(cli, boundaries)
}

fun setupLattice(cli: CLISettings, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Pair<Double, DoubleArray>?>>): LatticeD2Q9 {
    return setupLattice(cli, BGKDynamicsD2Q9(cli.omega), boundaries)
}

fun setupLattice(cli: CLISettings, dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Pair<Double, DoubleArray>?>>): LatticeD2Q9 {
    val listBoundaries = convertLegacyBoundaries(boundaries, cli)
    val lattice = LatticeD2Q9(cli.lx, cli.ly, cli.omega, dynamics, listBoundaries)
    print(lattice)

    return lattice
}

private fun convertLegacyBoundaries(boundaries: Map<BoundaryPosition, Pair<BoundaryType, Pair<Double, DoubleArray>?>>, cli: CLISettings): ArrayList<BoundaryDescriptor> {
    val createDescriptor = { position: BoundaryPosition ->
        { type: BoundaryType,
          doubleParam: Double?,
          doubleArrayParam: DoubleArray?,
          LX: Int, LY: Int ->
            when (position) {
                BoundaryPosition.LEFT -> BoundaryDescriptorFactory.createLeftBoundary(type, doubleParam, doubleArrayParam, LX, LY)
                BoundaryPosition.TOP -> BoundaryDescriptorFactory.createTopBoundary(type, doubleParam, doubleArrayParam, LX, LY)
                BoundaryPosition.RIGHT -> BoundaryDescriptorFactory.createRightBoundary(type, doubleParam, doubleArrayParam, LX, LY)
                BoundaryPosition.BOTTOM -> BoundaryDescriptorFactory.createBottomBoundary(type, doubleParam, doubleArrayParam, LX, LY)
            }
        }
    }
    val listBoundaries = ArrayList<BoundaryDescriptor>()
    for ((key, entry) in boundaries) {
        listBoundaries.add(createDescriptor(key)(
                entry.first,
                entry.second?.component1(),
                entry.second?.component2(),
                cli.lx,
                cli.ly
        ))
    }
    return listBoundaries
}