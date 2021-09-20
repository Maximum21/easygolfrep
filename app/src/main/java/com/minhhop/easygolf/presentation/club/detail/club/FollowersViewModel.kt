package com.minhhop.easygolf.presentation.club.detail.club

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class FollowersViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val followersLiveData = MutableLiveData<FollowersResponse>()
    fun getFollowers(id:String , start : Int = 0){
        mScope.launch {
            interactors.onFollowForClub.invoke(id, start,{
                followersLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

}