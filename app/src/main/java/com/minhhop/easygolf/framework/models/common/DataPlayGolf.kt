package com.minhhop.easygolf.framework.models.common

import com.google.gson.annotations.SerializedName
import com.minhhop.easygolf.utils.AppUtil
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class DataPlayGolf : RealmObject() {

    @PrimaryKey
    @SerializedName("id")
    var id = ""

    @SerializedName("score")
    var mValueScore = 0

    @SerializedName("fairwayHit")
    var mValueFairwayHit = -1

    @SerializedName("greenInRegulation")
    var mValueGreenInRegulation = -1

    @SerializedName("putt")
    var mValuePutt = -1

    @SerializedName("resId")
    var resId = 0

    @SerializedName("resIdWhite")
    var resIdWhite = 0

    @SerializedName("numberHole")
    var numberHole = 0

    @SerializedName("hole_id")
    var holeId = ""

    var teeId:Int = 0

    var flagLatitude:Double = 0.0

    var flagLongitude:Double = 0.0


    var distance = 0
    var par = 0

    var index = 0

    var idClub = ""

    init {
        id = AppUtil.getRandomString(10)
    }
}