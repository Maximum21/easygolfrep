package com.minhhop.easygolf.presentation.custom

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.EasyGolfBounceInterpolator
import com.minhhop.easygolf.presentation.golf.GoogleMapsHelper
import kotlinx.android.synthetic.main.view_distance_golf.view.*

class DistanceGolfView : LinearLayout {

    private var mValue:Int = 0
    private var mEAniStar:Animation? = null
    private var mAnimator:ValueAnimator? = null
    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.view_distance_golf,this)
        mEAniStar = AnimationUtils.loadAnimation(context, R.anim.animation_show_view_distance)
        val interpolator = EasyGolfBounceInterpolator(0.2, 10.0)
        mEAniStar?.interpolator = interpolator

    }

    fun setValue(value: Int,forValue:Boolean = true){
        mTxtDistance?.text = value.toString()
        if(forValue)
        mValue = value
    }

    fun getValue(): Int = mValue

    fun changeUnit(unit:GoogleMapsHelper.UnitGolf){

        mTxtUnit?.text = if(unit == GoogleMapsHelper.UnitGolf.YARD){
            context.getString(R.string.yard_shortcut)
        }else{
            context.getString(R.string.meter_shortcut)
        }
    }

    fun show(){
        if(this.visibility != View.VISIBLE) {
            visibility = View.VISIBLE
            startAnimation(this.mEAniStar)
            startCountAnimation()
        }
    }

    fun hide(){
        mEAniStar?.cancel()
        mAnimator?.removeAllUpdateListeners()
        mAnimator?.cancel()
        this.animation?.cancel()
        visibility = View.GONE
    }
    /**
     * Animation make count
     *
     */

    private fun startCountAnimation() {
        mAnimator = ValueAnimator.ofInt(0,mValue)
        mAnimator?.duration = mEAniStar?.duration?:700
        mAnimator?.addUpdateListener {
            setValue(it.animatedValue as Int,false)
        }
        mAnimator?.addListener(object : Animator.AnimatorListener{
            override fun onAnimationRepeat(p0: Animator?) {

            }

            override fun onAnimationEnd(p0: Animator?) {
            }

            override fun onAnimationCancel(p0: Animator?) {
                visibility = View.GONE
            }

            override fun onAnimationStart(p0: Animator?) {

            }

        })
        mAnimator?.start()
    }
}