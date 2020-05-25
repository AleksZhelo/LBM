package com.alekseyzhelo.lbm.gui.lwjgl

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.core.lattice.MaterialsLatticeD2Q9
import com.alekseyzhelo.lbm.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.render.GL30Renderer
import com.alekseyzhelo.lbm.gui.lwjgl.util.ResourcesUtil
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.MaterialUtil
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.sampling.sampleVectorField
import com.alekseyzhelo.lbm.util.sampling.toDoubleArrFile
import com.alekseyzhelo.lbm.util.timing.printExecutionTime

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = CLISettings()
    val cm = CMSettings()
    collectArguments("LWJGL_LBM.jar", arrayOf(cli, cm), args)

    val inletUX = 0.50
    val density = 1.0
    //val image = ResourcesUtil.loadImageResource("/lattices/oscillator_Jan.bmp")
    val image = ResourcesUtil.loadImageResource("/lattices/oscillator_medium.bmp")
    cli.lx = image.width
    cli.ly = image.height
    val lattice = MaterialsLatticeD2Q9(
            image,
            cli.omega,
            BGKDynamicsD2Q9(cli.omega)
    )
    lattice.iniEquilibrium(density, doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(density, doubleArrayOf(inletUX, 0.0))

    LatticeStatistics.init(lattice)
    MaterialUtil.configure(density, doubleArrayOf(inletUX, 0.0))

    println("Min density: ${LatticeStatistics.minDensity}")
    println("Max density: ${LatticeStatistics.maxDensity}")
    println("Max velocity norm: ${LatticeStatistics.maxVelocity}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }
    val renderer = GL30Renderer(cli, cm, lattice, 800, 600)
    renderer.initialize()

    renderer.frame(lattice.cells)
    printLine("Total density: ${lattice.totalDensity()}")

    if (cli.stop) readLine()

    val noCollisions = cli.noCollisions
    var time = 0
    val start = System.currentTimeMillis()
    while (time++ < cli.time && !renderer.windowShouldClose()) {
        lattice.stream()
        if (noCollisions)
            lattice.swapCellBuffers()
        else
            lattice.bulkCollideParallel(0, cli.lx - 1, 0, cli.ly - 1)
        //if(time > 20000) {
        renderer.frame(lattice.cells)
        LatticeStatistics.reset()
        //}
        if (time % 100 == 0) {
            printLine("time: $time, max velocity: ${lattice.maxVelocityNorm()}")
            printLine("Total density: ${lattice.totalDensity()}")
        }
        if(time % 1000 == 0) {
            val field = sampleVectorField(lattice)
            field.toDoubleArrFile("proper_oscillator_${time}_${cli.omega}_${inletUX}.txt")
            // break
        }
        //printLine("Min density: ${lattice.minDensity()}")
        //printLine("Max density: ${lattice.maxDensity()}")
        //printLine("Min velocity: ${lattice.minVelocityNorm()}")
//        printLine("Max velocity: ${lattice.maxVelocityNorm().format(6)}")
//        printLine("Total density: ${lattice.totalDensity()}")
        //printLine("0,0 density: ${lattice.cells[0][0].computeRho()}")
        //printLine("lx/2,ly/2 density: ${lattice.cells[cli.lx/2][cli.ly/2].computeRho()}")
    }
    time--
    val end = System.currentTimeMillis()

    printExecutionTime(end, start, time)
    println("Executed $time LBM steps.")
    printLine("Total density: ${lattice.totalDensity()}")

    renderer.terminate()
}

