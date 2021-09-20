package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.Tee
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class TeeEntity : RealmObject() {
    @PrimaryKey
    var uuid:String = ""
    var roundId:String = ""
    var id:String = ""
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var type:String = ""
    var yard:Double = 0.0

    companion object{
        fun fromTee(roundId:String,tee:Tee):TeeEntity{
            val teeEntity = TeeEntity()
            teeEntity.uuid = "${tee.id}_${Calendar.getInstance().timeInMillis}"
            teeEntity.roundId = roundId
            teeEntity.id = tee.id
            teeEntity.latitude = tee.latitude
            teeEntity.longitude = tee.longitude
            teeEntity.type = tee.type
            teeEntity.yard = tee.yard
            return teeEntity
        }
    }

    fun toTee():Tee = Tee(
            this.id,
            this.latitude,
            this.longitude,
            this.type,
            this.yard
    )
}