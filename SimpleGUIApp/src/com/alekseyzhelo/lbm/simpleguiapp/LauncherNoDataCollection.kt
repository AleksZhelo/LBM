package com.alekseyzhelo.lbm.simpleguiapp

import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.functions.multiplePressureWaveRho
import com.alekseyzhelo.lbm.simpleguiapp.util.initGraphicsWindow
import com.alekseyzhelo.lbm.simpleguiapp.util.setupLattice
import com.alekseyzhelo.lbm.simpleguiapp.util.setupVisualizer
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import java.text.DecimalFormat

/**
 * @author Aleks on 18-05-2016.
 */

val squareEmptyRho: (lx: Int, ly: Int, squareX: Int, squareY: Int, squareRho: Double) -> (i: Int, j: Int) -> Double
        =
        { lx, ly, squareX, squareY, squareRho ->
            val balancedRho = 1.0 + (squareX * squareY * (1.0 - squareRho)) /
                    (lx * ly - squareX * squareY).toDouble()
            val centerX = lx / 2
            val centerY = ly / 2
            { i: Int, j: Int ->
                when {
                    (i >= centerX - squareX / 2) && (i <= centerX + squareX / 2)
                            && (j >= centerY - squareY / 2) && (j <= centerY + squareY / 2) -> squareRho
                    else -> balancedRho
                }
            }
        }

fun main(args: Array<String>) {
    val cli = collectArguments("SimpleGUIApp.jar", args)
    val lattice = setupLattice(cli)
    lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 10, 10, 3.0), doubleArrayOf(0.0, 0.0))

    println("Min density: ${lattice.minDensity()}")
    println("Max density: ${lattice.maxDensity()}")
    println("Max velocity norm: ${lattice.maxVelocityNorm()}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }
    val visualize = setupVisualizer(cli, lattice)

    initGraphicsWindow(cli)

    visualize()
    printLine("Total density: ${lattice.totalDensity()}")

    if (cli.stop) readLine()

    val noCollisions = cli.noCollisions
    var time = 0
    val start = System.currentTimeMillis()
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        if (noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.collideParallel()
        visualize()
        //printLine("Total density: ${lattice.totalDensity()}")
    }
    time--
    val end = System.currentTimeMillis()

    val formatter = DecimalFormat("#0.00000");
    println("Execution time: ${formatter.format((end - start) / 1000.0)} seconds");
    print("Executed $time LBM steps.")
}

