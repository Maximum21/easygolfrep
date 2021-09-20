package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.Green
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class GreenEntity : RealmObject() {
    @PrimaryKey
    lateinit var id:String
    var red: EasyGolfLocationEntity? = null
    var white: EasyGolfLocationEntity? = null
    var blue: EasyGolfLocationEntity? = null
    var coordinates:RealmList<EasyGolfLocationEntity>? = null
    var roundId:String = ""
    companion object{
        fun fromGreen(roundId:String,green: Green?): GreenEntity?{
            return green?.let {
                val result = GreenEntity()
                result.roundId = roundId
                result.id = green.id
                result.red = EasyGolfLocationEntity.fromEasyGolfLocation(roundId,green.red)
                result.white = EasyGolfLocationEntity.fromEasyGolfLocation(roundId,green.white)
                result.blue = EasyGolfLocationEntity.fromEasyGolfLocation(roundId,green.blue)
                val coordinatesRealmList = RealmList<EasyGolfLocationEntity>()
                it.coordinates?.map {coordinate->
                    coordinatesRealmList.add(EasyGolfLocationEntity.fromEasyGolfLocation(roundId,coordinate))
                }
                result.coordinates = coordinatesRealmList

                result
            }
        }
    }

    fun toGreen() = Green(
            this.id,
            this.red?.toEasyGolfLocation(),
            this.white?.toEasyGolfLocation(),
            this.blue?.toEasyGolfLocation(),
            this.coordinates?.map {
                it.toEasyGolfLocation()
            }
    )
}