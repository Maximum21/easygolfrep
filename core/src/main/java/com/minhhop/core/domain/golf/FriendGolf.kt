package com.minhhop.core.domain.golf

data class FriendGolf(
        val user_id:String,
        val matches:List<MatchGolf>?
)