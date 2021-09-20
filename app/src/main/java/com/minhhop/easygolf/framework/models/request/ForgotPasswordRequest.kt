package com.minhhop.easygolf.framework.models.request

data class ForgotPasswordRequest(
        val phone_number: String,
        val country_code: String
)