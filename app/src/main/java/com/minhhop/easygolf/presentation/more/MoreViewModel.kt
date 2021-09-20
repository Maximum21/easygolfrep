package com.minhhop.easygolf.presentation.more

import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel

class MoreViewModel(private val interactors: Interactors) : EasyGolfViewModel() {

    fun logout(){
        interactors.logout()
    }
}