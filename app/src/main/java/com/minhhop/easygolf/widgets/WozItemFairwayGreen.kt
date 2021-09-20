package com.minhhop.easygolf.widgets

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R

class WozItemFairwayGreen : LinearLayout{

    private lateinit var mImgCenter:ImageView

//    @DrawableRes
//    private var mRes:Int? = null

    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView() {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.circle_single_point)
        mImgCenter = ImageView(context)
        mImgCenter.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.textColorDark))
        addView(mImgCenter)
        mImgCenter.apply {
            layoutParams.width = resources.getDimension(R.dimen.size_item_center_fairway_green).toInt()
            layoutParams.height = resources.getDimension(R.dimen.size_item_center_fairway_green).toInt()
            requestLayout()
        }
    }

    fun setIcon(@DrawableRes res: Int){
//        mRes = res
        mImgCenter.setImageResource(res)
    }

    fun registerBigBrother(){
        mImgCenter.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorLinker))
        setBackgroundResource(R.drawable.circle_single_big_brother_point)
    }

}
