package com.minhhop.easygolf.framework.bundle

data class UnregisterBundle (
    val type:String = "facebook",
    val accessToken:String,
    val email:String?
)