package com.alekseyzhelo.lbm.simpleguiapp

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.core.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.functions.multiplePressureWaveRho
import com.alekseyzhelo.lbm.simpleguiapp.algs4.drawDensityTable
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import edu.princeton.cs.algs4.StdDraw
import edu.princeton.cs.algs4.StdIn
import java.awt.Color
import java.util.*

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = collectArguments("SimpleGUIApp.jar", args)
    val lattice = setupLattice(cli)
    val rnd = Random()
    //lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 5, 5, 10.0),
    //        { i, j -> doubleArrayOf(0.3 * rnd.nextGaussian(), 0.3 * rnd.nextGaussian()) })
    lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 5, 5, 10.0), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(1.0, diagonalVelocity)
    val maxVelocityNorm = lattice.maxVelocityNorm()
    val minDensity = lattice.minDensity() //0.978
    val maxDensity = 1.23 // lattice.maxDensity()

    println("Min density: ${lattice.minDensity()}")
    println("Max density: ${lattice.maxDensity()}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }

    StdDraw.setCanvasSize(750, 750)
    StdDraw.clear();
    StdDraw.setPenColor(StdDraw.BLACK);
    val N = (cli.lx + cli.ly) / 2
    StdDraw.setXscale(0.0, 1.00 * N);
    StdDraw.setYscale(0.0, 1.00 * N);
    StdDraw.filledSquare(N / 2.0, N / 2.0, N / 2.0);
    StdDraw.show(0);

    lattice.drawDensityTable(minDensity, maxDensity)
    StdDraw.show(0);
    //StdIn.readChar()
//    when (cli.drawVelocities) {
//        true -> lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
//        false -> lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
//    }
    //printLine("Total density: ${lattice.totalDensity()}")

    var time = 0
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.collide()
        StdDraw.clear(Color.BLACK)
        when (cli.drawVelocities) {
        //true -> lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
        //false -> lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
            false -> lattice.drawDensityTable(minDensity, maxDensity)
        }
        //println("Min density: ${lattice.minPressure()}")
        //println("Max density: ${lattice.maxPressure()}")
        StdDraw.show(0);
        if (cli.stopping) StdIn.readChar()
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