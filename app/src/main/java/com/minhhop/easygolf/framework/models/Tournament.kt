package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Tournament(
        @SerializedName("id")
        @Expose
        val id:Int,

        @SerializedName("name")
        @Expose
        val name:String,

        @SerializedName("photo")
        @Expose
        val photo:String?,

        @SerializedName("schedule_date")
        @Expose
        val scheduleDate:String,

        @SerializedName("participants")
        @Expose
        val participants:List<FriendTournament>,

        @SerializedName("club")
        @Expose
        val club: Club
)