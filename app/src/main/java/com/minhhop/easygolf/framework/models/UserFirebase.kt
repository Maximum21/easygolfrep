package com.minhhop.easygolf.framework.models

data class UserFirebase (
        val first_name:String?,
        val last_name:String?,
        val avatar:String?,
        val online:Boolean = false
)