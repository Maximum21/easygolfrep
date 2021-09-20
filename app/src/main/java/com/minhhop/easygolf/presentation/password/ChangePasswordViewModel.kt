package com.minhhop.easygolf.presentation.password

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val interactors: Interactors) : EasyGolfViewModel(){

    val changePassword = MutableLiveData<String>()
    fun ChangePassword(resetPassword: ResetPassword){
        Log.e("testreset","${Gson().toJson(resetPassword)}");
        mScope.launch {
            interactors.getProfileUser.invoke(resetPassword,0,{
                changePassword.postValue("")
            },{
                Log.e("testreset","ERROR NOW ${it.message}");
            })
        }
    }
}