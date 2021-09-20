package com.minhhop.easygolf.presentation.golf.component.marker

import android.animation.ValueAnimator
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.minhhop.easygolf.services.LatLngInterpolator

class MarkerAnimation {
    companion object{
        private var instance: MarkerAnimation? = null
        fun getInstance(): MarkerAnimation {
            if(instance == null){
                instance = MarkerAnimation()
            }
            return instance!!
        }
    }

    fun animateMarker(destination: LatLng, bearing: Float, marker: Marker?, callback: EventAnimationMarker?,
                      defaultDuration: Long = 5000)
                : ValueAnimator? {
            marker?.apply {

                val valueAnimator = ValueAnimator.ofFloat(0f, 1f)
                val startPosition = position
                val endPosition = LatLng(destination.latitude, destination.longitude)

                val startRotation = rotation

                val latLngInterpolator = LatLngInterpolator.LinearFixed()
                valueAnimator.duration = defaultDuration
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.addUpdateListener { animation ->
                    try {
                        val v = animation.animatedFraction
                        val newPosition = latLngInterpolator.interpolate(v, startPosition, endPosition)
                        callback?.onCallBack(animation, newPosition, computeRotation(v, startRotation, bearing))

                    } catch (ex: Exception) {

                    }
                }
                valueAnimator.start()

                return valueAnimator
            }

            return null
        }

        private fun computeRotation(fraction: Float, start: Float, end: Float): Float {
            val normalizeEnd = end - start // rotate start to 0
            val normalizedEndAbs = (normalizeEnd + 360) % 360

            val direction = (if (normalizedEndAbs > 180) -1 else 1).toFloat() // -1 = anticlockwise, 1 = clockwise
            val rotation: Float
            rotation = if (direction > 0) {
                normalizedEndAbs
            } else {
                normalizedEndAbs - 360
            }

            val result = fraction * rotation + start
            return (result + 360) % 360
        }

}