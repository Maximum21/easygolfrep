package com.minhhop.easygolf.presentation.golf.play

import com.google.android.gms.maps.model.LatLng

interface GoogleMapBehavior{
    /**
     * camera map change
     * */
    fun onCameraMoveListener(teePosition:LatLng, holderPosition:LatLng,flagPosition:LatLng,
                             easyGolfMapFragment: EasyGolfMapFragment,zoomState:EasyGolfMapFragment.ZoomStateGreen)
    fun onChangeDistance(teePosition:LatLng, holderPosition:LatLng,flagPosition:LatLng,
                         easyGolfMapFragment: EasyGolfMapFragment,zoomState:EasyGolfMapFragment.ZoomStateGreen)
    /**
     * zoom hole
     * */
    fun onFinishZoomHole(teePosition:LatLng, holderPosition:LatLng,flagPosition:LatLng,
                         easyGolfMapFragment: EasyGolfMapFragment,zoomState:EasyGolfMapFragment.ZoomStateGreen)
}