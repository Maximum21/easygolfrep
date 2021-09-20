package com.minhhop.easygolf.framework.models.entity

import com.minhhop.core.domain.EasyGolfApp
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*

open class EasyGolfAppEntity : RealmObject() {
    @PrimaryKey
    var id: String = ""
    var roundId: String? = null
    var timeToCheckExitRound: Long? = null
    var updatedAt:Long = 0L
    var isOnFirebase:Boolean = false

    companion object {
        fun fromEasyGolfApp(easyGolfApp: EasyGolfApp): EasyGolfAppEntity {
            val result = EasyGolfAppEntity()
            result.id = Calendar.getInstance().timeInMillis.toString()
            result.roundId = easyGolfApp.roundId
            result.timeToCheckExitRound = easyGolfApp.timeToCheckExitRound
            result.updatedAt = Calendar.getInstance().timeInMillis
            result.isOnFirebase = easyGolfApp.isOnFirebase
            return result
        }
    }

    fun toEasyGolfApp() :EasyGolfApp{
        val result = EasyGolfApp(
                this.roundId,
                this.timeToCheckExitRound,
                this.isOnFirebase
        )
        result.id = this.id
        return result
    }
}