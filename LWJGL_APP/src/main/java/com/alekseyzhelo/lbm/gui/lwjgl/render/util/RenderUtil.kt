package com.alekseyzhelo.lbm.gui.lwjgl.render.util

import com.alekseyzhelo.lbm.gui.lwjgl.color.FloatColor

private fun binarySearch(a: FloatArray, key: Float): Int {
    var low = 0
    var high = a.size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midVal = a[mid]

        if (midVal < key)
            low = mid + 1  // Neither val is NaN, thisVal is smaller
        else if (midVal > key)
            high = mid - 1 // Neither val is NaN, thisVal is larger
        else {
            val midBits = java.lang.Float.floatToIntBits(midVal)
            val keyBits = java.lang.Float.floatToIntBits(key)
            if (midBits == keyBits)
            // Values are equal
                return if (mid > 0) mid - 1 else mid // Key found
            else if (midBits < keyBits)
            // (-0.0, 0.0) or (!NaN, NaN)
                low = mid + 1
            else
            // (0.0, -0.0) or (NaN, !NaN)
                high = mid - 1
        }
    }
    return low - 1 // key not found.
}

private val x = floatArrayOf(0.0f, 0.15f, 0.4f, 0.5f, 0.65f, 0.8f, 1.0f)
private val r = floatArrayOf(0.0f, 0.0f, 0.0f, 0.56470588f, 1.0f, 1.0f, 0.54509804f)
private val g = floatArrayOf(0.0f, 0.0f, 1.0f, 0.93333333f, 1.0f, 0.0f, 0.0f)
private val b = floatArrayOf(0.54509804f, 1.0f, 1.0f, 0.56470588f, 0.0f, 0.0f, 0.0f)

// TODO: extract to a color ramp colormap
internal fun cellFColor(normalized: Float): FloatColor {
    val low = binarySearch(x, normalized)
    val high = low + 1
    if (normalized.isNaN()) {
        return FloatColor(0.0f, 0.0f, 0.0f)
    }
    //try {
    val p = (normalized - x[low]) / (x[high] - x[low])
    val oneMinP = 1.0f - p
    return FloatColor(oneMinP * r[low] + p * r[high], oneMinP * g[low] + p * g[high], oneMinP * b[low] + p * b[high])
//    }catch(e: ArrayIndexOutOfBoundsException) {
//        println("low: $low, high: $high, val: $normalized")
//        System.exit(0)
//        return FColor(0.0f, 0.0f, 0.0f)
//    }
}