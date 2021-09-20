package com.minhhop.easygolf.presentation.player.add

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.User
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class AddPlayerViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val listFriends = MutableLiveData<List<User>>()
    fun fetchFriends(){
        mScope.launch {
            interactors.fetchFriends({
                listFriends.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}