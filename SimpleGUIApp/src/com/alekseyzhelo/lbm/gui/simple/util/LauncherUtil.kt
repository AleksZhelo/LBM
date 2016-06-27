package com.alekseyzhelo.lbm.gui.simple.util

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.gui.simple.algs4.FasterStdDraw
import com.alekseyzhelo.lbm.gui.simple.algs4.drawDensityTable
import com.alekseyzhelo.lbm.gui.simple.algs4.drawVelocityNormTable
import com.alekseyzhelo.lbm.gui.simple.algs4.drawVelocityVectorTable
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import java.awt.Color

fun setupVisualizer(cli: CLISettings, lattice: LatticeD2Q9, delay: Int = 25): () -> Unit {
    return when (cli.headless) {
        false -> { ->
            val maxVelocityNorm = lattice.maxVelocityNorm()
            val minDensity = lattice.minDensity()
            val maxDensity = lattice.maxDensity()

            val velocityMax = if (cli.noRescale) { -> maxVelocityNorm } else { -> lattice.maxVelocityNorm() }
            val densityMin = if (cli.noRescale) { -> minDensity } else { -> lattice.minDensity() }
            val densityMax = if (cli.noRescale) { -> maxDensity } else { -> lattice.maxDensity() }

            val drawVelocities = cli.drawVelocities
            val vectorField = cli.vectorField
            {
                FasterStdDraw.clear(Color.BLACK)
                when (drawVelocities) {
                    true -> {
                        when (vectorField) {
                            true -> lattice.drawVelocityVectorTable(0.0, velocityMax())
                            false -> lattice.drawVelocityNormTable(0.0, velocityMax())
                        }
                    }
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