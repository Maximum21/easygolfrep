package com.minhhop.core.domain.golf

data class EasyGolfLocation(
        var latitude:Double,
        var longitude:Double
){
    companion object{
        fun instance() = EasyGolfLocation(0.0,0.0)
    }

    fun set(latitude:Double,longitude:Double){
        this.latitude = latitude
        this.longitude = longitude
    }

    fun isEmpty() = latitude == 0.0 && longitude == 0.0
}