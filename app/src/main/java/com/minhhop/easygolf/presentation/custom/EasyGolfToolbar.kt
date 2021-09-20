package com.minhhop.easygolf.presentation.custom

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.minhhop.easygolf.R

class EasyGolfToolbar : MaterialToolbar {

    private var mEventToolbar:EventToolbar? = null
    private var mRegisterDoubleTap = false
    constructor(context: Context) : super(context){ initView(null) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initView(attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ){initView(attrs)}

    private fun initView(attrs: AttributeSet?){
        contentInsetStartWithNavigation = 0
        setTitleTextAppearance(context,R.style.bold)

        attrs?.let { attributeSet->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyGolfToolbar)
            if(typedArray.getBoolean(R.styleable.EasyGolfToolbar_isLight,true)){
                setTitleTextColor(ContextCompat.getColor(context,R.color.colorWhite))
                setSubtitleTextColor(ContextCompat.getColor(context,R.color.colorWhite))
                setNavigationIcon(R.drawable.ic_icon_back)
            }else{
                setTitleTextColor(ContextCompat.getColor(context,R.color.textColorDark))
                setSubtitleTextColor(ContextCompat.getColor(context,R.color.textColorGray))
                setNavigationIcon(R.drawable.ic_icon_back_dark)
            }
            mRegisterDoubleTap = typedArray.getBoolean(R.styleable.EasyGolfToolbar_isDoubleTap,false)
            typedArray.recycle()
        }
        setNavigationOnClickListener {
            mEventToolbar?.onClickBack(mRegisterDoubleTap)
        }
    }

    fun addEventBack(eventToolbar: EventToolbar){
        mEventToolbar = eventToolbar
    }

    interface EventToolbar{
        fun onClickBack(isDoubleTap:Boolean)
    }
}