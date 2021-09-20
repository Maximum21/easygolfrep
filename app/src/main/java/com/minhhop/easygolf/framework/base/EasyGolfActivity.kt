package com.minhhop.easygolf.framework.base

import android.content.*
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.firebase.NotificationModel.Type.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.bundle.NotificationBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.presentation.custom.EasyGolfToolbar
import com.minhhop.easygolf.services.EasyGolfFirebaseService


abstract class EasyGolfActivity<out T : EasyGolfViewModel> : AppCompatActivity() {
    enum class StatusLoading {
        FIRST_LOAD,
        FINISH_LOAD,
        REFRESH_LOAD
    }

    var mStatusLoading = StatusLoading.FIRST_LOAD
    private var mRefreshLayout: SwipeRefreshLayout? = null

    abstract val mViewModel: T

    private var mViewMask: View? = null

    /**
     * Start handle Broad cast receiver
     * */

    private val mMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { intentReceive ->
                showCommonMessage("You got new notification", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, p1: Int) {
                        NotificationBundle.extraBundle(intentReceive.extras)?.let {
                            directionNotificationBundle(it)
                        }
                        dialog?.dismiss()
                    }
                })
            }
        }
    }

    fun directionNotificationBundle(notificationBundle: NotificationBundle){
        notificationBundle.parseBundle()?.let { parserBundle ->
            when (notificationBundle.type) {
                GENERAL -> {

                }
                ADD_FRIEND -> {

                }
                FOLLOW_USER -> {

                }
                ACCEPT_FRIEND_REQUEST -> {

                }
                DECLINE_FRIEND_REQUEST -> {

                }
                SYNC_CONTACT -> {

                }
                ROUND_INVITATION -> {
                    parserBundle.getString(PlayGolfBundle::roundId.name)?.let { roundGolfId->
                        EasyGolfNavigation.playGolfDirection(this, PlayGolfBundle(
                                PlayGolfBundle.TypeGame.BATTLE_GAME,roundGolfId,null,TeeUtils.DEFAULT_TEE_TYPE,null
                        ))
                    }
                }
                COMPLETE_ROUND -> {
                    EasyGolfNavigation.endGameBundle(parserBundle)?.let { endGameBundle ->
                        EasyGolfNavigation.endGameGolfDirection(this, endGameBundle)
                    }
                }
                APPROVED_ROUND -> {

                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                IntentFilter(EasyGolfFirebaseService.BROAD_CAST_INTENT))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
        super.onStop()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(setLayout())
        setupToolBar()
        setupViewMask()
        setupRefreshLayout()
        setTheme(R.style.ThemeAppDark)
        mViewModel.mCommonErrorLive.observe(this, Observer {
            errorResponseMessage(it)
            commonError()
        })
        initView()
        onCreateWithState(savedInstanceState)
        loadData()
    }

    open fun onCreateWithState(savedInstanceState: Bundle?) {

    }

    private val timerBack = object : CountDownTimer(2000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            mDoubleBackToExitPressedOnce = false
        }
    }
    private var mDoubleBackToExitPressedOnce = false
    private fun setupToolBar() {
        (findViewById(R.id.toolbarBack) as? EasyGolfToolbar)?.addEventBack(object : EasyGolfToolbar.EventToolbar {
            override fun onClickBack(isDoubleTap: Boolean) {
                if (isDoubleTap) {
                    if (!mDoubleBackToExitPressedOnce) {
                        mDoubleBackToExitPressedOnce = true
                        toast(getString(R.string.double_tap_to_back))
                        timerBack.start()
                    } else {
                        finish()
                    }
                } else {
                    finish()

                }
            }
        })
    }

    private fun setupViewMask() {
        mViewMask = findViewById(R.id.viewMask)
        mViewMask?.setOnClickListener { hideViewMaskCallBack() }
    }

    open fun hideViewMaskCallBack() {}

    private fun setupRefreshLayout() {
        mRefreshLayout = findViewById(R.id.refreshLayout)

        mRefreshLayout?.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPullToRefresh),
                ContextCompat.getColor(this, R.color.colorPullToRefreshMore))

        mRefreshLayout?.setOnRefreshListener {
            onRefreshData()
        }
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(newBase)
        if (needToOverrideFontSize()) {
            val newOverride = Configuration(newBase?.resources?.configuration)
            newOverride.fontScale = 1.0f
            applyOverrideConfiguration(newOverride)
        }
    }

    open fun needToOverrideFontSize() = false

    open fun onRefreshData() {
        mStatusLoading = StatusLoading.REFRESH_LOAD
    }

    open fun hideRefreshLoading() {
        mRefreshLayout?.isRefreshing = false
    }

    fun showCommonMessage(message: String?, event: DialogInterface.OnClickListener? = null) {
        MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_no_network))
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton(getString(android.R.string.ok), event)
                .show()
    }

    private fun showNoNetworkFoundDialog() {
        MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.title_no_network))
                .setMessage(getString(R.string.content_no_network))
                .setCancelable(false)
                .setPositiveButton(getString(android.R.string.ok), null)
                .show()
    }

    private fun errorResponseMessage(errorResponse: ErrorResponse) {
        if (errorResponse.noNetwork) {
            showNoNetworkFoundDialog()
        } else {
            showCommonMessage(errorResponse.message)
        }
    }

    fun getViewRoot() = findViewById<View?>(R.id.viewRoot)

    open fun viewMask() {
        if (mViewMask?.visibility != View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.show_alpha)
            mViewMask?.visibility = View.VISIBLE
            mViewMask?.startAnimation(animation)
        }
    }

    open fun hideMask() {
        if (mViewMask?.visibility != View.GONE) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.hide_alpha)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    mViewMask?.visibility = View.GONE
                }

                override fun onAnimationStart(animation: Animation?) {}
            })
            mViewMask?.startAnimation(animation)
        }
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    fun hideSoftKeyboard() {
        currentFocus?.let { viewFocus ->
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(viewFocus.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    open fun commonError() {
        hideMask()
    }

    abstract fun setLayout(): Int

    abstract fun initView()

    abstract fun loadData()


    override fun onDestroy() {
        mViewModel.cancelAllRequest()
        super.onDestroy()
    }

    protected fun isHideSoftKeyBoardTouchOutSide(): Boolean {
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (isHideSoftKeyBoardTouchOutSide()) {
            val viewFocus = window.currentFocus
            (viewFocus as? EditText)?.let { focus ->
                Log.e("WOW", "viewFocus")
                val screenLocation = IntArray(2)
                viewFocus.getLocationOnScreen(screenLocation)
                val x = event.rawX + focus.left - screenLocation[0]
                val y = event.rawY + focus.top - screenLocation[1]

                if (event.action == MotionEvent.ACTION_DOWN) {
                    if (x < focus.left || x >= focus.right
                            || y < focus.top || y >= focus.bottom
                    ) {
                        hideSoftKeyboard()
                        viewFocus.clearFocus()
                    }
                }
            }
        }

        return super.dispatchTouchEvent(event)
    }
}