package com.minhhop.easygolf.presentation.forgotpassword

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.view.View
import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.UnregisterBundle
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

open class ForgotPasswordActivity : EasyGolfActivity<ForgotPasswordViewModel>() {

    override val mViewModel: ForgotPasswordViewModel
            by inject()

    private var mUnregisterBundle:UnregisterBundle? = null
    override fun setLayout(): Int = R.layout.activity_forgot_password

    override fun initView() {
        mUnregisterBundle = EasyGolfNavigation.forgotPasswordBundle(intent)
        if(mUnregisterBundle != null){
            editEmail.setText(mUnregisterBundle?.email)
            layoutEmail.visibility = View.VISIBLE
        }else{
            layoutEmail.visibility = View.GONE
        }

        mViewModel.currentCountryLiveData.observe(this, Observer {
            valueNameCountry.text = getString(R.string.format_result_name_country, it.nice_name, it.phone_code.toString())
        })
        mViewModel.sendForgotPasswordCallbackLiveData.observe(this, Observer {
            btVerify.setStatus(EasyGolfButton.Status.IDLE)
            EasyGolfNavigation.verifyCodeDirection(this,it)
        })
        valueNameCountry.setOnClickListener {
            EasyGolfNavigation.countryDirection(this)
        }

        btVerify.setOnClickListener {
            if (btVerify.getStatus() == EasyGolfButton.Status.IDLE) {
               sendOTP()
            }
        }
    }

    open fun sendOTP(){
        val valuePhoneEditText = editPhone.text?.toString()
        if (!valuePhoneEditText.isNullOrEmpty()) {
            btVerify.setStatus(EasyGolfButton.Status.PROCESS)

            mUnregisterBundle?.let { unregisterBundle->
               mViewModel.unregisterForSocial(valuePhoneEditText,editEmail?.text?.toString(),unregisterBundle.accessToken,unregisterBundle.type)
            }?:mViewModel.postForgotPassword(valuePhoneEditText)
        } else {
            btVerify.setStatus(EasyGolfButton.Status.IDLE)
            showCommonMessage(getString(R.string.missing_filed))
        }
    }

    override fun loadData() {
        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        val countryCode = tm?.simCountryIso
        mViewModel.getDefaultCountry(countryCode)
    }
    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == Contains.REQUEST_CODE_COUNTRY && resultCode == Activity.RESULT_OK){
            EasyGolfNavigation.countryBundle(data)?.toCountry()?.let { countrySelected->
                mViewModel.setCountry(countrySelected)
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        finish()
    }
}