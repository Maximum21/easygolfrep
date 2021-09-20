package com.minhhop.easygolf.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minhhop.core.domain.ErrorResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext
@Deprecated("do not implementation")
open class EasyGolfViewModel1 : ViewModel() {
    private val mParenJob = Job()
    private val mCoroutineContext: CoroutineContext
        get() =  mParenJob + Dispatchers.Default
    val mScope = CoroutineScope(mCoroutineContext)

    val mCommonErrorLive = MutableLiveData<ErrorResponse>()

    fun cancelAllRequest(){
        mCoroutineContext.cancel()
    }
}