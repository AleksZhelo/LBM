package com.alekseyzhelo.lbm.simpleguiapp.util

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.dynamics.Dynamics2DQ9
import com.alekseyzhelo.lbm.simpleguiapp.algs4.FasterStdDraw
import com.alekseyzhelo.lbm.simpleguiapp.algs4.drawDensityTable
import com.alekseyzhelo.lbm.simpleguiapp.algs4.drawVelocityNormTable
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import java.awt.Color

/**
 * @author Aleks on 17-06-2016.
 */
fun setupLattice(cli: CLISettings): LatticeD2Q9 {
    val boundaries = mapOf(
            Pair(BoundaryPosition.LEFT, BoundaryType.PERIODIC),
            Pair(BoundaryPosition.TOP, BoundaryType.PERIODIC),
            Pair(BoundaryPosition.RIGHT, BoundaryType.PERIODIC),
            Pair(BoundaryPosition.BOTTOM, BoundaryType.PERIODIC)
    )
    return setupLattice(cli, boundaries)
}

fun setupLattice(cli: CLISettings, boundaries: Map<BoundaryPosition, BoundaryType>): LatticeD2Q9 {
    return setupLattice(cli, BGKDynamicsD2Q9(cli.omega), boundaries)
}

fun setupLattice(cli: CLISettings, dynamics: Dynamics2DQ9, boundaries: Map<BoundaryPosition, BoundaryType>): LatticeD2Q9 {
    val lattice = LatticeD2Q9(cli.lx, cli.ly, dynamics, boundaries)
    print(lattice)

    return lattice
}

fun setupVisualizer(cli: CLISettings, lattice: LatticeD2Q9, delay: Int = 25): () -> Unit {
    return when (cli.headless) {
        false -> { ->
            val maxVelocityNorm = lattice.maxVelocityNorm()
            val minDensity = lattice.minDensity()
            val maxDensity = lattice.maxDensity()

            val velocityMax = if (cli.noRescale) { -> maxVelocityNorm } else { -> lattice.maxVelocityNorm() }
            val densityMin = if (cli.noRescale) { -> minDensity } else { -> lattice.minDensity() }
            val densityMax = if (cli.noRescale) { -> maxDensity } else { -> lattice.maxDensity() }
            {
                FasterStdDraw.clear(Color.BLACK)
                when (cli.drawVelocities) {
                    true -> lattice.drawVelocityNormTable(0.0, velocityMax())
                    false -> lattice.drawDensityTable(densityMin(), densityMax())
                }
                FasterStdDraw.show(delay);
            }
        }
        true -> { -> { -> } }
    }()  // TODO: so is this stupid or what?
}

fun initGraphicsWindow(cli: CLISettings, width: Int = 750, height: Int = 750) {
    if (!cli.headless) {
        FasterStdDraw.setCanvasSize(width, height)
        FasterStdDraw.setXscale(0.0, cli.lx.toDouble());
        FasterStdDraw.setYscale(0.0, cli.ly.toDouble());
    }
}