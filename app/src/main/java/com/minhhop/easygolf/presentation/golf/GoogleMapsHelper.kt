package com.minhhop.easygolf.presentation.golf

import android.animation.ValueAnimator
import android.location.Location
import android.view.animation.LinearInterpolator
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.SphericalUtil
import com.minhhop.easygolf.presentation.golf.component.marker.EventAnimationMarker
import com.minhhop.easygolf.services.LatLngInterpolator
import com.minhhop.easygolf.utils.AppUtil
import kotlin.math.*


object GoogleMapsHelper {
    enum class UnitGolf{
        METER,
        YARD
    }
    //Meter
//    const val EARTH_RADIUS = 6378100.0
    private const val UNIT_PIXEL_METER = 156543.03392

    private const val UNIT_YARD = 1.0936133

    fun getDistanceLatLng(pointA: LatLng?, pointB: LatLng?, unit:UnitGolf = UnitGolf.YARD): Double {
        return if(pointA == null || pointB == null){
            -1.0
        }else {
            val holder = SphericalUtil.computeDistanceBetween(pointA, pointB)
            convertMeterYard(unit,holder)
        }
    }

    /**
     * @param @value is unit meter
     * */
    fun convertMeterYard( unit:UnitGolf, value:Double):Double{
        return if (unit == UnitGolf.YARD) value * UNIT_YARD else value
    }

    /**
     * @param @value is unit yard
     * */
    fun convertYardMeter( unit:UnitGolf, value:Double):Double{
        return if (unit == UnitGolf.YARD) value  else value / UNIT_YARD
    }

    fun getCenterPointFromList(points: List<LatLng>): LatLng? {
        return if(points.isNotEmpty()) {
            val builder = LatLngBounds.Builder()
            for (element in points) {
                builder.include(element)
            }
            val bounds: LatLngBounds = builder.build()
            bounds.center
        }else null
    }

    fun computeCentroid(pointA: LatLng, pointB: LatLng): LatLng {
        return SphericalUtil.interpolate(pointA, pointB, 0.5)
    }

    fun getBearingFromLocation(lat1: LatLng, lat2: LatLng): Float {
        val startingLocation = Location("starting point")
        startingLocation.latitude = lat1.latitude
        startingLocation.longitude = lat1.longitude

        //Get the target location
        val endingLocation = Location("ending point")
        endingLocation.latitude = lat2.latitude
        endingLocation.longitude = lat2.longitude

        //Find the Bearing from current location to next location
        return startingLocation.bearingTo(endingLocation)
    }

    fun getDestinationPoint(source: LatLng, bearing: Double, dist: Double): LatLng? {
        val earthRadiusMeter = AppUtil.EARTH_RADIUS / 1000
        val distResult = dist / earthRadiusMeter
        val bearingResult = Math.toRadians(bearing)

        val lat1 = Math.toRadians(source.latitude)
        val long1 = Math.toRadians(source.longitude)
        val lat2 = asin(sin(lat1) * cos(distResult) +
                cos(lat1) * sin(distResult) * cos(bearingResult))

        val lon2 = long1 + atan2(sin(bearingResult) * sin(distResult) *
                cos(lat1),
                cos(distResult) - sin(lat1) *
                        sin(lat2))
        return if (lat2.isNaN() || lon2.isNaN()) {
            null
        } else LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
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

    fun getPerPixelToMeter(position:LatLng,map: GoogleMap):Double = (UNIT_PIXEL_METER * cos(position.latitude * Math.PI / 180) / 2.0.pow(map.cameraPosition.zoom.toDouble()))
}