package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FriendTournament (
        @SerializedName("id")
        @Expose
        val id:Int,

        @SerializedName("stage")
        @Expose
        val stage:String,

        @SerializedName("avatar")
        @Expose
        val avatar:String?,

        @SerializedName("fullname")
        @Expose
        val fullName:String
)