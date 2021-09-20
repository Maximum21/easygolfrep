package com.minhhop.easygolf.framework.models.firebase

import com.minhhop.core.domain.User

data class UserFirebase(
        var id: String = "",
        var first_name: String? = null,
        var last_name: String? = null,
        var avatar: String? = null,
        var handicap:Double = 0.0,
        var online: Boolean? = false,
        var timestamp: Long? = null
){
    fun toUser() = User(
            this.id,
            this.first_name,
            this.last_name,
            handicap = this.handicap,
            avatar = this.avatar
    )
}