package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.DataScoreGolf
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class DataScoreGolfEntity : RealmObject(){
    @PrimaryKey
    var id:String = ""
    var score:Int? = null
    var fairwayHit:Int?= null
    var greenInRegulation:Int?= null
    var putts:Int?= null
    var roundId:String? = null
    var number:Int= 0
    var holeId:String? = null
    var teeId:String? = null
    companion object{
        fun fromDataScoreGolf(dataScoreGolf: DataScoreGolf):DataScoreGolfEntity{
            val result = DataScoreGolfEntity()
            result.id = dataScoreGolf.id?:Calendar.getInstance().timeInMillis.toString()
            result.score = dataScoreGolf.score
            result.fairwayHit = dataScoreGolf.fairwayHit
            result.greenInRegulation = dataScoreGolf.greenInRegulation
            result.putts = dataScoreGolf.putts
            result.roundId = dataScoreGolf.roundId
            result.number = dataScoreGolf.number?:1
            return result
        }
    }

    fun toDataScoreGolf() = DataScoreGolf(
            this.id,
            this.score,
            this.fairwayHit,
            this.greenInRegulation,
            this.putts,
            this.roundId,
            this.number
    )
}