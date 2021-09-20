package com.minhhop.core.domain

data class Authorization(
        val user_name:String,
        val token_type:String,
        val access_token:String,
        val status:String? = null
)