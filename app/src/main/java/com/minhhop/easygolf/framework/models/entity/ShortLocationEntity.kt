package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.EasyGolfLocation
import com.minhhop.core.domain.golf.ShortLocation
import io.realm.RealmObject

open class ShortLocationEntity : RealmObject(){
    var lat:Double = 0.0
    var lon:Double = 0.0

    companion object{
        fun fromShortLocation(shortLocation: ShortLocation?):ShortLocationEntity?{
            return shortLocation?.let {
                val result = ShortLocationEntity()
                result.lat = it.lat
                result.lon = it.lon
                result
            }
        }
    }

    fun toShortLocation() = ShortLocation(this.lat,this.lon)
}