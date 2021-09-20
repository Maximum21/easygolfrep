package com.minhhop.easygolf.presentation.feed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.Club
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import kotlinx.coroutines.launch

class CreateNewPostViewModel(override val interactors: Interactors) : GolfClubViewModel(interactors) {
    val friendsList = MutableLiveData<ArrayList<User>>()
    val responseCreated = MutableLiveData<NewsFeed>()
    val updatedPost = MutableLiveData<NewsFeed>()
    val imageDelete = MutableLiveData<String>()

    fun createPost(postDescription: String, files: ArrayList<String>, selectedClub: Club?, friendsList: List<User>) {
        mScope.launch {
            interactors.postDetails.invoke(postDescription, files, selectedClub, friendsList, { result ->
                responseCreated.postValue(result)
            }, { error ->
                Log.e("WOW", "error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun updatePost(postDescription: String, files: ArrayList<String>, selectedClub: Club?, friendsList: List<User>, id: String, tag:Int) {
        mScope.launch {
            interactors.postDetails.invoke(postDescription, files, selectedClub, friendsList, id,tag, { result ->
                updatedPost.postValue(result)
            }, { error ->
                Log.e("WOW", "error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun removeImage(postId: String, imageId: String, adapterPosition: Int) {
        mScope.launch {
            interactors.postDetails.invoke(postId, 1, imageId, { result ->
                imageDelete.postValue(result)
            }, { error ->
                Log.e("WOW", "error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
}