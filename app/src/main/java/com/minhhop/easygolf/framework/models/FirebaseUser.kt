package com.minhhop.easygolf.framework.models

data class FirebaseUser (
        var _id:String = "",
        var first_name:String = "",
        var last_name:String = "",
        var avatar:String = "",
        var online:Boolean = false
){
    fun getFullName():String = "$first_name $last_name"

    fun getShortName():String = "${this.first_name.substring(0, 1)}.${this.last_name}"
}