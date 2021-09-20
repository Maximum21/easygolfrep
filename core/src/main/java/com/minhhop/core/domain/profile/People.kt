package com.minhhop.core.domain.profile

import com.minhhop.core.domain.Country
import com.minhhop.core.domain.Location

data class People (
        var id: String,
        val email: String? = null,
        val gg_id: String? = null,
        val first_name: String?,
        val last_name: String?,
        val gender: String? = null,
        val country_code: String? = null,
        val phone_notification: Boolean = false,
        val email_notification: Boolean = false,
        val birthday: String? = null,
        val phone_number: String,
        val avatar: String? = null,
        val title: String? = null,
        val enabled: Boolean = false,
        val company: String? = null,
        val country_id: String? = null,
        val country: Country? = null,
        val google_id: String? = null,
        val facebook_id: String? = null,
        val status: String? = null,
        val date_created: String? = null,
        var handicap: Double = 0.0,
        var friends: Int = 0,
        var following: Int = 0,
        var code: String,
        var prefix: String,
        var language: String,
        var tee: String,
        var tee_id: String,
        var female: Boolean,
        val last_updated:String,
        val apple_id:String,
        val fullname:String,
        val estimated_handicap:Boolean,
        val last_location: Location,
        val fb_id:String,
        val profile_id:String
)