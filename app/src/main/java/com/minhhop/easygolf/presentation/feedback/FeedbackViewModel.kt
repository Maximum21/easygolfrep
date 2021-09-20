package com.minhhop.easygolf.presentation.feedback

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.golf.Feedback
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class FeedbackViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val feedbackCallbackLive = MutableLiveData<VerificationMessage>()
    fun sendFeedback(subject:String?,content:String?){
        mScope.launch {
            interactors.sendFeedbackGolf(Feedback(subject, content),{
                feedbackCallbackLive.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}