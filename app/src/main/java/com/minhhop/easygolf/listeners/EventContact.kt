package com.minhhop.easygolf.listeners

import com.minhhop.easygolf.framework.models.entity.UserEntity

interface EventContact {
    fun onClick(userEntity: UserEntity)
}