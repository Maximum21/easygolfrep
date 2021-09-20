package com.minhhop.easygolf.framework.base

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.golf.ClubPager
import com.minhhop.easygolf.framework.Interactors
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * <p>Extend this model if we going to use nearby clubs api</p>
 */
open class GolfClubViewModel(open val interactors: Interactors) : EasyGolfViewModel() {
    var start = 0
    val clubNearbyPagerLive = MutableLiveData<ClubPager>()
    fun fetchNearBy(latitude: Double, longitude: Double, keyword: String, onRefresh: Boolean = false) {
        if (onRefresh) {
            start = 0
        }

        mScope.launch {
            interactors.clubsNearby(latitude, longitude, start, keyword, { result ->
                clubNearbyPagerLive.postValue(result)
                start = result.paginator.start;
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    var suggestedOffset = 0
    val suggestedClubsPagerLive = MutableLiveData<ClubPager>()
    fun fetchSuggestedClubs(latitude: Double, longitude: Double, keyword: String, onRefresh: Boolean = false) {
        if (onRefresh) {
            suggestedOffset = 0
        }

        mScope.launch {
            interactors.fetchSuggestedClubs(latitude, longitude, suggestedOffset, keyword, { result ->
                suggestedClubsPagerLive.postValue(result)
                suggestedOffset = result.paginator.start;
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    private var searchJob: Job? = null

    fun searchDebounced(latitude: Double, longitude: Double, keyword: String, startInner: Int = 0) {
        searchJob?.cancel()
        searchJob = mScope.launch {
            delay(300)
            interactors.fetchSuggestedClubs(latitude, longitude, startInner, keyword, { result ->
                suggestedClubsPagerLive.postValue(result)
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }
}