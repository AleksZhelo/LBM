package com.alekseyzhelo.lbm.statistics

import com.alekseyzhelo.lbm.core.lattice.LatticeD2
import com.alekseyzhelo.lbm.util.maxDensity
import com.alekseyzhelo.lbm.util.maxVelocityNorm
import com.alekseyzhelo.lbm.util.minDensity
import com.alekseyzhelo.lbm.util.norm

/**
 * @author Aleks on 03-07-2016.
 */

// TODO: currently works as a side effect of computeU and computeRhoU. Fix
object LatticeStatistics {

    var minDensity: Double = Double.MAX_VALUE
    var maxDensity: Double = 0.0
    var maxVelocity: Double = 0.0

    private var gatherDensity = false
    private var gatherVelocity = false

    fun init(lattice: LatticeD2){
        maxVelocity = lattice.maxVelocityNorm()
        minDensity = lattice.minDensity()
        maxDensity = lattice.maxDensity()
    }

    fun configure(gatherDensity: Boolean, gatherVelocity: Boolean) {
        this.gatherDensity = gatherDensity
        this.gatherVelocity = gatherVelocity
    }

    fun gatherMinMaxDensity(rho: Double) {
        if(gatherDensity) {
            if (rho > maxDensity) {
                maxDensity = rho
            } else if (rho < minDensity) {
                minDensity = rho
            }
        }
    }

    fun gatherMaxVelocity(U: DoubleArray) {
        if(gatherVelocity) {
            val norm = norm(U)
            if (norm > maxVelocity) {
                maxVelocity = norm
            }
        }
    }

    fun reset() {
        if(gatherDensity) {
            minDensity = Double.MAX_VALUE
            maxDensity = 0.0
        }
        if(gatherVelocity) {
            maxVelocity = 0.0
        }
    }

}