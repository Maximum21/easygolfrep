package com.minhhop.core.domain

import java.util.*

data class EasyGolfApp(
        val roundId:String? = null,
        val timeToCheckExitRound:Long? = null,
        var isOnFirebase:Boolean = false
){
    var id:String = ""
    fun isEmpty():Boolean{
        return roundId.isNullOrEmpty() && timeToCheckExitRound == null
    }
    companion object{
        fun buildNewTime():Long{
            val timeNeedUpdate = Calendar.getInstance()
            timeNeedUpdate.add(Calendar.SECOND, 30)//TODO change to oke
            return timeNeedUpdate.timeInMillis
        }
    }
}