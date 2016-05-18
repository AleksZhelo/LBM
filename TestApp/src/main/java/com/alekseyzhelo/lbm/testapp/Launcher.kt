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

    val lattice = LatticeD2Q9(cli.lx, cli.ly, BGKDynamicsD2Q9(cli.omega))
    print(lattice)

    var time = 0
    while (time++ < cli.time) {
        lattice.streamPeriodic()
        lattice.collide()
    }
    time--

    print("Executed $time LBM steps.")
}