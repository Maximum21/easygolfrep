package com.minhhop.easygolf.presentation.custom.countdown

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R

class EasyGolfCountDown : LinearLayout {

    private lateinit var mTextTitle:MaterialTextView
    private lateinit var mTextValueCountDown:MaterialTextView

    constructor(context: Context?) : super(context) { initView(null) }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(attrs) }


    private fun initView(attrs: AttributeSet?){
        orientation = HORIZONTAL
        mTextTitle = MaterialTextView(context)
        TextViewCompat.setTextAppearance(mTextTitle,R.style.normal)

        mTextTitle.setTextColor(ContextCompat.getColor(context, R.color.colorTitleCountDown))
        mTextTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.limit_small_font_size))

        mTextValueCountDown = MaterialTextView(context)
        TextViewCompat.setTextAppearance(mTextValueCountDown,R.style.bold)
        mTextValueCountDown.text = context.getString(R.string.zero)
        mTextValueCountDown.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
        mTextValueCountDown.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.large_font_size))

        val layoutParamsTitle = ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT
        )
        addView(mTextTitle,layoutParamsTitle)

        addView(mTextValueCountDown)
        mTextValueCountDown.layoutParams.apply {
                    (this as? MarginLayoutParams)?.marginStart = context.resources.getDimension(R.dimen.small_margin).toInt()
                }

        attrs?.let { attributeSet->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyGolfCountDown)
            mTextTitle.text = typedArray.getString(R.styleable.EasyGolfCountDown_titleCountDown)
            typedArray.recycle()
        }
    }

    fun setValueCountDown(value:Int?){
        mTextValueCountDown.text = value?.toString()
    }

    fun setValueCountDown(value:Double?){
        mTextValueCountDown.text = value?.toString()
    }
}