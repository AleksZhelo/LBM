package com.alekseyzhelo.lbm.gui.lwjgl

import com.alekseyzhelo.lbm.cli.LatticeFileSettings
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.core.lattice.MaterialsLatticeD2Q9
import com.alekseyzhelo.lbm.dynamics.BGKDynamicsD2Q9
import com.alekseyzhelo.lbm.functions.columnPressureWaveRho
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.render.GL30Renderer
import com.alekseyzhelo.lbm.gui.lwjgl.render.MaterialGL30Renderer
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.MaterialUtil
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.sampling.sampleVectorField
import com.alekseyzhelo.lbm.util.sampling.toDoubleArrFile
import com.alekseyzhelo.lbm.util.timing.printExecutionTime
import java.io.File
import javax.imageio.ImageIO

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = LatticeFileSettings()
    val cm = CMSettings()
    collectArguments("LWJGL_APP.jar", arrayOf(cli, cm), args)

    val inletUX = 0.50 / 4
    val density = 1.0
    val image = ImageIO.read(File(cli.latticeFile))
    cli.lx = image.width
    cli.ly = image.height
    val lattice = MaterialsLatticeD2Q9(
        image,
        BGKDynamicsD2Q9(cli.omega)
    )
    lattice.iniEquilibrium(density, doubleArrayOf(0.0, 0.0))
//    lattice.iniEquilibrium(columnPressureWaveRho(cli.lx, cli.ly, 400, 5.5), doubleArrayOf(0.0, 0.0))
//    lattice.iniEquilibrium(density, doubleArrayOf(inletUX, 0.0))

    LatticeStatistics.initVerbose(lattice)
    if (cli.noRescale) {
        LatticeStatistics.configure(false, false)
    } else {
        LatticeStatistics.configure(!cli.drawVelocities, cli.drawVelocities)
    }

    MaterialUtil.configure(density, doubleArrayOf(inletUX, 0.0))

    val printLine = { x: Any -> if (cli.verbose) println(x) }
    val renderer = MaterialGL30Renderer(cli, cm, image.width, image.height)
    renderer.initialize()

//    renderer.frame(lattice.cells)
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
        if (time % 1000 == 0) {
            val field = sampleVectorField(lattice)
            field.toDoubleArrFile("proper_oscillator_${time}_${cli.omega}_${inletUX}.txt")
            // break
        }
    }
    time--
    val end = System.currentTimeMillis()

    printExecutionTime(end, start, time)
    println("Executed $time LBM steps.")
    printLine("Total density: ${lattice.totalDensity()}")

    renderer.terminate()
}

