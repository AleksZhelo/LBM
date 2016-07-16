package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2

/**
 * @author Aleks on 29-05-2016.
 */
fun LatticeD2.toDensityTable(mainF: Boolean): String {
    val computeRho = if (mainF)
        { x: CellD2Q9 -> x.computeRho() }
    else
        { x: CellD2Q9 -> x.computeBufferRho() }

    return buildString {
        for (j in cells[0].size - 1 downTo 0) {
            for (i in cells.indices) {
                append("${computeRho(cells[i][j])} ")
            }
            appendln()
        }
    }.replace("0.0", "_._")
}

fun LatticeD2.toVelocityTable(mainF: Boolean): String {
    val computeRho = if (mainF)
        { x: CellD2Q9 -> x.computeRho() }
    else
        { x: CellD2Q9 -> x.computeBufferRho() }

    val computeU: (x: CellD2Q9, rho: Double) -> DoubleArray = if (mainF)
        { x, Rho -> x.computeU(Rho) }
    else
        { x, Rho -> x.computeBufferU(Rho) }

    val computeRhoU = if (mainF)
        { x: CellD2Q9 -> x.computeRhoU() }
    else
        { x: CellD2Q9 -> x.computeBufferRhoU() }

    return buildString {
        for (j in cells[0].size - 1 downTo 0) {
            for (i in cells.indices) {
                append("${printCellVelocity(computeRhoU(cells[i][j]))}|")
            }
            appendln()
        }
    }
}

fun printCellPressure(Rho: Double): String {
    return "${Rho.format()}"
}

fun printCellVelocity(U: DoubleArray): String {
    return "${U[0].format()} ${U[1].format()}"
}

fun printCellVelocityNorm(U: DoubleArray): String {
    val norm = norm(U)
    return "${norm.format()}"
}