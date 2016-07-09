package com.alekseyzhelo.lbm.util

import com.alekseyzhelo.lbm.core.lattice.DescriptorD2Q9
import java.text.DecimalFormat
import java.util.*

/**
 * @author Aleks on 28-05-2016.
 */

@Suppress("NOTHING_TO_INLINE") // TODO: investigate
inline fun normalize(value: Double, minValue: Double, maxValue: Double): Double {
    return (value - minValue) / (maxValue - minValue)
}

val opposite = arrayListOf(0, 3, 4, 1, 2, 7, 8, 5, 6)

fun norm(U: DoubleArray): Double {
    var norm = 0.0
    for (i in U.indices) {
        norm += U[i] * U[i]
    }
    return Math.sqrt(norm)
}

fun normSquare(U: DoubleArray): Double {
    var uSqr = 0.0
    for (i in U.indices) {
        uSqr += U[i] * U[i]
    }
    return uSqr
}

fun scalarProduct(U: DoubleArray, V: DoubleArray): Double {
    var prod = 0.0
    for (i in U.indices) {
        prod += U[i] * V[i]
    }
    return prod
}

fun normalize(U: DoubleArray): DoubleArray {
    val norm = norm(U)
    val ortVector = Arrays.copyOf(U, U.size)
    for (i in ortVector.indices) {
        ortVector[i] /= norm
    }
    return ortVector
}

private val doubleFormat = DecimalFormat("+0.000;-0.000")
fun Double.format() = doubleFormat.format(this)
fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)