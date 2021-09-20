package com.minhhop.easygolf.presentation.round

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.round.RoundHistoryPager
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import kotlinx.coroutines.launch

class RoundHistoryViewModel(override val interactors: Interactors) : GolfClubViewModel(interactors) {
    var roundHistory = MutableLiveData<RoundHistoryPager>()
    var startS = 0

    private fun getProfileUserInLocal() = interactors.getProfileInLocal{
        mCommonErrorLive.postValue(it)
    }
    fun getRoundHistory() {
        getProfileUserInLocal()?.profile_id?.let { profileId->
            mScope.launch {
                interactors.roundHistoryRespository(startS, profileId, { result ->
                    roundHistory.postValue(result)
                    startS = result.paginator.start
                }, { error ->
                    mCommonErrorLive.postValue(error)
                })
            }
        }
    }
}