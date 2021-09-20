package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SocicalRequest(
        @SerializedName("first_name")
        @Expose
        private val firstName: String,

        @SerializedName("last_name")
        @Expose
        private val lastName: String,

        @SerializedName("brand")
        @Expose
        private val brand: String,

        @SerializedName("device_token")
        @Expose
        private val deviceToken: String,

        @SerializedName("os")
        @Expose
        private val os:String = "ANDROID"
)