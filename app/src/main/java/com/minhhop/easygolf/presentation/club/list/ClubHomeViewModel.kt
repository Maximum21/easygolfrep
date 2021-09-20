package com.minhhop.easygolf.presentation.club.list

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.golf.ClubPager
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import kotlinx.coroutines.launch
import retrofit2.Call

class ClubHomeViewModel(override val interactors: Interactors) : GolfClubViewModel(interactors) {

    fun fetchByHomePage(latitude: Double, longitude: Double, onLoadMore: Boolean = false, onRefresh: Boolean = false) {
        if (!onLoadMore) {
            mScope.launch {
                fetchNearBy(latitude, longitude, "", onRefresh)
            }
        }
        mScope.launch {
            fetchRecommend(latitude, longitude, onRefresh)
        }
    }

    var mCallbackFetchRecommend: Call<*>? = null
    val clubRecommendPagerLive = MutableLiveData<ClubPager>()
    var startPage = 0
    private suspend fun fetchRecommend(latitude: Double, longitude: Double, onRefresh: Boolean = false) {
        if (onRefresh) {
            mCallbackFetchRecommend?.let { callbackDrama ->
                if (!callbackDrama.isCanceled) {
                    callbackDrama.cancel()
                }
            }
            startPage = 0
        }
        interactors.fetchRecommendClub(latitude, longitude, startPage, { result ->
            clubRecommendPagerLive.postValue(result)
            startPage = result.paginator.start
        }, { error ->
            mCommonErrorLive.postValue(error)
        }, { callback ->
            mCallbackFetchRecommend = (callback as? ResponseRequest<*>)?.call
        })
    }

}