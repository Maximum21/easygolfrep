package com.minhhop.easygolf.presentation.splash

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.User
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class SplashViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    fun isReadySignIn(): Boolean = interactors.isReadySignIn()
    fun getProfileUserInLocal() = interactors.getProfileInLocal{ mCommonErrorLive.postValue(it) }

    val userProfileLiveData = MutableLiveData<User>()
    fun fetchProfileUser(){
        mScope.launch {
            interactors.getProfileUser({
                userProfileLiveData.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}