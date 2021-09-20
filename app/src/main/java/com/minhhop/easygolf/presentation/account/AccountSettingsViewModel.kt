package com.minhhop.easygolf.presentation.account

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.User
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class AccountSettingsViewModel (private val interactors: Interactors) : EasyGolfViewModel() {
    val userProfile = MutableLiveData<User>()
    fun getUser() = interactors.getProfileInLocal{
        mCommonErrorLive.postValue(it)
    }

    fun getUserProfile(){
        mScope.launch {
            interactors.getProfileUser.invoke({ result->
                userProfile.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
}