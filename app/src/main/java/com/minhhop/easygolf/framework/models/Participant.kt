package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Participant (
        @Expose
        @SerializedName("owner")
        val owner:Boolean,
        @Expose
        @SerializedName("last_updated")
        val lastUpdated:String,
        @Expose
        @SerializedName("stage")
        val stage:String,
        @Expose
        @SerializedName("date_created")
        val dateCreated:String,
        @Expose
        @SerializedName("tournament")
        val tournament: Tournament
)