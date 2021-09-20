package com.minhhop.easygolf.widgets

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R

class WozItemPutt : LinearLayout{

    private var mTxtCenter:TextView? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.circle_single_point)
        mTxtCenter = TextView(context)

        addView(mTxtCenter)
        mTxtCenter?.apply {
            gravity = Gravity.CENTER
            setTextColor(ContextCompat.getColor(context,R.color.color_point_score))
            textAlignment = TextView.TEXT_ALIGNMENT_CENTER
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            requestLayout()
        }
    }

    fun setValueText(value:String){
        mTxtCenter!!.text = value
    }

    fun registerBigBrother(){
        setBackgroundResource(R.drawable.circle_single_big_brother_point)
        mTxtCenter?.apply {
            setTextColor(ContextCompat.getColor(context,R.color.colorLinker))
        }
    }
}
