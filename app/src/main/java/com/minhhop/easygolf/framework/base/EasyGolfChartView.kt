package com.minhhop.easygolf.framework.base

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import com.minhhop.easygolf.R

abstract class EasyGolfChartView : FrameLayout{
    companion object{
        const val ANIMATION_CHART = "ANIMATION_CHART"
    }
    lateinit var mAnimator: ValueAnimator
    open val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    open var mSmallMargin = 0
    constructor(context: Context) : super(context){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView()
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView()
    }

    enum class TypeChart{
        HALF_CIRCLE,
        CIRCLE,
        COLUMN
    }
    abstract val mTypeChart:TypeChart

    private fun initView(){
        mSmallMargin = context.resources.getDimension(R.dimen.small_margin).toInt()
        mPaint.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL

        }
        mAnimator = ValueAnimator()
        mAnimator.interpolator = interpolatorDefaultAnimation()
        mAnimator.duration = durationDefaultAnimation()
        setWillNotDraw(false)
    }

    fun onStartAnimation(propertyAnimation: PropertyValuesHolder,key:Int? = null){
        mAnimator.removeAllListeners()
        mAnimator.setValues(propertyAnimation)
        mAnimator.addUpdateListener { animation ->
            val value = animation.getAnimatedValue(ANIMATION_CHART) as? Float ?: 0f
            updateAnimationCallback(value)
            invalidate()
        }
        mAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
                endAnimationCallback(key)
            }

            override fun onAnimationCancel(p0: Animator?) {

            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        mAnimator.start()
    }

    abstract fun updateAnimationCallback(value:Float)
    abstract fun endAnimationCallback(key:Int?)
    open fun totalPercent() = 1f
    open fun durationDefaultAnimation() = 1000L
    open fun interpolatorDefaultAnimation():TimeInterpolator = LinearInterpolator()
}