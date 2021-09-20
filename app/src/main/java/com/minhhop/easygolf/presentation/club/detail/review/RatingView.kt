package com.minhhop.easygolf.presentation.club.detail.review

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import java.math.RoundingMode
import java.text.DecimalFormat


class RatingView : LinearLayout {
    companion object{
        private const val MAX_RATING = 5

        fun roundRate(rate:Double):String{
            return if((rate - rate.toInt()) <= 0) rate.toInt().toString()
            else {
                val df = DecimalFormat("#.#")
                df.roundingMode = RoundingMode.HALF_UP
                df.format(rate)
            }
        }
    }
    enum class TypeView{
        TEXT,
        ICON
    }
    private var mTypeView = TypeView.ICON
    private var mRate:Int = 0
    private var mRatingViewListener:RatingViewListener? = null
    private var mNormalMargin = 0
    private var mSmallMargin = 0
    private var mSizeIcon = 0
    constructor(context: Context?) : super(context) { initView(null) }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView(attrs) }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(attrs) }

    private fun initView(attrs: AttributeSet?){
        mNormalMargin = context.resources.getDimension(R.dimen.normal_margin).toInt()
        mSmallMargin = context.resources.getDimension(R.dimen.small_margin).toInt()
        mSizeIcon = context.resources.getDimension(R.dimen.size_default_star_rating).toInt()
        orientation = HORIZONTAL

        attrs?.let { attributeSet->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.RatingView)
            mTypeView = if(typedArray.getInt(R.styleable.RatingView_format,0) == 0){
                TypeView.TEXT
            }else{
                TypeView.ICON
            }
            typedArray.recycle()

        }
        if(mTypeView == TypeView.TEXT){
            for (i in 0 until MAX_RATING){
                createItemStarViewWithText(i)
            }
        }
        else{
            for (i in 0 until MAX_RATING){
                createItemStarViewWithIcon(i)
            }
        }
        unSelectRating()
    }

    private fun createItemStarViewWithText(index:Int){

        val itemRatingView = MaterialTextView(context)
        itemRatingView.text = (index + 1).toString()
        itemRatingView.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.normal_font_size))
        TextViewCompat.setTextAppearance(itemRatingView,R.style.normal)
        itemRatingView.gravity = Gravity.CENTER
        itemRatingView.setPadding(mNormalMargin,mSmallMargin,mNormalMargin,mSmallMargin)
        itemRatingView.compoundDrawablePadding = mSmallMargin/2
        itemRatingView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_icon_star,0)
        addView(itemRatingView)
        if(index > 0){
            (itemRatingView.layoutParams as? MarginLayoutParams)?.marginStart = mNormalMargin
        }
        itemRatingView.background = ContextCompat.getDrawable(context,R.drawable.background_view_rating_normal)


        itemRatingView.setOnClickListener {
            mRatingViewListener?.onSelectRating(index + 1)
        }
    }

    private fun createItemStarViewWithIcon(index:Int){

        val itemRatingView = ImageView(context)
        itemRatingView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_star))
        ImageViewCompat.setImageTintList(itemRatingView,ColorStateList.valueOf(ContextCompat.getColor(context,R.color.textColorGray)))
        itemRatingView.adjustViewBounds = true
        addView(itemRatingView,mSizeIcon,mSizeIcon)
        if(index > 0){
            (itemRatingView.layoutParams as? MarginLayoutParams)?.marginStart = mNormalMargin
        }
        itemRatingView.setOnClickListener {
            unSelectRating(index )
            mRate = index + 1
        }
    }

    fun setRate(rate:Int){
        mRate = rate
        unSelectRating(rate - 1)
    }

    fun getRate() = mRate

    private fun unSelectRating(selectIndex: Int = -1){
        for (index in 0 until childCount){
            if(mTypeView == TypeView.TEXT){
                (getChildAt(index) as? MaterialTextView)?.let { itemRatingView->
                    val colorEnable = if(index != selectIndex) {
                        itemRatingView.background = ContextCompat.getDrawable(context,R.drawable.background_view_rating_normal)
                        ContextCompat.getColor(context, R.color.colorEnable)
                    }else{
                        itemRatingView.background = ContextCompat.getDrawable(context,R.drawable.background_view_rating_enable)
                        ContextCompat.getColor(context, R.color.colorWhite)
                    }
                    itemRatingView.setTextColor(colorEnable)
                    TextViewCompat.setCompoundDrawableTintList(itemRatingView,
                            ColorStateList.valueOf(colorEnable))
                }
            }else{
                (getChildAt(index) as? ImageView)?.let { itemRatingView->
                    val colorEnable = if(index <= selectIndex) {
                        ContextCompat.getColor(context, R.color.colorEnable)
                    }else{
                        ContextCompat.getColor(context, R.color.colorStarDisEnable)
                    }
                    ImageViewCompat.setImageTintList(itemRatingView,ColorStateList.valueOf(colorEnable))
                }
            }

        }
    }

    fun addListener(ratingViewListener:RatingViewListener){
        this.mRatingViewListener = ratingViewListener
    }

    interface RatingViewListener{
        fun onSelectRating(rate:Int)
    }
}