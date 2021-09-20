package com.minhhop.easygolf.presentation.user

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.profile.PeopleResponse
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class UserProfileViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val profileDetails = MutableLiveData<PeopleResponse>()
    val requestAction = MutableLiveData<PeopleResponse>()
    fun fetchProfileDetails(id: String) {
        mScope.launch {
            interactors.userProfileRepository.invoke(id, {
                profileDetails.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun requestAction(id: String, s: String, i: Int) {
        mScope.launch {
            interactors.userProfileRepository.invoke(id, s, i, {
                requestAction.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }
}