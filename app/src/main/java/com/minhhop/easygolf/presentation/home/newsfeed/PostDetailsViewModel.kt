package com.minhhop.easygolf.presentation.home.newsfeed

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class PostDetailsViewModel (private val interactors: Interactors) : EasyGolfViewModel() {
    val commentsList = MutableLiveData<List<Comment>>()
    val comment = MutableLiveData<Comment>()
    val deletePost = MutableLiveData<String>()
    val removeComment = MutableLiveData<String>()
    val replaceComment = MutableLiveData<Comment>()
    val newsFeedModel = MutableLiveData<NewsFeed>()
    fun addComment(text: String, id: String) {
        mScope.launch {
            interactors.postDetails.invoke(id,text,{ result->
                comment.postValue(result)

            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun listComments(id: String) {
        mScope.launch {
            interactors.postDetails.invoke(id,{ result->
                commentsList.postValue(result.data)

            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
    fun deletePost(id: String) {
        mScope.launch {
            interactors.postDetails.invoke(id,0,"",{ result->
                deletePost.postValue(id)

            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun deleteComment(postId: String, commentId: String) {
        mScope.launch {
            interactors.postDetails.invoke(postId,commentId,0,{ result->
                removeComment.postValue(commentId)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun editComment(postId: String, commentId: String,comment: String) {
        mScope.launch {
            interactors.postDetails.invoke(postId,commentId,comment,1,{ result->
                Log.e("WOW","won: ${result.comment}")
                replaceComment.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
    fun likePost(postId: String) {
        mScope.launch {
            interactors.newsfeeds.invoke(postId,0,{ result->
                newsFeedModel.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun unLikePost(postId: String) {
        mScope.launch {
            interactors.newsfeeds.invoke(postId,1,{ result->
                newsFeedModel.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun getUser() = interactors.getProfileInLocal{
        mCommonErrorLive.postValue(it)
    }
}