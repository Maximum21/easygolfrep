package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VerifyCodeRequest(
        @SerializedName("token")
        @Expose
        private val token: String,

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