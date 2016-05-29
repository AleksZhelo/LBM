package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.cell.CellD2Q9
import com.alekseyzhelo.lbm.core.lattice.LatticeD2Q9

/**
 * @author Aleks on 29-05-2016.
 */
fun LatticeD2Q9.toDensityTable(mainF: Boolean): String {
    val computeRho = if (mainF)
        { x: CellD2Q9 -> x.computeRho(x.f) }
    else
        { x: CellD2Q9 -> x.computeRho(x.fBuf) }

    return buildString {
        for (j in cells[0].size - 1 downTo 0) {
            for (i in cells.indices) {
                append("${computeRho(cells[i][j])} ")
            }
            appendln()
        }
    }.replace("0.0", "_._")
}

fun LatticeD2Q9.toVelocityTable(mainF: Boolean): String {
    val computeRho = if (mainF)
        { x: CellD2Q9 -> x.computeRho(x.f) }
    else
        { x: CellD2Q9 -> x.computeRho(x.fBuf) }

    val computeU: (x: CellD2Q9, Rho: Double) -> DoubleArray = if (mainF)
        { x, Rho -> x.computeU(Rho, x.f) }
    else
        { x, Rho -> x.computeU(Rho, x.fBuf) }

    return buildString {
        for (j in cells[0].size - 1 downTo 0) {
            for (i in cells.indices) {
                append("${printCellVelocity(cells[i][j].computeRhoU())}|")
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