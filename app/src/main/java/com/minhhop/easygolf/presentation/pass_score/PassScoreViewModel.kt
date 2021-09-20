package com.minhhop.easygolf.presentation.pass_score

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.golf.PassScore
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class PassScoreViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val passScoreCallbackLive = MutableLiveData<VerificationMessage>()
    fun passScoreForCourse(courseId:String,teeType:String,grossScore:Double,adjustedScore:Double,date:Long){
        mScope.launch {
            interactors.passScoreGolfForCourse(PassScore(
                    courseId,grossScore,date,adjustedScore,
                    teeType

            ),{
                passScoreCallbackLive.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}