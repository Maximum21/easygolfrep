package com.minhhop.easygolf.framework.common

import android.view.animation.Interpolator
import kotlin.math.cos
import kotlin.math.pow

class EasyGolfBounceInterpolator(private val amplitude: Double,private val frequency: Double) : Interpolator {

    override fun getInterpolation(input: Float): Float {
        return ((- 1 * Math.E.pow(-input / amplitude)) * cos(frequency * input) + 1).toFloat()
    }
}