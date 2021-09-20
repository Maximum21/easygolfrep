package com.minhhop.easygolf.presentation.forgotpassword

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.Country
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.VerifyCodeBundle
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val currentCountryLiveData = MutableLiveData<Country>()
    fun getDefaultCountry(iso:String?) {
        mScope.launch {
            interactors.fetchCountries({
                interactors.getDefaultCountryByLocale(iso,{ country ->
                    country?.let {
                        currentCountryLiveData.postValue(it)
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })

            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun setCountry(country: Country){
        currentCountryLiveData.postValue(country)
    }

    val sendForgotPasswordCallbackLiveData = MutableLiveData<VerifyCodeBundle>()
    fun postForgotPassword(phoneNumber:String){
        currentCountryLiveData.value?.let { country ->
            mScope.launch {
                interactors.userForgotPassword(phoneNumber,country.iso,{
                    sendForgotPasswordCallbackLiveData.postValue(VerifyCodeBundle(
                            phoneNumber,country.iso
                    ))
                },{
                    mCommonErrorLive.postValue(it)
                })
            }
        }
    }

    fun unregisterForSocial(phoneNumber:String,email:String?,accessToken:String,type:String){
        currentCountryLiveData.value?.let { country ->
            mScope.launch {
                interactors.unregisterForLoginBySocial(type,accessToken,phoneNumber,country.iso,email,{
                    sendForgotPasswordCallbackLiveData.postValue(VerifyCodeBundle(
                            phoneNumber,country.iso
                    ))
                },{
                    mCommonErrorLive.postValue(it)
                })
            }
        }
    }
}