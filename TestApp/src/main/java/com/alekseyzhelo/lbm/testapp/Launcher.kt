package com.alekseyzhelo.lbm.testapp

import com.alekseyzhelo.lbm.core.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9
import com.alekseyzhelo.lbm.testapp.cli.CLISettings
import com.beust.jcommander.JCommander

/**
 * @author Aleks on 18-05-2016.
 */
fun main(args: Array<String>) {
    val cli = CLISettings();

    val commander = JCommander(cli);
    commander.setProgramName("TestApp.jar");

    try {
        commander.parse(*args)
    } catch (e: com.beust.jcommander.ParameterException) {
        val builder = StringBuilder()
        builder.appendln(e.message)
        commander.usage(builder)
        print(builder.toString())
        return
    }

    val printLine = { x: Any -> if (cli.verbose) println(x) }

    val lattice = LatticeD2Q9(cli.lx, cli.ly, BGKDynamicsD2Q9(cli.omega))
    print(lattice)

    lattice.testInit(10, 0, 0, 0.5)
    lattice.testInit(0, 0, 1, 1.0)
    lattice.testInit(0, 4, 2, 2.0)
    lattice.testInit(0, 1, 3, 3.0)
    lattice.testInit(1, 8, 4, 4.0)
    lattice.testInit(1, 2, 5, 5.0)
    lattice.testInit(3, 5, 6, 6.0)
    lattice.testInit(6, 6, 7, 7.0)
    lattice.testInit(6, 1, 8, 8.0)

    printLine(lattice.toDensityTable(true))
    printLine("Total density: ${lattice.totalDensity()}")

    var time = 0
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.collide()
        printLine(lattice.toDensityTable(true))
        printLine("Total density: ${lattice.totalDensity()}")
    }
    time--

    print("Executed $time LBM steps.")
}