package com.alekseyzhelo.lbm.gui.simple

import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.functions.shearWaveMaxVelocityY
import com.alekseyzhelo.lbm.functions.shearWaveVelocity
import com.alekseyzhelo.lbm.gui.simple.util.initGraphicsWindow
import com.alekseyzhelo.lbm.gui.simple.util.setupVisualizer
import com.alekseyzhelo.lbm.util.*
import com.alekseyzhelo.lbm.util.lattice.setupLattice
import com.alekseyzhelo.lbm.util.sampling.sampleAverageXSpeed
import com.alekseyzhelo.lbm.util.sampling.sampleXSpeed
import com.alekseyzhelo.lbm.util.sampling.toDoubleArrayFile
import com.alekseyzhelo.lbm.util.sampling.toFile
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = collectArguments("SimpleGUIApp.jar", args)
    val lattice = setupLattice(cli)

    val shearA0Max = 0.1
    val kNum = 0
    lattice.iniEquilibrium(1.0, shearWaveVelocity(cli.ly.toDouble(), kNum, shearA0Max))

    println("Min density: ${lattice.minDensity()}")
    println("Max density: ${lattice.maxDensity()}")
    println("Max velocity norm: ${lattice.maxVelocityNorm()}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }
    val visualize = setupVisualizer(cli, lattice)

    initGraphicsWindow(cli)

    visualize()
    printLine("Total density: ${lattice.totalDensity()}")

    if (cli.stop) readLine()

    val samples = ArrayList<Double>()
    val sampleY = shearWaveMaxVelocityY(cli.ly.toDouble(), kNum)

    val xSamples = ArrayList<DoubleArray>()
    val sampleX = 30

    var time = 0
    samples.add(sampleAverageXSpeed(lattice, sampleY))
    xSamples.add(sampleXSpeed(lattice, sampleX))
    while (time++ < cli.time) {
        lattice.stream()
        if (cli.noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.bulkCollideParallel(0, cli.lx - 1, 0, cli.ly - 1)
        samples.add(sampleAverageXSpeed(lattice, sampleY))
        if (time % 10 == 0)
            xSamples.add(sampleXSpeed(lattice, sampleX))
        visualize()
        //printLine("Min density: ${lattice.minDensity()}")
        //printLine("Max density: ${lattice.maxDensity()}")
        printLine("Min velocity norm: ${lattice.minVelocityNorm().format()}")
        printLine("Max velocity norm: ${lattice.maxVelocityNorm().format()}")
        //printLine("Total density: ${lattice.totalDensity()}")
    }
    time--

    print("Executed $time LBM steps.")

    val dateFormat = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss.S")

    samples.toFile("${cli.lx}x${cli.ly}_${sampleY}_Y_k${kNum}_${cli.omega}_omega_${cli.time}_iterations_${dateFormat.format(Date())}.txt")
    xSamples.toDoubleArrayFile("v_x_of_y_a0_${shearA0Max}_${cli.lx}x${cli.ly}_${sampleX}_X_k${kNum}_${cli.omega}_omega_${cli.time}_iterations_${dateFormat.format(Date())}.txt")
}


