package com.minhhop.easygolf.presentation.golf.component.marker

import android.animation.ValueAnimator
import com.google.android.gms.maps.model.LatLng

interface EventAnimationMarker {
    fun onCallBack(animation: ValueAnimator, pos: LatLng, rotation:Float)
}