package com.minhhop.core.domain

data class Contact(
        val avatar:String?,
        val firstName:String?,
        val lastName:String?,
        val phone_number:String,
        val country_code:String?,
        var selected:Boolean = false
){
    fun getFullName():String = "$firstName $lastName"

}