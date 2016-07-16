package com.alekseyzhelo.lbm.util.timing

import java.text.DecimalFormat

internal val formatter = DecimalFormat("#0.00000")

fun printExecutionTime(end: Long, start: Long, time: Int) {
    val seconds = (end - start) / 1000.0
    println("Execution time: ${formatter.format(seconds)} seconds")
    println("Average FPS: ${time / seconds}")
}