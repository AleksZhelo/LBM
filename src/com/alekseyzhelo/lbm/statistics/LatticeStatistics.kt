package com.alekseyzhelo.lbm.statistics

import com.alekseyzhelo.lbm.util.norm

/**
 * @author Aleks on 03-07-2016.
 */

object LatticeStatistics {

    private var maxVelocity: Double = 0.0

    fun gatherMaxVel(U: DoubleArray) {
        val norm = norm(U)
        if (norm > maxVelocity) {
            maxVelocity = norm
        }
    }

    fun getMaxVel(): Double {
        return maxVelocity
    }

    fun reset() {
        maxVelocity = 0.0
    }

}