package com.minhhop.easygolf.presentation.support

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator


class RevealAnimation(private val mView:View,private val mActivity:Activity,intent:Intent) {
    companion object{
        const val EXTRA_CIRCULAR_REVEAL_X = "EXTRA_CIRCULAR_REVEAL_X"
        const val EXTRA_CIRCULAR_REVEAL_Y = "EXTRA_CIRCULAR_REVEAL_Y"
    }

    private var revealX:Int = 0
    private var revealY:Int = 0
    init {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
//                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_X) &&
//                intent.hasExtra(EXTRA_CIRCULAR_REVEAL_Y)) {
            mView.setVisibility(View.INVISIBLE)
            revealX = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_X, 0)
            revealY = intent.getIntExtra(EXTRA_CIRCULAR_REVEAL_Y, 0)
            revealActivity(revealX, revealY)

//            val viewTreeObserver: ViewTreeObserver = mView.getViewTreeObserver()
//            if (viewTreeObserver.isAlive) {
//                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        revealActivity(revealX, revealY)
//                        mView.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                    }
//                })
//            }
//        } else {
//
//            //if you are below android 5 it jist shows the activity
//            mView.setVisibility(View.VISIBLE)
//        }
    }


    fun revealActivity(x: Int, y: Int) {
        Log.e("WOW","Build.VERSION.SDK_INT: ${Build.VERSION.SDK_INT}")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()

            // create the animator for this view (the start radius is zero)
            val circularReveal:Animator = ViewAnimationUtils.createCircularReveal(mView, 0, 0, 0f, 500f)
            circularReveal.duration = 300
            circularReveal.interpolator = AccelerateInterpolator()

            // make the view visible and start the animation
            mView.visibility = View.VISIBLE
            circularReveal.start()
        } else {
            mActivity.finish()
        }
    }

    fun unRevealActivity() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mActivity.finish()
        } else {
            val finalRadius = (Math.max(mView.width, mView.height) * 1.1).toFloat()
            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(
                    mView, revealX, revealY, finalRadius, 0f)
            circularReveal.setDuration(5000)
            circularReveal.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    mView.visibility = View.INVISIBLE
                    mActivity.finish()
                    mActivity.overridePendingTransition(0, 0)
                }
            })
            circularReveal.start()
        }
    }
//
//    private fun revealActivity(x:Int,y:Int){
//        val finalRadius = Math.max(mView.width,mView.height) * 1.1f
//        val circularReveal = ViewAnimationUtils.createCircularReveal(mView,0,0,0f,finalRadius)
//        circularReveal.interpolator = AccelerateInterpolator()
//        circularReveal.setDuration(1000L)
//
//        circularReveal.addListener(object: Animator.AnimatorListener{
//            override fun onAnimationRepeat(p0: Animator?) {
//
//            }
//
//            override fun onAnimationEnd(p0: Animator?) {
//               Log.e("WOW","onAnimationEnd")
//                mView.visibility = View.INVISIBLE
//            }
//
//            override fun onAnimationCancel(p0: Animator?) {
//
//            }
//
//            override fun onAnimationStart(p0: Animator?) {
//                Log.e("WOW","onAnimationStart")
//            }
//
//        })
//        mView.visibility = View.VISIBLE
//        circularReveal.start()
//    }
}