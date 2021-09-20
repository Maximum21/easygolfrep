package com.minhhop.easygolf.listeners

import com.minhhop.easygolf.framework.models.UserChat

interface EventMessenger {
    fun onClick(userChat: UserChat)
}