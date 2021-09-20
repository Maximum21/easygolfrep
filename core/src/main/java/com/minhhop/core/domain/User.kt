package com.minhhop.core.domain

import java.util.*

data class User (
    var id: String,
    var first_name: String? = null,
    var last_name: String? = null,
    var email: String? = null,
    var profile_id: String? = null,
    var gender: String? = null,
    var country_code: String? = null,
    var phone_notification: Boolean? = null,
    var email_notification: Boolean? = null,
    var birthday: String? = null,
    var phone_number: String? = null,
    var avatar: String? = null,
    var title: String? = null,
    var enabled: Boolean? = null,
    var company: String? = null,
    var country_id: String? = null,
    var country: Country? = null,
    var google_id: String? = null,
    var facebook_id: String? = null,
    var status: String? = null,
    var date_created: String? = null,
    var handicap: Double? = null,
    var friends: Int? = null,
    var following: Int? = null,
    var language : String? = null
){
    val fullName:String?
       get() {
           return "$first_name $last_name"
        }

    val cutName:String?
        get() {
            return if(this.first_name?.length?:-1 > 0) {
                val lastName =  if(this.last_name?.length?:-1 > 0) { this.last_name?.substring(0,1)?.toUpperCase(Locale.getDefault()) } else null
                "${this.first_name?.substring(0,1)?.toUpperCase(Locale.getDefault())}$lastName"
            } else {
                this.last_name?:"UK"
            }
        }
    val shortName: String?
        get() {
            return if(this.first_name?.length?:-1 > 0) {
                "${this.first_name?.substring(0,1)}.${this.last_name?:"Unknown"}"
            } else {
                this.last_name?:"Unknown"
            }
        }

}