package com.minhhop.easygolf.listeners

import com.minhhop.easygolf.framework.models.entity.UserEntity

interface EventSelectPeople {
    fun onSelect(userEntity: UserEntity)
    fun onUnSelect(userEntity: UserEntity)
}