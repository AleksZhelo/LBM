package com.alekseyzhelo.lbm.testapp

import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.functions.pressureWaveRho
import com.alekseyzhelo.lbm.testapp.curses.blue
import com.alekseyzhelo.lbm.testapp.curses.drawPressureRatedTable
import com.alekseyzhelo.lbm.testapp.curses.drawVelocityRatedTable
import com.alekseyzhelo.lbm.util.*
import com.alekseyzhelo.lbm.util.lattice.setupLattice
import jcurses.system.Toolkit

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = collectArguments("TestApp.jar", args)
    val lattice = setupLattice(cli)
    lattice.iniEquilibrium(pressureWaveRho(cli.lx, cli.ly, 1.4), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(1.0, diagonalVelocity)
    val maxVelocityNorm = lattice.maxVelocityNorm()
    val minPressure = lattice.minDensity()
    val maxPressure = lattice.maxDensity()
    val velocityRatedValue = { U: DoubleArray -> printCellVelocity(U) }
    val pressureRatedValue = { Rho: Double -> printCellPressure(Rho) }

    jcurses.system.Toolkit.init()

    val printLine = { x: Any -> if (cli.verbose) println(x) }

    Toolkit.clearScreen(blue)
    when (cli.drawVelocities) {
        true -> lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
        false -> lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
    }
    if (cli.stop) Toolkit.readCharacter()
    //printLine("Total density: ${lattice.totalDensity()}")

    var time = 0
    while (time++ < cli.time) {
        lattice.stream()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.bulkCollide(0, cli.lx - 1, 0, cli.ly - 1)
        when (cli.drawVelocities) {
            true -> lattice.drawVelocityRatedTable(0.0, maxVelocityNorm, velocityRatedValue)
            false -> lattice.drawPressureRatedTable(minPressure, maxPressure, pressureRatedValue)
        }
        if (cli.stop) Toolkit.readCharacter()
        //printLine("Total density: ${lattice.totalDensity()}")
    }
    time--

    Toolkit.clearScreen(blue)
    print("Executed $time LBM steps.")
}


