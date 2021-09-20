package com.minhhop.easygolf.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.minhhop.easygolf.R

class WozItemPickerPoint(context: Context) : FrameLayout(context) {
    private var mTxtValue:TextView? = null

    init {
        mTxtValue = TextView(context)
        mTxtValue?.apply {
            setTextColor(Color.parseColor("#20334D"))
            gravity = Gravity.CENTER
            setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimension(R.dimen.font_size_point))
            typeface = Typeface.DEFAULT_BOLD
        }
        addView(mTxtValue)
        val params = mTxtValue!!.layoutParams as LayoutParams
        params.gravity = Gravity.CENTER

    }

    fun setBackgroundItem(@DrawableRes idRes: Int){
        setBackgroundResource(idRes)
    }

    fun setValue(value:String,isMax:Boolean = false){
        mTxtValue?.text = value
        if(isMax){
            mTxtValue?.rotation = 45f
            mTxtValue?.setTextSize(TypedValue.COMPLEX_UNIT_PX,resources.getDimension(R.dimen.font_size_max_point))
        }
    }

    fun getValue():String{
        return mTxtValue!!.text.toString()
    }
}