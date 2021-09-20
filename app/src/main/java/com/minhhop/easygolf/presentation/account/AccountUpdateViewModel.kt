package com.minhhop.easygolf.presentation.account

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.Country
import com.minhhop.core.domain.User
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class AccountUpdateViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val userLiveData = MutableLiveData<User>()
    val userProfileLiveData = MutableLiveData<User>()
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

    fun updateProfileUser(firstName: String, lastName: String, email: String?,
                          countryCode: String, phoneNumber: String?, birthday: String?,
                          gender: String?, phoneNotification: Boolean, emailNotification: Boolean) {
        mScope.launch {
            interactors.updateProfileUser(firstName, lastName, email, countryCode, phoneNumber, birthday, gender, phoneNotification, emailNotification, {
                userLiveData.postValue(it)
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun updateUserProfileInLocal(user: User) {
        mScope.launch {
            interactors.updateUserProfileInLocal(user, {
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun getUserProfile() {
        interactors.getProfileInLocal{
            mCommonErrorLive.postValue(it)
        }?.let { user ->
            userProfileLiveData.postValue(user)
        }?:  mScope.launch {
            interactors.getProfileUser({
                userProfileLiveData.postValue(it)
            },{mCommonErrorLive.postValue(it)})
        }

//        mScope.launch {
//            interactors.getProfileUser.invoke({ result ->
//                userProfile.postValue(result)
//            }, { error ->
//                mCommonErrorLive.postValue(error)
//            })
//        }
    }
}