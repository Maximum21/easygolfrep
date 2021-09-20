package com.minhhop.easygolf.presentation.signin

import android.content.Intent
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import kotlinx.android.synthetic.main.activity_signin.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.ext.android.inject

class SignInActivity : EasyGolfActivity<SignInViewModel>(), FacebookCallback<LoginResult> {
    override val mViewModel: SignInViewModel
            by inject()

    /**
     * callback for facebook
     *
     */
    private var callbackManager: CallbackManager? = null

    override fun setLayout(): Int = R.layout.activity_signin

    override fun initView() {
        startMaskView.alpha = 1f
        startMaskView.animate().alpha(0f).setDuration(300)
                .setInterpolator(LinearInterpolator())
                .start()

        btSignUp.setOnClickListener {
            EasyGolfNavigation.signUpDirection(this)
        }

        btSignIn.setOnClickListener {
            val username = editLogin.text?.toString()
            val password = editPassword.text?.toString()
            if (!username.isNullOrEmpty() && !password.isNullOrEmpty()) {
                btSignIn.setStatus(EasyGolfButton.Status.PROCESS)
                mViewModel.signIn(username.trim { it <= ' ' }, password)
            } else {
                if (username.isNullOrEmpty()) {
                    showCommonMessage(getString(R.string.missing_username))
                } else {
                    showCommonMessage(getString(R.string.missing_password))
                }
            }
        }

        btForgotPassword.setOnClickListener {
            EasyGolfNavigation.forgotPasswordDirection(this)
        }

        mViewModel.isUnRegisterLive.observe(this, Observer {
            EasyGolfNavigation.forgotPasswordDirection(this,it)
        })

        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager, this)

        btLoginFacebook.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
    }

    override fun needToOverrideFontSize(): Boolean = true

    override fun loadData() {
        mViewModel.signInResultLive.observe(this, Observer {
            btSignIn.setStatus(EasyGolfButton.Status.IDLE)
            hideMask()
            if (it == null) {
                EasyGolfNavigation.easyGolfHomeDirection(this)
                finish()
            } else {
               showCommonMessage(getString(R.string.login_fail))
            }
        })
    }


    /**
     * Handle callback facebook
     * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSuccess(result: LoginResult?) {
        result?.let { loginResult ->
            mViewModel.signInWithFacebook(loginResult)
        }
    }

    override fun onCancel() {
    }

    override fun onError(error: FacebookException?) {
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        if (mViewModel.isReadySignIn()) {
            EasyGolfNavigation.easyGolfHomeDirection(this)
            finish()
        }
    }
}