package com.alekseyzhelo.lbm.util

import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

/**
 * @author Aleks on 19-06-2016.
 */

// TODO: fix JVM signature and use the same name
fun ArrayList<DoubleArray>.toDoubleArrayFile(filename: String) {
    val path = Paths.get(filename)
    Files.newOutputStream(path).bufferedWriter().use {
        for (a in this) {
            for (d in a) {
                it.write("$d ")
            }
            it.newLine()
        }
    }
}

fun ArrayList<Double>.toFile(filename: String) {
    val path = Paths.get(filename)
    Files.newOutputStream(path).bufferedWriter().use {
        for (d in this) {
            it.write("$d ")
        }
    }
}
