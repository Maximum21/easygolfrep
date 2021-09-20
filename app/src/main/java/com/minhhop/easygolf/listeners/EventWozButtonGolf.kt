package com.minhhop.easygolf.listeners

import com.minhhop.easygolf.framework.models.common.DataPlayGolf

interface EventWozButtonGolf {
    fun onSaveMe(data: DataPlayGolf)
    fun onMore()
}