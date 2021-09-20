package com.minhhop.easygolf.listeners

import com.minhhop.easygolf.framework.models.common.BattleScorePlayer
import com.minhhop.easygolf.framework.models.ProfileUser

interface OnAddPlayerListener {
    fun onAdd()
    fun onClickPlayer( player: ProfileUser, data: BattleScorePlayer)
    fun removeUser(player: ProfileUser)

    fun showButtonCanncel()
}