package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.SerializedName

class RoundHost {
    @SerializedName("hole_id")
    var holeId:String = ""
    var flag:Coordinate? = null
    var score:Int = 0
    var putts:Int? = null
    var fairway_hit:Int? = null
    var green_in_regulation:Int? = null
    @SerializedName("tee_id")
    var teeId:Int? = null
    var shots:Int? = null
}