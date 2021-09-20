package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.golf.Scorecard
import io.realm.RealmObject

open class ScorecardEntity : RealmObject() {
    var courseId:String = ""
    var par:Int = 0
    var yard:Double = 0.0
    var distance:Double = 0.0
    var type:String = ""
    var cr:Double? = 0.0
    var sr:Double? = 0.0

    companion object{
        fun fromScorecard(courseId:String,scorecard: Scorecard):ScorecardEntity{
            val result = ScorecardEntity()
            result.courseId = courseId
            result.par = scorecard.par
            result.yard = scorecard.yard
            result.distance = scorecard.distance
            result.type = scorecard.type
            result.cr = scorecard.cr
            result.sr = scorecard.sr
            return result
        }
    }

    fun toScorecard() = Scorecard(
            this.par,
            this.yard,
            this.distance,
            this.type,
            this.cr,
            this.sr
    )
}