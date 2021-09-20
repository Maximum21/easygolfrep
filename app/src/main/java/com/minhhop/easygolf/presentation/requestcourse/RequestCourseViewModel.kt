package com.minhhop.easygolf.presentation.requestcourse

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.course.RequestCourse
import com.minhhop.core.domain.course.RequestedCourse
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import kotlinx.coroutines.launch

class RequestCourseViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val requestedCourse = MutableLiveData<RequestedCourse>()
    fun reqeustCourse(requestCourse: RequestCourse) {
        mScope.launch {
            interactors.clubsNearby.invoke(requestCourse, {
                requestedCourse.postValue(it)
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }
}