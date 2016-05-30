package com.alekseyzhelo.lbm.simpleguiapp

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.core.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.functions.columnPressureWaveRho
import com.alekseyzhelo.lbm.functions.multiplePressureWaveRho
import com.alekseyzhelo.lbm.simpleguiapp.algs4.FasterStdDraw
import com.alekseyzhelo.lbm.simpleguiapp.algs4.drawDensityTable
import com.alekseyzhelo.lbm.simpleguiapp.algs4.drawVelocityNormTable
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import java.awt.Color
import java.util.*

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = collectArguments("SimpleGUIApp.jar", args)
    val lattice = setupLattice(cli)
    val rnd = Random()
    //lattice.iniEquilibrium(1.0, doubleArrayOf(0.1, 0.0))
    //lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 5, 5, 10.0),
    //        { i, j -> doubleArrayOf(0.3 * rnd.nextGaussian(), 0.3 * rnd.nextGaussian()) })
    lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 3, 3, 2.0), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(columnPressureWaveRho(cli.lx, cli.ly, cli.lx / 2, 2.0), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 3, 3, 2.0),
            //{ i, j -> doubleArrayOf(0.01 * rnd.nextGaussian(), 0.01 * rnd.nextGaussian()) })
//    lattice.iniTest { i, j, k ->
//        val waveX = 3
//        val waveY = 3
//        val waveCenterRho = 2.0 * 1.0/9.0
//        val lx = cli.lx
//        val ly = cli.ly
//        val balancedRho = (1.0 - (waveX * waveY * (waveCenterRho - 1.0)) / (lx * ly)) * 1.0 / 9.0
//        val centerX = lx / 2
//        val centerY = ly / 2
//        when {
//            (i >= centerX - waveX / 2) && (i <= centerX + waveX / 2)
//                    && (j >= centerY - waveY / 2) && (j <= centerY + waveY / 2) -> waveCenterRho
//            else -> balancedRho
//        }
//    }
    //lattice.iniEquilibrium(1.0, diagonalVelocity)
    val maxVelocityNorm = lattice.maxVelocityNorm()
    val minDensity = lattice.minDensity() //0.978
    val maxDensity = 1.23 // lattice.maxDensity()

    println("Min density: ${lattice.minDensity()}")
    println("Max density: ${lattice.maxDensity()}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }

    FasterStdDraw.setCanvasSize(750, 750)
    FasterStdDraw.clear();
    FasterStdDraw.setPenColor(FasterStdDraw.BLACK);
    val N = (cli.lx + cli.ly) / 2
    FasterStdDraw.setXscale(0.0, 1.00 * N);
    FasterStdDraw.setYscale(0.0, 1.00 * N);
    FasterStdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
    FasterStdDraw.show(0);

    when (cli.drawVelocities) {
        true -> lattice.drawVelocityNormTable(0.0, lattice.maxVelocityNorm())
        false -> lattice.drawDensityTable(lattice.minDensity(), lattice.maxDensity())
    }
    //printLine("Total density: ${lattice.totalDensity()}")

    //readLine()

    var time = 0
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.collide()
        FasterStdDraw.clear(Color.BLACK)
        when (cli.drawVelocities) {
            true -> lattice.drawVelocityNormTable(0.0, lattice.maxVelocityNorm())
            false -> lattice.drawDensityTable(lattice.minDensity(), lattice.maxDensity())
        }
        //println("Min density: ${lattice.minDensity()}")
        //println("Max density: ${lattice.maxDensity()}")
        FasterStdDraw.show(25);
        if (cli.stopping) readLine()
        //printLine("Total density: ${lattice.totalDensity()}")
    }
    time--

    print("Executed $time LBM steps.")
}


private fun setupLattice(cli: CLISettings): LatticeD2Q9 {
    val lattice = LatticeD2Q9(cli.lx, cli.ly, BGKDynamicsD2Q9(cli.omega))
    print(lattice)

    return lattice
}