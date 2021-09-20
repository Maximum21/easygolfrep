package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Scorecard

data class PlayGolfBundle(
        /**
         *
         * @see TypeGame
         */
        var typeGame: TypeGame,
        val roundId:String?,
        val courseId:String?,
        var teeType:String,
        var scorecards:String?,
        var members:String? = null
){
    enum class TypeGame(val value:Int){
        EXPLORE(0),
        NEW_GAME(1),
        BATTLE_GAME(2),
        UNKNOWN(3)
    }
    companion object{
        fun toMembers(data:List<User>?):String?{
            return data?.let { clearData->
                Gson().toJson(clearData)
            }
        }

        fun toScorecard(data:List<Scorecard>?):String?{
            return data?.let { clearData->
                Gson().toJson(clearData)
            }
        }
    }

    fun clearMember(){
        members = null
    }

    fun getMembers(): List<User>?{
        return members?.let { dataJson->
            try {
                Gson().fromJson(dataJson,Array<User>::class.java).asList()
            }catch (e:Exception){
                e.printStackTrace()
                null
            }
        }
    }
}