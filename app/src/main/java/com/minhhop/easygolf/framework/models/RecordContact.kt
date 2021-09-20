package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RecordContact : RealmObject(){
    @PrimaryKey
    private var _id = "easygolf.record"
    @SerializedName("last_time_tamp")
    var lastTimeUpdate:String = ""
}