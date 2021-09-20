package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CourseClub {

    @Expose
    @SerializedName("id")
    lateinit var id:String

    @Expose
    @SerializedName("name")
    lateinit var name:String

    @Expose
    @SerializedName("scorecard")
    lateinit var scorecardClubs:ArrayList<ScorecardClub>
}