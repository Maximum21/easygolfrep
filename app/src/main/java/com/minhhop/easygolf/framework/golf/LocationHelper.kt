package com.minhhop.easygolf.framework.golf

import android.Manifest
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation

class LocationHelper {
    companion object {
        private const val UPDATE_INTERVAL: Long = 1000
        private const val FASTEST_INTERVAL: Long = 1000
    }

    //    Service to get location
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null

    //    To get address
    private var mLocationCallback: LocationCallback? = null
    private var mOnLocationCatch: OnLocationCatch? = null
    private var mLocationRequest: LocationRequest? = null

    fun startLocationUpdatesForActivity(activity: EasyGolfActivity<*>, updatePosition: Boolean, onLocationCatch: OnLocationCatch?){
        if(mFusedLocationProviderClient == null) {
            startLocationUpdates(updatePosition, onLocationCatch)
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        }
        updateRequest(activity)
    }

    fun startLocationUpdatesForFragment(fragment: EasyGolfFragment<*>, updatePosition: Boolean, onLocationCatch: OnLocationCatch?){
        if(mFusedLocationProviderClient == null) {
            startLocationUpdates(updatePosition, onLocationCatch)
            fragment.context?.let { context ->
                mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
            }
        }
        updateRequest(fragment)
    }
    private fun startLocationUpdates(updatePosition: Boolean, onLocationCatch: OnLocationCatch?) {
        this.mOnLocationCatch = onLocationCatch
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (mFusedLocationProviderClient != null && !updatePosition) {
                    mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
                }
                val currentLocation = locationResult.locations[0]
                if (currentLocation != null) {
                    mOnLocationCatch?.onCatch(LatLng(currentLocation.latitude,
                            currentLocation.longitude), currentLocation.bearing)
                }
            }

            override fun onLocationAvailability(locationAvailability: LocationAvailability) {
                if (!locationAvailability.isLocationAvailable) {
                    mOnLocationCatch?.locationNotAvailable()
                }
            }
        }

        mLocationRequest = LocationRequest()
        mLocationRequest?.interval = FASTEST_INTERVAL
        mLocationRequest?.fastestInterval = UPDATE_INTERVAL
        mLocationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

    }

    fun removeLocationUpdates() {
        mFusedLocationProviderClient?.removeLocationUpdates(mLocationCallback)
    }

    fun updateRequest(activity: EasyGolfActivity<*>) {
        mFusedLocationProviderClient?.let { fusedLocationProviderClient ->
            mLocationCallback?.let { locationCallback ->
                EasyGolfNavigation.checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Contains.REQUEST_LOCATION_PERMISSION,
                        activity,
                        {
                            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper())
                        },{
                            mOnLocationCatch?.onPermissionDenied()
                        }
                )
            }
        }
    }

    private fun updateRequest(fragment: EasyGolfFragment<*>) {
        mFusedLocationProviderClient?.let { fusedLocationProviderClient ->
            mLocationCallback?.let { locationCallback ->
                EasyGolfNavigation.checkSelfPermission(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Contains.REQUEST_LOCATION_PERMISSION,
                        fragment,
                        {
                            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, locationCallback, Looper.getMainLooper())
                        },{
                    mOnLocationCatch?.onPermissionDenied()
                }
                )
            }
        }
    }

    interface OnLocationCatch {
        fun onCatch(mLatLng: LatLng?, bearing: Float)
        fun locationNotAvailable()
        fun onPermissionDenied()
    }
}