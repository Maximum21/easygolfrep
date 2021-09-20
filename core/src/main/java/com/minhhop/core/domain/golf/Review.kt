package com.minhhop.core.domain.golf

import com.minhhop.core.domain.User

data class Review(
        val id:String,
        val user:User,
        val rate:Double,
        val content:String,
        val date_created:String,
        val last_updated:String
)