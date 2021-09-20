package com.minhhop.easygolf.framework.models.request

data class UpdateProfileRequest(
        val firstName: String?,
        val lastName: String,
        val email: String?,
        val countryCode:String,
        val phoneNumber: String?,
        val birthday:String?,
        val gender:String?,
        val phoneNotification:Boolean,
        val emailNotification:Boolean
)