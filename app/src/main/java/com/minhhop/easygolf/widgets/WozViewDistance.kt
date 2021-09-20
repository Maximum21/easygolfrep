package com.minhhop.easygolf.widgets

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.widgets.WozViewDistance.POSITION.*
import kotlin.math.roundToInt

class WozViewDistance : LinearLayout {
    enum class POSITION{
        BLACK,
        CENTER,
        FRONT
    }
    private var mMainPadding = 0
    private var mEventWozDistance:EventWozDistance? = null

    private var mDistanceBackView:MaterialTextView? = null
    private var mDistanceCenterView:MaterialTextView? = null
    private var mDistanceFrontView:MaterialTextView? = null


    constructor(context: Context?) : super(context) { initView() }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView(){
        orientation = VERTICAL
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.background_holder_distance_green)
        mMainPadding = context.resources.getDimension(R.dimen.small_padding).toInt()
        setPadding(mMainPadding,0,mMainPadding/2,mMainPadding)

        mDistanceBackView = createValueDistanceView(BLACK)
        mDistanceCenterView = createValueDistanceView(CENTER)
        mDistanceFrontView = createValueDistanceView(FRONT)

        val containerBackView = createItemDistance(mDistanceBackView!!,BLACK)
        val containerCenterView = createItemDistance(mDistanceCenterView!!,CENTER)
        val containerFrontView = createItemDistance(mDistanceFrontView!!,FRONT)
        addView(containerBackView)
        addView(containerCenterView)
        addView(containerFrontView)

        containerBackView.setOnClickListener {
            updateSelected(BLACK)
            mEventWozDistance?.onSelected(BLACK)
        }
        containerCenterView.setOnClickListener {
            updateSelected(CENTER)
            mEventWozDistance?.onSelected(CENTER)
        }
        containerFrontView.setOnClickListener {
            updateSelected(FRONT)
            mEventWozDistance?.onSelected(FRONT)
        }
        updateSelected(CENTER)
    }

    private fun createItemDistance(valuePosition:View,position: POSITION):View{
        val container = LinearLayout(context)
        container.orientation = VERTICAL
        val titlePosition = MaterialTextView(context)
        TextViewCompat.setTextAppearance(titlePosition,R.style.bold)
        titlePosition.setTextSize(TypedValue.COMPLEX_UNIT_PX,context.resources.getDimension(R.dimen.limit_small_font_size))
        titlePosition.gravity = Gravity.CENTER

        when(position){
            BLACK -> {
                titlePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_blue))
                titlePosition.text = context.getString(R.string.back_green)

                container.addView(titlePosition)
                (titlePosition.layoutParams as? MarginLayoutParams)?.topMargin = mMainPadding
                container.addView(valuePosition)
            }
            CENTER -> {
                titlePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_yellow))
                titlePosition.text = context.getString(R.string.center_green)

                container.addView(valuePosition)
                (valuePosition.layoutParams as? MarginLayoutParams)?.topMargin = mMainPadding
                container.addView(titlePosition)
            }
            FRONT -> {
                titlePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_red))
                titlePosition.text = context.getString(R.string.front_green)

                container.addView(valuePosition)
                (valuePosition.layoutParams as? MarginLayoutParams)?.topMargin = mMainPadding
                container.addView(titlePosition)
            }
        }

        return container
    }

    private fun createValueDistanceView(position: POSITION):MaterialTextView{
        val valuePosition = MaterialTextView(context)
        valuePosition.gravity = Gravity.CENTER
        when(position){
            BLACK -> {
                valuePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_blue))
            }
            CENTER -> {
                valuePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_yellow))
            }
            FRONT -> {
                valuePosition.setTextColor(ContextCompat.getColor(context,R.color.color_flag_red ))
            }
        }
        return valuePosition
    }

    fun setEvent(event:EventWozDistance){
        mEventWozDistance = event
    }

    private fun updateSelected(position: POSITION){
        when(position){
            BLACK -> {
                updateStyleView(mDistanceBackView,true)
                updateStyleView(mDistanceCenterView)
                updateStyleView(mDistanceFrontView)
            }
            CENTER -> {
                updateStyleView(mDistanceBackView)
                updateStyleView(mDistanceCenterView,true)
                updateStyleView(mDistanceFrontView)
            }
            FRONT -> {
                updateStyleView(mDistanceBackView)
                updateStyleView(mDistanceCenterView)
                updateStyleView(mDistanceFrontView,true)
            }
        }
    }
    private fun updateStyleView(textView: MaterialTextView?,isSelected:Boolean = false){
        if(isSelected){
            textView?.let {
                TextViewCompat.setTextAppearance(it,R.style.bold)
                it.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.huge_font_size)
                )
            }
        }else{
            textView?.let {
                TextViewCompat.setTextAppearance(it,R.style.normal)
                it.setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        context.resources.getDimension(R.dimen.large_font_size)
                )
            }
        }
    }

    fun setValue(value : Double,position: POSITION){
        when(position){
            BLACK -> {
                mDistanceBackView?.text = value.roundToInt().toString()
            }
            CENTER -> {
                mDistanceCenterView?.text = value.roundToInt().toString()
            }
            FRONT -> {
                mDistanceFrontView?.text = value.roundToInt().toString()
            }
        }
    }

    interface EventWozDistance {
        fun onSelected(pos: POSITION)
    }
}