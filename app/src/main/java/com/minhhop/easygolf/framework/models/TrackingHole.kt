package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class TrackingHole : RealmObject() {
    @PrimaryKey
    @SerializedName("club_id")
    lateinit var clubId:String
    @SerializedName("number_hole")
    var numberHole:Int = 0
}