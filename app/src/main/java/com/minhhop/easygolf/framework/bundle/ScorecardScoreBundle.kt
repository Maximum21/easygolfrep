package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.golf.Scorecard

data class ScorecardScoreBundle(
        val data:String?
) {
    companion object{
        fun toData(scorecard:List<Scorecard>?):String?{
            return scorecard?.let { clearData->
                Gson().toJson(clearData)
            }
        }
    }

    fun getResult() = fetchData(data)
    private fun fetchData(data: String?): List<Scorecard>?{
        return data?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Array<Scorecard>::class.java).asList()
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}