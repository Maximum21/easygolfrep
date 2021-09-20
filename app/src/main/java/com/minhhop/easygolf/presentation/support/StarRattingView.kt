package com.minhhop.easygolf.presentation.support

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R

class StarRattingView : LinearLayout {

    constructor(context: Context?) : super(context) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        orientation = HORIZONTAL
        for (i in 0..4) {
            val iE = ImageView(context)
            iE.setImageResource(R.drawable.baseline_star_rate_black_18)
            iE.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.color_star_un_enable))
            addView(iE)
        }
    }

    fun setRatting(value: Int) {
        var targetStar = value
        if (targetStar > MAX_POINT) {
            targetStar = MAX_POINT
        }
        for (i in 0 until childCount) {
            val iE = getChildAt(i) as ImageView
            if(i < targetStar) {
                iE.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                        R.color.color_star_enable))
            }else{
                iE.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                        R.color.color_star_un_enable))
            }
        }
    }

    companion object {
        private const val MAX_POINT = 10
    }
}