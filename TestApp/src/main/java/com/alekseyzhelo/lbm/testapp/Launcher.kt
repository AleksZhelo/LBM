package com.alekseyzhelo.lbm.testapp

import com.alekseyzhelo.lbm.core.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.testapp.cli.CLISettings
import com.alekseyzhelo.lbm.testapp.curses.*
import com.alekseyzhelo.lbm.util.printCellPressure
import com.alekseyzhelo.lbm.util.printCellVelocityNorm
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import jcurses.system.Toolkit

/**
 * @author Aleks on 18-05-2016.
 */

val pressureWaveRho: (cellCount: Int) -> (i: Int, j: Int) -> Double
        =
        { cellCount ->
            val balancedRho = 1.0 - 0.4 / cellCount
            { i: Int, j: Int ->
                if (i == 5 && j == 5) {
                    1.4
                } else {
                    balancedRho
                }
            }
        }

val triplePressureWaveRho: (cellCount: Int) -> (i: Int, j: Int) -> Double
        =
        { cellCount ->
            val balancedRho = 1.0 - 3 * 0.4 / cellCount
            { i: Int, j: Int ->
                when {
                    i == 5 && j == 5 -> 1.4
                    i == 3 && j == 7 -> 1.4
                    i == 7 && j == 3 -> 1.4
                    else -> balancedRho
                }
            }
        }

val diagonalVelocity = { i: Int, j: Int ->
    if ((i <= 7 && i >= 4) && (j <= 7 && j >= 4)) {
        doubleArrayOf(0.5, -0.5)
    } else {
        doubleArrayOf(0.0, 0.0)
    }
}

// TODO: implement
val test: (cellCount: Int) -> (i: Int, j: Int) -> DoubleArray
        =
        { cellCount ->
            { i: Int, j: Int ->
                if ((i <= 7 && i >= 4) && (j <= 7 && j >= 4)) {
                    doubleArrayOf(0.5, -0.5)
                } else {
                    doubleArrayOf(0.0, 0.0)
                }
            }
        }

fun main(args: Array<String>) {
    val cli = collectArguments(args)
    val lattice = setupLattice(cli)
    lattice.iniEquilibrium(pressureWaveRho(cli.lx * cli.ly), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(1.0, diagonalVelocity)
    val maxVelocityNorm = lattice.maxVelocityNorm()
    val minPressure = lattice.minPressure()
    val maxPressure = lattice.maxPressure()
    val velocityRatedValue = { U: DoubleArray -> printCellVelocityNorm(U) }
    val pressureRatedValue = { Rho: Double -> printCellPressure(Rho) }

    jcurses.system.Toolkit.init()

    val printLine = { x: Any -> if (cli.verbose) println(x) }

    Toolkit.clearScreen(blue)
    //lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
    lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
    Toolkit.readCharacter()
    //printLine("Total density: ${lattice.totalDensity()}")

    var time = 0
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.collide()
        //lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
        lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
        //Toolkit.readCharacter()
        //printLine("Total density: ${lattice.totalDensity()}")
    }
    time--

    Toolkit.clearScreen(blue)
    print("Executed $time LBM steps.")
}

private fun collectArguments(args: Array<String>): CLISettings {
    val cli = CLISettings();

    val commander = JCommander(cli);
    commander.setProgramName("TestApp.jar");

    try {
        commander.parse(*args)
    } catch (e: ParameterException) {
        val builder = StringBuilder()
        builder.appendln(e.message)
        commander.usage(builder)
        print(builder.toString())
        System.exit(1)
    } finally {
        return cli
    }
}

private fun setupLattice(cli: CLISettings): LatticeD2Q9 {
    val lattice = LatticeD2Q9(cli.lx, cli.ly, BGKDynamicsD2Q9(cli.omega))
    print(lattice)

    return lattice
}