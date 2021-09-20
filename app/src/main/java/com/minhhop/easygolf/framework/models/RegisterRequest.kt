package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RegisterRequest(
        @SerializedName("first_name")
        @Expose
       val firstName:String,
        @SerializedName("last_name")
        @Expose
        val lastName:String,
        @SerializedName("country_code")
        @Expose
        val countryCode:String,

        @SerializedName("email")
        @Expose
        val email:String,
        @SerializedName("phone_number")
        @Expose
        val phone:String,
        @SerializedName("password")
        @Expose
        val password:String,

        @SerializedName("os")
        @Expose
        private val os:String = "ANDROID"
)