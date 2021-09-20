package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.EasyGolfLocation
import io.realm.RealmObject

open class EasyGolfLocationEntity : RealmObject() {
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var roundId:String = ""
    companion object{
        fun fromEasyGolfLocation(roundId:String,easyGolfLocation: EasyGolfLocation?):EasyGolfLocationEntity?{
            return easyGolfLocation?.let {
                val result = EasyGolfLocationEntity()
                result.roundId = roundId
                result.latitude = it.latitude
                result.longitude = it.longitude
                result
            }
        }
    }

    fun toEasyGolfLocation() = EasyGolfLocation(this.latitude,this.longitude)
}