package com.minhhop.easygolf.presentation.golf.component.score.picker

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R

class EasyGolfPickerScoreView : FrameLayout{

    companion object{
        @DrawableRes
        fun getResResource(score:Int,par: Int): Int {
            return when (score - par) {
                in Int.MIN_VALUE..-2 -> {
                    R.drawable.circle_double_point
                }
                -1 -> {
                    R.drawable.circle_single_point
                }
                0 -> {
                    0
                }
                1 -> {
                    R.drawable.rectangle_single_point
                }
                else -> {
                    R.drawable.rectangle_double_point
                }
            }
        }

        fun getResResourceWhite(score:Int,par: Int): Int {
            return when (score - par) {
                in Int.MIN_VALUE..-2 -> {
                    R.drawable.circle_double_point_white
                }
                -1 -> {
                    R.drawable.circle_single_point_white
                }
                0 -> {
                    0
                }
                1 -> {
                    R.drawable.rectangle_single_point_white
                }
                else -> {
                    R.drawable.rectangle_double_point_white
                }
            }
        }
    }

    private var mPar:Int? = null
    private var mIsFinishShowView = false
    private lateinit var mContainerView:GridLayout
    private lateinit var mTxtBigBrother: MaterialTextView
    private var mListPositionItem = ArrayList<PointF>()
    private var mEasyGolfPickerScoreListener: EasyGolfPickerScoreListener? = null

    private var mSmallMargin = 0
    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        mSmallMargin = context.resources.getDimension(R.dimen.little_margin).toInt()
        setPadding(mSmallMargin,0,mSmallMargin,0)
        val sizeItem = context.resources.getDimension(R.dimen.size_player).toInt()
        mContainerView = GridLayout(context)
        mContainerView.columnCount = 5
        addView(mContainerView)
        for (i in 0..8) {
            val scoreView = createScoreView(i)
            mContainerView.addView(scoreView, sizeItem, sizeItem)
            (scoreView.layoutParams as? MarginLayoutParams)?.let { marginLayoutParams ->
                marginLayoutParams.marginStart = mSmallMargin
                marginLayoutParams.marginEnd = mSmallMargin
                marginLayoutParams.topMargin = mSmallMargin
            }
        }
        requestLayout()
        mTxtBigBrother = MaterialTextView(context)
        mTxtBigBrother.gravity = Gravity.CENTER
        mTxtBigBrother.setTextColor(ContextCompat.getColor(context,R.color.textColorDark))
        TextViewCompat.setTextAppearance(mTxtBigBrother,R.style.bold)
        mTxtBigBrother.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.huge_font_size))
        mTxtBigBrother.setBackgroundResource(R.drawable.circle_double_point)
        addView(mTxtBigBrother, (sizeItem * 1.3).toInt(), (sizeItem * 1.3).toInt())
        (mTxtBigBrother.layoutParams as? LayoutParams)?.gravity = Gravity.CENTER
        mTxtBigBrother.visibility = View.INVISIBLE

        mTxtBigBrother.setOnClickListener {
            resetToggle()
        }
    }

    private fun createScoreView(index: Int): View {
        val childView = if(index < 8){
            val scoreView = MaterialTextView(context)
            scoreView.gravity = Gravity.CENTER
            scoreView.setTextColor(ContextCompat.getColor(context,R.color.textColorDark))
            TextViewCompat.setTextAppearance(scoreView,R.style.bold)
            scoreView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.large_font_size))
            scoreView
        }else{
            val scoreView = ImageView(context)
            val padding = (context.resources.getDimension(R.dimen.size_radius_player)/2).toInt()
            scoreView.setPadding(padding,padding,padding,padding)
            scoreView.adjustViewBounds = true
            scoreView.scaleType = ImageView.ScaleType.CENTER_CROP
            scoreView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_more))
            scoreView.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.textColorDark))
            scoreView
        }

        childView.setOnClickListener {
            (it.tag as? String)?.let { tag->
               toggleChild(tag = tag)
            }?:mEasyGolfPickerScoreListener?.onClickMore()
        }
        return childView
    }

    fun setPar(par: Int?,value:Int? = null){
        mPar = par
        par?.let { parClear ->
            val startValue = parClear - 2
            val size = mContainerView.childCount
            for (index in 0 until size) {
                val childView = mContainerView.getChildAt(index)
                if (childView is MaterialTextView) {
                    childView.text = (startValue + index).toString()
                    childView.tag = (startValue + index).toString()
                }
                if(childView is MaterialTextView) {
                    childView.setBackgroundResource(getResResource(startValue + index, par))
                }
            }
           updateValue(value)
        }
    }

    fun updateValue(value:Int? = null){
        if(mIsFinishShowView){
            value?.let { valueClear ->
                toggleChild(false,valueClear.toString())
            }?:resetToggle()

        }else{
            val observer = mContainerView.viewTreeObserver
            observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    for (i in 0 until mContainerView.childCount) {
                        val childView = mContainerView.getChildAt(i)
                        mListPositionItem.add(PointF(childView.x, childView.y))
                    }
                    mIsFinishShowView = true
                    value?.let { valueClear ->
                        toggleChild(false,valueClear.toString())
                    }?:resetToggle()
                    mContainerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    private fun toggleChild(isAnimation: Boolean = true,tag: String?) {
        Log.e("WOW","toggleChild ${tag}")
        mTxtBigBrother.text = tag
        mTxtBigBrother.visibility = View.VISIBLE
        var isFoundItOnContainerView = false
        for (i in 0 until mContainerView.childCount) {
            val childView = mContainerView.getChildAt(i)
            (childView.tag as? String)?.let { tagChild->
                if(tag == tagChild){
                    mPar?.let { par->
                        try {
                            val score = tagChild.toInt()
                            mTxtBigBrother.setBackgroundResource(getResResource(score,par))
                        }catch (e:Exception){
                            e.printStackTrace()
                        }
                    }
                    isFoundItOnContainerView = true
                }
            }
            if(isAnimation) {
                childView.animate()
                        .alpha(0f)
                        .x(mTxtBigBrother.x)
                        .y(mTxtBigBrother.y)
                        .setDuration(200)
                        .start()
            }else{
                childView.alpha = 0f
                childView.x = mTxtBigBrother.x
                childView.y = mTxtBigBrother.y
            }
        }
        mTxtBigBrother.tag = tag
        if(!isFoundItOnContainerView){
            mPar?.let { par->
                mTxtBigBrother.setBackgroundResource(getResResource(Int.MAX_VALUE,par))
            }
        }
        mTxtBigBrother.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

    }

    fun getScore():Int?{
        return (mTxtBigBrother.tag?.toString()?.toInt())
    }

    private fun resetToggle() {
        mTxtBigBrother.tag = null
        mTxtBigBrother.visibility = View.INVISIBLE
        mTxtBigBrother.alpha = 0f
        for (i in 0 until mContainerView.childCount) {
            val childView = mContainerView.getChildAt(i)
            childView.visibility = View.VISIBLE
            childView.animate()
                    .alpha(1f)
                    .x(mListPositionItem[i].x)
                    .y(mListPositionItem[i].y)
                    .setDuration(200)
                    .start()
        }
    }

    fun setEventMoreScore(event: EasyGolfPickerScoreListener) {
        this.mEasyGolfPickerScoreListener = event
    }

    interface EasyGolfPickerScoreListener{
        fun onClickMore()
    }
}
