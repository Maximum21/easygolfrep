package com.minhhop.easygolf.widgets.spotlight

import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.widgets.spotlight.listener.AbstractAnimatorListener
import com.minhhop.easygolf.widgets.spotlight.listener.OnSpotLightListener
import com.minhhop.easygolf.widgets.spotlight.target.Target

@SuppressLint("ViewConstructor")
class SpotLightView(context: Context, @ColorRes overlayColor: Int, listener: OnSpotLightListener) : FrameLayout(context) {

    private var mPaint = Paint()
    private var mSpotPaint = Paint()
    private var mAnimator:ValueAnimator? = null
    private var mCurrentTarget: Target? = null
    @ColorRes private var mOverlayColor:Int = overlayColor

    init {
        bringToFront()
        setWillNotDraw(false)
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        mSpotPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        setOnClickListener {

            mAnimator?.apply {
                if(isRunning && animatedValue as Float > 0){
                    listener.onSpotlightViewClicked()
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint.color = ContextCompat.getColor(context,mOverlayColor)
        canvas?.apply {
            drawRect(0f,0f,width.toFloat(),height.toFloat(),mPaint)

            if (mAnimator != null && mCurrentTarget != null) {
                mCurrentTarget!!.shape
                        .draw(this, mCurrentTarget!!.point, mAnimator!!.animatedValue as Float, mSpotPaint)
            }
        }
    }

    fun startSpotlight(duration: Long, animation: TimeInterpolator,
                       listener: AbstractAnimatorListener) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        objectAnimator.duration = duration
        objectAnimator.interpolator = animation
        objectAnimator.addListener(listener)
        objectAnimator.start()
    }

    fun finishSpotlight(duration: Long, animation: TimeInterpolator,
                        listener: AbstractAnimatorListener) {
        val objectAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        objectAnimator.duration = duration
        objectAnimator.interpolator = animation
        objectAnimator.addListener(listener)
        objectAnimator.start()
    }

    fun turnUp(target: Target, listener: AbstractAnimatorListener) {
        mCurrentTarget = target

        mAnimator = ValueAnimator.ofFloat(0f, 1f)
        mAnimator?.apply {
            addUpdateListener {
                this@SpotLightView.invalidate()
            }
            interpolator = target.animation
            duration = target.duration
            addListener(listener)
            start()
        }


    }

    fun turnDown(listener: AbstractAnimatorListener) {
        if (mCurrentTarget == null) {
            return
        }

        mAnimator = ValueAnimator.ofFloat(1f, 0f)
        mAnimator?.apply {
            addUpdateListener {
                this@SpotLightView.invalidate()
            }
            addListener(listener)
            interpolator = mCurrentTarget!!.animation
            duration = mCurrentTarget!!.duration
            start()
        }

    }
}
