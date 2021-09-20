package com.minhhop.easygolf.presentation.splash

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.CountDownTimer
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.NotificationBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.ext.android.inject

class SplashActivity : EasyGolfActivity<SplashViewModel>() {

    override val mViewModel: SplashViewModel by inject()

    override fun setLayout(): Int = R.layout.activity_splash

    override fun initView() {
        /**
         * handle notification bundle
         * */

        if (!isTaskRoot) {
            NotificationBundle.extraBundle(intent.extras)?.let {
                directionNotificationBundle(it)
            }
            finish()
        } else {
            val displayMetrics = DisplayMetrics()
            this.windowManager.defaultDisplay.getMetrics(displayMetrics)
            Utils.current_width = displayMetrics.widthPixels.toDouble()
            Utils.current_height = displayMetrics.heightPixels.toDouble()
            Utils.width = Utils.current_width / Utils.Standard_width
            Utils.height = Utils.current_height / Utils.Standard_height
            playAnimationStartLetter()
        }
    }

    override fun loadData() {}

    private fun playAnimationStartLetter() {
        for (index in 0 until imgLogo.childCount) {
            createAnimationLetter(imgLogo.getChildAt(index), index)
        }
    }

    private fun createAnimationLetter(viewAnimation: View, index: Int) {
        viewAnimation.scaleY = 0f
        viewAnimation.pivotY = 1f
        viewAnimation.pivotX = 0.5f
        val scaleY = ObjectAnimator.ofFloat(viewAnimation, "scaleY", 1.1f)
        val scaleY2 = ObjectAnimator.ofFloat(viewAnimation, "scaleY", 1f)
        scaleY.interpolator = OvershootInterpolator()
        scaleY2.interpolator = OvershootInterpolator()
        val animatorTop = AnimatorSet().apply {
            startDelay = 200L * index
            play(scaleY).before(scaleY2)
            duration = 500L
            play(scaleY2)
        }

        if (index == imgLogo.childCount - 1) {
            animatorTop.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {
                }

                override fun onAnimationEnd(p0: Animator?) {
                    val animationG = (ContextCompat.getDrawable(this@SplashActivity, R.drawable.animation_letter_g) as? AnimatedVectorDrawable)
                    letterG.setImageDrawable(animationG)
                    animationG?.start()
                    timer.start()
                }

                override fun onAnimationCancel(p0: Animator?) {
                }

                override fun onAnimationStart(p0: Animator?) {
                }
            })
        }
        animatorTop.start()
    }

    private val timer = object : CountDownTimer(1500, 1000) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            letterG.pivotX = letterG.width / 2f
            letterG.pivotY = letterG.height / 2f
            val scaleX = ObjectAnimator.ofFloat(letterG, "scaleX", 40f)
            val scaleY = ObjectAnimator.ofFloat(letterG, "scaleY", 40f)
            scaleX.interpolator = OvershootInterpolator()
            scaleY.interpolator = OvershootInterpolator()
            val animatorTop = AnimatorSet().apply {
                duration = 300L
                playTogether(scaleX, scaleY)
            }

            animatorTop.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(p0: Animator?) {}
                override fun onAnimationEnd(p0: Animator?) {
                    if (mViewModel.isReadySignIn()) {
                        EasyGolfNavigation.easyGolfHomeDirection(this@SplashActivity,intent.extras)
                    } else {
                        EasyGolfNavigation.signInDirection(this@SplashActivity)
                    }
                    finish()
                }

                override fun onAnimationCancel(p0: Animator?) {}
                override fun onAnimationStart(p0: Animator?) {}

            })
            animatorTop.start()
        }
    }
}
