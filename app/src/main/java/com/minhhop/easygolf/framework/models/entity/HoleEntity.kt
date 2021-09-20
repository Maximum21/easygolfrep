package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.Hole
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class HoleEntity : RealmObject(){
    @PrimaryKey
    lateinit var holeId:String
    var name:String? = null
    var latitude:Double = 0.0
    var longitude:Double = 0.0
    var number:Int = 0
    var index:Int = 0
    var par:Int = 0
    var yard:Int = 0
    var tees:RealmList<TeeEntity>? = null
    var green: GreenEntity? = null
    var image:String? = null
    var roundId:String? = null
    companion object{
        fun fromHole(roundId:String,hole: Hole?):HoleEntity?{
            return hole?.let {
                val result = HoleEntity()
                result.roundId = roundId
                result.holeId = hole.hole_id
                result.name = hole.name
                result.latitude = hole.latitude
                result.longitude = hole.longitude
                result.number = hole.number
                result.index = hole.index
                result.par = hole.par
                result.yard = hole.yard

                val teeRealmList = RealmList<TeeEntity>()
                hole.tees?.map {tee->
                    teeRealmList.add(TeeEntity.fromTee(roundId,tee))
                }
                result.tees = teeRealmList

                result.green = GreenEntity.fromGreen(roundId,it.green)
                result
            }
        }
    }

    fun toHole() = Hole(
            this.holeId,
            this.name,
            this.latitude,
            this.longitude,
            this.number,
            this.index,
            this.par,
            this.yard,
            this.tees?.map {
                it.toTee()
            },
            this.green?.toGreen(),
            this.image
    )
}