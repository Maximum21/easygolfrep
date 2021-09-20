package com.minhhop.easygolf.presentation.home.newsfeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.feed.NewsFeedResponse
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class NewsFeedViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val newsFeedPagerLive = MutableLiveData<NewsFeedResponse>()
    val newsFeedLiked = MutableLiveData<NewsFeed>()
    var startPage = 0
    var startUserPage = 0
    fun fetchPosts(tag:String, onRefresh:Boolean = false){
        if (onRefresh) {
            startPage = 0;
        }
        mScope.launch {
            interactors.newsfeeds.invoke(tag,startPage,0,{ result->
                result?.let{
                    newsFeedPagerLive.postValue(it)
                    startPage = result.paginator.start;
                }
            },{ error->
                mCommonErrorLive.postValue(error)
            })
        }

    }
    fun fetchUserPosts(id:String){
        mScope.launch {
            interactors.newsfeeds.invoke(id,startUserPage,1,{ result->
                result?.let{
                    newsFeedPagerLive.postValue(it)
                    startUserPage = result.paginator.start
                }
            },{ error->
                mCommonErrorLive.postValue(error)
            })
        }

    }

    fun likePost(postId: String) {
        mScope.launch {
            interactors.newsfeeds.invoke(postId,0,{ result->
                Log.e("testingmlike","$result")
                newsFeedLiked.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
                var temp = newsFeedPagerLive.value!!
                newsFeedPagerLive.postValue(temp)
            })
        }
    }

    fun unLikePost(postId: String) {
        mScope.launch {
            interactors.newsfeeds.invoke(postId,1,{ result->
                Log.e("testingmlike","$result")
                newsFeedLiked.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
                val temp = newsFeedPagerLive.value!!
                newsFeedPagerLive.postValue(temp)
            })
        }
    }

    fun getUser() = interactors.getProfileInLocal{
        mCommonErrorLive.postValue(it)
    }

    fun fetchClubPosts(clubId: String,start:String = "0") {
        mScope.launch {
            interactors.newsfeeds.invoke(clubId,start.toInt(),2,{ result->
                Log.e("testingmlike","$result")
                newsFeedPagerLive.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
                val temp = newsFeedPagerLive.value!!
                newsFeedPagerLive.postValue(temp)
            })
        }
    }
}