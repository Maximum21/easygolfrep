package com.minhhop.easygolf.services

import com.google.android.gms.maps.model.LatLng
import java.lang.Math.*


interface LatLngInterpolator {
    fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng

    class Linear : LatLngInterpolator {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            val lng = (b.longitude - a.longitude) * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }

    class LinearFixed : LatLngInterpolator {
        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            val lat = (b.latitude - a.latitude) * fraction + a.latitude
            var lngDelta = b.longitude - a.longitude

            // Take the shortest path across the 180th meridian.
            if (Math.abs(lngDelta) > 180) {
                lngDelta -= Math.signum(lngDelta) * 360
            }
            val lng = lngDelta * fraction + a.longitude
            return LatLng(lat, lng)
        }
    }

    class Spherical : LatLngInterpolator {

        override fun interpolate(fraction: Float, a: LatLng, b: LatLng): LatLng {
            // http://en.wikipedia.org/wiki/Slerp
            val fromLat = toRadians(a.latitude)
            val fromLng = toRadians(a.longitude)
            val toLat = toRadians(b.latitude)
            val toLng = toRadians(b.longitude)
            val cosFromLat = cos(fromLat)
            val cosToLat = cos(toLat)

            // Computes Spherical interpolation coefficients.
            val angle = computeAngleBetween(fromLat, fromLng, toLat, toLng)
            val sinAngle = sin(angle)
            if (sinAngle < 1E-6) {
                return a
            }
            val aHolder = sin((1 - fraction) * angle) / sinAngle
            val bHolder = sin(fraction * angle) / sinAngle

            // Converts from polar to vector and interpolate.
            val x = aHolder * cosFromLat * cos(fromLng) + bHolder * cosToLat * cos(toLng)
            val y = aHolder * cosFromLat * sin(fromLng) + bHolder * cosToLat * sin(toLng)
            val z = aHolder * sin(fromLat) + bHolder * sin(toLat)

            // Converts interpolated vector back to polar.
            val lat = atan2(z, sqrt(x * x + y * y))
            val lng = atan2(y, x)
            return LatLng(toDegrees(lat), toDegrees(lng))
        }

        private fun computeAngleBetween(fromLat: Double, fromLng: Double, toLat: Double, toLng: Double): Double {
            val dLat = fromLat - toLat
            val dLng = fromLng - toLng
            return 2 * asin(sqrt(pow(sin(dLat / 2), 2.0) + cos(fromLat) * cos(toLat) * pow(sin(dLng / 2), 2.0)))
        }
    }
}