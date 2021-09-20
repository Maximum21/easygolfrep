package com.minhhop.easygolf.presentation.verify_code

import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import kotlinx.android.synthetic.main.activity_verify_code.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.ext.android.inject

class VerifyCodeActivity : EasyGolfActivity<VerifyCodeViewModel>() {
    override val mViewModel: VerifyCodeViewModel
            by inject()

    override fun setLayout(): Int = R.layout.activity_verify_code

    override fun initView() {
        EasyGolfNavigation.verifyCodeBundle(intent)?.let {
            mViewModel.mVerifyCodeBundle = it
            val builderPhone = StringBuilder(it.phone)
            for (i in 0 until it.phone.length - 4) {
                builderPhone.setCharAt(i, '*')
            }
            mTxtOtpNumber.text = builderPhone.toString()
        } ?: finish()

        mViewModel.countDownLive.observe(this, Observer {
            btResendCode.text = if (it <= 0) {
                getString(R.string.resend_code_clear)
            } else {
                getString(R.string.resend_code, it.toString())
            }
        })
        mViewModel.verifyOnSuccess.observe(this, Observer {
            verifyCodeView.burnIceKeyboard()
            btSubmit.setStatus(EasyGolfButton.Status.IDLE)
            EventBus.getDefault().post(MessageEvent())
            finish()
        })
        btResendCode.setOnClickListener {
            mViewModel.resendCode()
        }
        btSubmit.setOnClickListener {
            if(btSubmit.isEnabled){
                btSubmit.setStatus(EasyGolfButton.Status.PROCESS)
                sendOTP(verifyCodeView.textString)
            }
        }
    }

    override fun commonError() {
        super.commonError()
        verifyCodeView.setCodeItemErrorLineDrawable()
        verifyCodeView.burnIceKeyboard()
        btSubmit.setStatus(EasyGolfButton.Status.IDLE)
    }
    override fun loadData() {
        //Set the schedule function
        mViewModel.countDown()
        verifyCodeView.openKeyboard()
    }

    private fun sendOTP(code:String){
        verifyCodeView.freezeKeyboard()
        mViewModel.verifyCode(code)
    }
}