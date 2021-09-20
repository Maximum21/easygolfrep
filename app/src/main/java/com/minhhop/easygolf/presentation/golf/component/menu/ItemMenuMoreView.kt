package com.minhhop.easygolf.presentation.golf.component.menu

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R

class ItemMenuMoreView : LinearLayout {
    private var mIcon:ImageView? = null
    private var mTextDescription:MaterialTextView? = null
    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView(){
        gravity = Gravity.CENTER_VERTICAL
        orientation = HORIZONTAL
        val y: Int = context.resources.getDimension(R.dimen.normal_margin).toInt()
        setPadding(0, y/2, 0, y/2)

        mIcon = ImageView(context)
        mIcon?.adjustViewBounds = true
        mIcon?.layoutParams = LayoutParams(context.resources.getDimension(R.dimen.d_20).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)

        addView(mIcon)

        mTextDescription = MaterialTextView(context)
        mTextDescription?.apply {
            val paddingLeft: Int = context.resources.getDimension(R.dimen.d_5).toInt()
            setPadding(paddingLeft, 0, 0, 0)
            setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.font_menu))
            TextViewCompat.setTextAppearance(this,R.style.normal)
        }
        addView(mTextDescription)
    }

    fun setIcon(@DrawableRes resIcon:Int){
        mIcon?.setImageResource(resIcon)
    }

    fun setText(value:String?){
        mTextDescription?.text = value
    }
}