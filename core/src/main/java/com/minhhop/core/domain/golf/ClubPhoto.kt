package com.minhhop.core.domain.golf

import com.minhhop.core.domain.User

data class ClubPhoto(
        val id:String,
        val photo:String?,
        val user:User
)