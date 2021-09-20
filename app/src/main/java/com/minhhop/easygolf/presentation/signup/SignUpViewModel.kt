package com.minhhop.easygolf.presentation.signup

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.User
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.VerifyCodeBundle
import com.minhhop.easygolf.framework.models.UpdateUser
import kotlinx.coroutines.launch

class SignUpViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val currentCountryLiveData = MutableLiveData<Country>()
    fun getDefaultCountry(iso: String?) {
        mScope.launch {
            interactors.fetchCountries({
                interactors.getDefaultCountryByLocale(iso, { country ->
                    country?.let {
                        currentCountryLiveData.postValue(it)
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })

            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun setCountry(country: Country) {
        currentCountryLiveData.postValue(country)
    }

    val registerSuccess = MutableLiveData<VerifyCodeBundle>()
    fun fetchApiRegister(firstName: String, lastName: String, email: String, phone: String, password: String) {
        currentCountryLiveData.value?.let { country ->
            mScope.launch {
                interactors.signUp(firstName, lastName, country.iso, email, phone, password, {
                    registerSuccess.postValue(VerifyCodeBundle(phone, country.iso))
                }, { error ->
                    mCommonErrorLive.postValue(error)
                })
            }
        }

    }
}