package com.minhhop.easygolf.presentation.notification

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.notifications.GolfNotification
import com.minhhop.core.domain.notifications.NotificationPager
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.GolfClubViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationViewModel (override val interactors: Interactors) : GolfClubViewModel(interactors) {
    var notifications = MutableLiveData<NotificationPager>()
    var readNotify = MutableLiveData<GolfNotification>()
    var deleteNotify = MutableLiveData<GolfNotification>()
    var startS = 0

    private var searchJob: Job? = null

    fun getNotifications(text:String) {
        Log.e("testingchecktoks","$startS")
        searchJob?.cancel()
        searchJob = mScope.launch {
            interactors.notificationRepository.invoke(startS,text,{ result->
                Log.e("WOW","${result.data.size}")
                if(result?.data.isNotEmpty()){
                    startS += result.data.size
                    notifications.postValue(result)
                }
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
    fun readNotifications(id:String) {
        mScope.launch {
            interactors.notificationRepository.invoke(id,0,{ result->
                    readNotify.postValue(result)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
    fun deleteNotifications(data:GolfNotification) {
        mScope.launch {
            interactors.notificationRepository.invoke(data.id,1,{ result->
                    deleteNotify.postValue(data)
            },{ error->
                Log.e("WOW","error: ${error.message}")
                mCommonErrorLive.postValue(error)
            })
        }
    }
}