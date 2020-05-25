package com.alekseyzhelo.lbm.gui.lwjgl

import com.alekseyzhelo.lbm.boundary.BoundaryType
import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.cli.collectArguments
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.render.GL30Renderer
import com.alekseyzhelo.lbm.statistics.LatticeStatistics
import com.alekseyzhelo.lbm.util.lattice.createBoundaries
import com.alekseyzhelo.lbm.util.lattice.setupLattice
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.timing.printExecutionTime

/**
 * @author Aleks on 18-05-2016.
 */

fun main(args: Array<String>) {
    val cli = CLISettings()
    val cm = CMSettings()
    collectArguments("LWJGL_LBM.jar", arrayOf(cli, cm), args)

//    val boundaries = createBoundaries(
//            BoundaryType.NO_SLIP, // left
//            BoundaryType.NO_SLIP, // top
//            BoundaryType.NO_SLIP, // right
//            BoundaryType.NO_SLIP, // bottom // ZHOU_HE_UX does not work :((((
//            tParam = Pair(-0.0, doubleArrayOf(0.50, 0.0)), //0.61, //0.01,
//            bParam = Pair(-0.0, doubleArrayOf(0.10, 0.0))
//    )

    val inletUX = 0.10
    val density = 1.0
    val boundaries = createBoundaries(
        BoundaryType.INLET, // left
        BoundaryType.NO_SLIP, // top
        BoundaryType.OUTLET, // right
        BoundaryType.NO_SLIP, // bottom // ZHOU_HE_UX does not work :((((
        lParam = Pair(density, doubleArrayOf(inletUX, 0.0)), //0.61, //0.01,
        tParam = Pair(density, doubleArrayOf(-0.1, 0.0)),
        rParam = Pair(inletUX, doubleArrayOf(-0.0, -0.0)),
        bParam = Pair(density, doubleArrayOf(-0.1, 0.0))
    )
    val force = 0.00001
    val lattice = setupLattice(
        cli,
        boundaries
    ) //, ConstantXForce_BGK_D2Q9(cli.omega, force), boundaries)
    //lattice.iniEquilibrium(multiplePressureWaveRho(cli.lx, cli.ly, 4, 4, 4.5), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(squareEmptyRho(cli.lx, cli.ly, 4, 4, 0.1), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(columnPressureWaveRho(cli.lx, cli.ly, 10, 1.5), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(rowPressureWaveRho(cli.lx, cli.ly, 100, 1.05), doubleArrayOf(0.0, 0.0))
    //lattice.iniEquilibrium(density, doubleArrayOf(0.0, 0.0))
    lattice.iniEquilibrium(density, doubleArrayOf(inletUX, 0.0))

    LatticeStatistics.init(lattice)

    println("Min density: ${LatticeStatistics.minDensity}")
    println("Max density: ${LatticeStatistics.maxDensity}")
    println("Max velocity norm: ${LatticeStatistics.maxVelocity}")

    val printLine = { x: Any -> if (cli.verbose) println(x) }
    val renderer = GL30Renderer(cli, cm, lattice, 512, 512)
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
//        if(time == 5000) {
//            val field = sampleVectorField(lattice)
//            field.toDoubleArrFile("vectorField5000_works.txt")
//            break
//        }
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

