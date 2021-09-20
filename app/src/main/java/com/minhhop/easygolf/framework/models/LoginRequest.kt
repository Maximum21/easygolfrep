package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class LoginRequest(

        @SerializedName("login")
        @Expose
        private val userName: String,

        @SerializedName("password")
        @Expose
        private val password: String,

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
//    @SerializedName("first_name")
//    @Expose
//    private val firstName: String?
//
//    @SerializedName("last_name")
//    @Expose
//    private val lastName: String?
//
//    @SerializedName("phone_number")
//    @Expose
//    private val phoneNumber: String?
//
//    @SerializedName("country_code")
//    @Expose
//    private val countryCode: String?
//
//    @SerializedName("email")
//    @Expose
//    private val email: String?
//
//    @SerializedName("password")
//    @Expose
//    private val password: String?
//
//    @SerializedName("os")
//    @Expose
//    private val os = "ANDROID"
//
//    @SerializedName("device_token")
//    @Expose
//    private val deviceToken: String?
//
//    @SerializedName("brand")
//    @Expose
//    private val brand: String?
//)
