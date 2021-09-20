package com.minhhop.easygolf.services.firebase

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ObjectUserProfile (
        @Expose
        @SerializedName("id")
        val id:String,
        @Expose
        @SerializedName("first_name")
        var first_name: String,

        @Expose
        @SerializedName("last_name")
        var last_name: String,

        @Expose
        @SerializedName("online")
        var online: Boolean = false,

        @Expose
        @SerializedName("avatar")
        var avatar: String? = null
)