package com.minhhop.easygolf.presentation.signup

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.VerifyCodeBundle
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.utils.AppUtil
import kotlinx.coroutines.launch

class RegisterViewModel(private val interactors: Interactors): EasyGolfViewModel() {

    var currentCountry = MutableLiveData<Country>()

    val commonErrorLive = MutableLiveData<ErrorResponse>()
    val missingField = MutableLiveData<Int>()
    val registerSuccess = MutableLiveData<VerifyCodeBundle>()

    fun fetchRegister(firstName:String,lastName:String,email:String,phone:String,password:String,confirmPassword:String){
        if(!AppUtils.isTextEmpty(firstName,lastName,email,phone,password,confirmPassword) && password.length > 6 && password == confirmPassword){
            currentCountry.value?.iso?.apply {
                fetchApiRegister(firstName,lastName,email,phone,this,password)
            }
        }else{
             if (AppUtils.isTextEmpty(firstName)) {
                missingField.postValue(R.string.error_first_name)
            }

            if (TextUtils.isEmpty(lastName)) {
                missingField.postValue(R.string.error_last_name)
            }

            if (!AppUtil.isValidEmail(email)) {
                missingField.postValue(R.string.error_email)
            }

            if (TextUtils.isEmpty(phone)) {
                missingField.postValue(R.string.error_phone)
            }

            if(password.length < 6){
                missingField.postValue(R.string.error_password)
            }else{
                if (password != confirmPassword) {
                    missingField.postValue(R.string.error_confirm_password)
                }
            }
        }
    }

    fun getDefaultCountry(){
        currentCountry.postValue(interactors.getDefaultCountry())
    }

    fun setCurrentCountry(name:String,iso: String){
        val newCountry = Country("easygolf",name,name,null,null,null,0,null,null,
                iso,0,null,0)
        currentCountry.postValue(newCountry)
    }

    private fun fetchApiRegister(firstName: String,lastName: String,email: String,phone: String,countryCode: String,password: String){
        mScope.launch {
            interactors.signUp(firstName,lastName,countryCode,email,phone,password,{
                registerSuccess.postValue(VerifyCodeBundle(phone, countryCode))
            },{error->
                commonErrorLive.postValue(error)
            })
        }
    }
}
