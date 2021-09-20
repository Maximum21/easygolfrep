package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TournamentEntry(
        @SerializedName("name")
        @Expose
        val name:String,

        @SerializedName("photo")
        @Expose
        val photo:String?,

        @SerializedName("schedule_date")
        @Expose
        val scheduleDate:String,

        @SerializedName("friends")
        @Expose
        val friends:List<String>,

        @SerializedName("club_id")
        @Expose
        val clubId: String
)