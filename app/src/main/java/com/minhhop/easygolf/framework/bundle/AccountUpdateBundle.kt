package com.minhhop.easygolf.framework.bundle

import com.google.gson.Gson
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.User
import java.lang.Exception

data class AccountUpdateBundle (
        var user: String?,
        var tag:Boolean=false
){
    fun toUser(): User? {
        return try {
            Gson().fromJson(this.user, User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}