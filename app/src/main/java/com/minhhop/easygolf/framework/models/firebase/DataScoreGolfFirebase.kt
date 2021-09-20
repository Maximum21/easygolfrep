package com.minhhop.easygolf.framework.models.firebase

import com.minhhop.core.domain.golf.DataScoreGolf

data class DataScoreGolfFirebase(
        var score:Int? = null,
        /**
         *@fairway left : 0
         *@fairway center : 1
         *@fairway right : 2
         * */
        var fairway_hit:Int? = null,
        /**
         *@greenInRegulation false : 0
         *@greenInRegulation true: 1
         * */
        var green:Int? = null,
        /**
         *@putt 0...4
         * */
        var putts:Int? = null,
        var tee_id:String? = null
){
    fun toDataScoreGolf() = DataScoreGolf(
            null,
            this.score,
            this.fairway_hit,
            this.green,
            this.putts,
            null,null
    )
    companion object{
        fun fromDataScoreGolf(data: DataScoreGolf):DataScoreGolfFirebase{
            return DataScoreGolfFirebase(
                    data.score,
                    data.fairwayHit,
                    data.greenInRegulation,
                    data.putts
            )
        }
    }
}