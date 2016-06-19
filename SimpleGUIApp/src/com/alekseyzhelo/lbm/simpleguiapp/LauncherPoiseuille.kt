package com.alekseyzhelo.lbm.simpleguiapp

import com.alekseyzhelo.lbm.boundary.BoundaryPosition
import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.dynamics.ConstantXForce_BGK_D2Q9
import com.alekseyzhelo.lbm.simpleguiapp.util.initGraphicsWindow
import com.alekseyzhelo.lbm.simpleguiapp.util.setupLattice
import com.alekseyzhelo.lbm.simpleguiapp.util.setupVisualizer
import com.alekseyzhelo.lbm.simpleguiapp.util.toDoubleArrayFile
import com.alekseyzhelo.lbm.util.format
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import sampleXSpeedAveragedByX
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

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
    val boundaries = mapOf(
            Pair(BoundaryPosition.LEFT, BoundaryType.PERIODIC),
            Pair(BoundaryPosition.TOP, BoundaryType.NO_SLIP),
            Pair(BoundaryPosition.RIGHT, BoundaryType.PERIODIC),
            Pair(BoundaryPosition.BOTTOM, BoundaryType.NO_SLIP)
    )
    val force = 0.00001
    val lattice = setupLattice(cli, ConstantXForce_BGK_D2Q9(cli.omega, force), boundaries)
    //lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 1, 1, 1.5), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(columnPressureWaveRho(cli.lx, cli.ly, 10, 1.5), doubleArrayOf(0.0, 0.0))
    lattice.iniEquilibrium(1.0, doubleArrayOf(0.0, 0.0))

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
    val ySamples = ArrayList<DoubleArray>()
    ySamples.add(sampleXSpeedAveragedByX(lattice))
    while (time++ < cli.time) {
        lattice.stream()
        if (noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.bulkCollideParallel(0, cli.lx - 1, 0, cli.ly - 1)
        visualize()
        if (time % 300 == 0) {
            ySamples.add(sampleXSpeedAveragedByX(lattice))
        }
        if (time % 1000 == 0) {
            printLine("Iteration: $time")
            val testVel = lattice.cells[50][50].computeRhoU(lattice.cells[50][50].f)
            printLine("Test velocity: (${testVel[0].format()}, ${testVel[1].format()})")
        }
        //printLine("Min density: ${lattice.minDensity()}")
        //printLine("Max density: ${lattice.maxDensity()}")
        //printLine("Min velocity: ${lattice.minVelocityNorm()}")
        //printLine("Max velocity: ${lattice.maxVelocityNorm()}")
        //printLine("Total density: ${lattice.totalDensity()}")
        //printLine("0,0 density: ${lattice.cells[0][0].computeRho()}")
        //printLine("lx/2,ly/2 density: ${lattice.cells[cli.lx/2][cli.ly/2].computeRho()}")
    }
    time--
    val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.S")

    ySamples.toDoubleArrayFile("Poiseuille_force_${force}_${cli.lx}x${cli.ly}_${cli.omega}_omega_${cli.time}_iterations_${dateFormat.format(Date())}.txt")
    val end = System.currentTimeMillis()

    val formatter = DecimalFormat("#0.00000");
    println("Execution time: ${formatter.format((end - start) / 1000.0)} seconds");
    println("Executed $time LBM steps.")

    printLine("Total density: ${lattice.totalDensity()}")


}

