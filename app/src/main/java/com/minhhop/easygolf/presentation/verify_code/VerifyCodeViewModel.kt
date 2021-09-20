package com.minhhop.easygolf.presentation.verify_code

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.VerifyCodeBundle
import kotlinx.coroutines.launch

class VerifyCodeViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    var mVerifyCodeBundle: VerifyCodeBundle? = null
    val verifyOnSuccess = MutableLiveData<Boolean>()

    private var mFinishCountDown = false
    var countDownLive = MutableLiveData<Long>()
    private val timer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            countDownLive.postValue(millisUntilFinished / 1000)
        }

        override fun onFinish() {
            mFinishCountDown = true
        }
    }

    fun countDown() {
        mFinishCountDown = false
        timer.start()
    }

    fun verifyCode(code: String) {
        mScope.launch {
            interactors.verifyCodeByToken(code, { result ->
                interactors.saveAccessTokenInLocal(result.access_token)
                /**
                 * save info user in cache
                 * */
                fetchProfileUser()
            }, { errorResponse ->
                mCommonErrorLive.postValue(errorResponse)
            })
        }
    }

    fun resendCode() {
        if(mFinishCountDown) {
            mVerifyCodeBundle?.let {
                mScope.launch {
                    interactors.resendCodeOTP(it.phone, it.countryCode, {
                        countDown()
                    }, { errorResponse ->
                        mCommonErrorLive.postValue(errorResponse)
                    })
                }
            }
        }
    }

    private fun fetchProfileUser() {
        mScope.launch {
            interactors.getProfileUser({
                verifyOnSuccess.postValue(true)
            }, { error ->
                mCommonErrorLive.postValue(error)
            })
        }
    }
}