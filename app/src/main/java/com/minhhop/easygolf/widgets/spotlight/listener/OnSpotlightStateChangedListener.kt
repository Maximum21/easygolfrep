package com.minhhop.easygolf.widgets.spotlight.listener

interface OnSpotlightStateChangedListener {
    /**
     * Called when SpotLight is started
     */
    fun onStarted()

    /**
     * Called when SpotLight is ended
     */
    fun onEnded()
}