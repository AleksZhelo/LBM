package com.alekseyzhelo.lbm.util.timing

import java.text.DecimalFormat

internal val formatter = DecimalFormat("#0.00000");

fun printExecutionTime(end: Long, start: Long) {
    println("Execution time: ${formatter.format((end - start) / 1000.0)} seconds");
}