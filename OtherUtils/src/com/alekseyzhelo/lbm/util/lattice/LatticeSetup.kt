package com.alekseyzhelo.lbm.util.lattice

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9

/**
 * @author Aleks on 17-06-2016.
 */

fun createBoundaries(
        left: BoundaryType,
        top: BoundaryType,
        right: BoundaryType,
        bottom: BoundaryType,
        lParam: Double? = null,
        tParam: Double? = null,
        rParam: Double? = null,
        bParam: Double? = null
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

fun setupLattice(cli: CLISettings, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Double?>>): LatticeD2Q9 {
    return setupLattice(cli, BGKDynamicsD2Q9(cli.omega), boundaries)
}

fun setupLattice(cli: CLISettings, dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, Pair<BoundaryType, Double?>>): LatticeD2Q9 {
    val lattice = LatticeD2Q9(cli.lx, cli.ly, cli.omega, dynamics, boundaries)
    print(lattice)

    return lattice
}