package com.minhhop.easygolf.framework.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ResendCodeRequest (
    @SerializedName("phone_number")
    @Expose
    private val phoneNumber: String,

    @SerializedName("country_code")
    @Expose
    private val countryCode: String
)