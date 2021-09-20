package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.golf.Scorecard

data class EndGameBundle(
        val roundId:String,
        var teeType:String?,
        var scorecards:String?,
        val typeGame:PlayGolfBundle.TypeGame
){
    fun getScorecards(): List<Scorecard>?{
        return scorecards?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Array<Scorecard>::class.java).asList()
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}