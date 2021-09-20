package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Friend (
        @SerializedName("user_id")
        @Expose
        val userId: String,


        @SerializedName("matches")
        @Expose
        val matches: List<Match>
)