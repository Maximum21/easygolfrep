package com.minhhop.easygolf.framework.models.entity

import com.google.gson.annotations.SerializedName
import com.minhhop.easygolf.framework.models.common.DataPlayGolf
import com.minhhop.easygolf.framework.models.RoundMatch
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataRoundGolfEntity : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    var id = "roundId"

    @SerializedName("roundMath")
    var roundMatch: RoundMatch? = null

    @SerializedName("scores")
    var scores:RealmList<DataPlayGolf> = RealmList()

    @SerializedName("nameTee")
    var nameTeeDefault = "BLUE"

    @SerializedName("indexCurrentHole")
    var indexCurrentHole = 0
}