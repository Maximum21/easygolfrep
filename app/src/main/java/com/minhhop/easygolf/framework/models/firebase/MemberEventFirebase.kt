package com.minhhop.easygolf.framework.models.firebase

import com.minhhop.core.domain.User

data class MemberEventFirebase(
        var isRemove:Boolean = false,
        var user:User
)