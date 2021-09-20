package com.minhhop.easygolf.presentation.chart

import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfChartView
import kotlin.math.roundToInt

open class HalfCircleChartView : EasyGolfChartView {
    override val mTypeChart: TypeChart
        get() = TypeChart.HALF_CIRCLE
    private val mStrokeWithDiver = 3f
    private var mColorHighLightChart = 0
    private var mColorChart = 0
    private val mPaintBackground = Paint(Paint.ANTI_ALIAS_FLAG)

    private lateinit var mContainerTitle: LinearLayout
    private var mValueTitleLeft: MaterialTextView? = null
    private var mValueTitleCenter: MaterialTextView? = null
    private var mValueTitleRight: MaterialTextView? = null

    private var mPercentCenter = 0f
    private var mValueLeft = 15f
    private var mValueCenter = 35f
    private var mValueRight = 80f

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
        mColorHighLightChart = ContextCompat.getColor(context, R.color.color_chart_high_light)
        mColorChart = ContextCompat.getColor(context, R.color.color_chart)
        mPaintBackground.apply {
            isAntiAlias = true
            color = ContextCompat.getColor(context, R.color.colorDiver)
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        mContainerTitle = LinearLayout(context)
        mContainerTitle.setPadding(0, mSmallMargin, 0, 0)
        mContainerTitle.orientation = LinearLayout.HORIZONTAL
        addView(mContainerTitle, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        (mContainerTitle.layoutParams as? LayoutParams)?.gravity = Gravity.BOTTOM or Gravity.CENTER
        mValueTitleLeft = buildTitleView(context.getString(R.string.left), null)
        mValueTitleCenter = buildTitleView(context.getString(R.string.fairway_hit), null, true)
        mValueTitleRight = buildTitleView(context.getString(R.string.right), null)
    }

    override fun updateAnimationCallback(value: Float) {
        mPercentCenter = value
    }

    override fun endAnimationCallback(key: Int?) {

    }

    fun setValue(left:Int?,center:Int?,right:Int?){
        mValueLeft = left?.toFloat()?:0f
        mValueCenter = center?.toFloat()?:0f
        mValueRight = right?.toFloat()?:0f
        Log.e("WOW","(percentCenter() * 100): ${(percentCenter() * 100)}")
        mValueTitleLeft?.text = context?.getString(
                R.string.percent,(percentLeft() * 100).roundToInt().toString()
        )
        mValueTitleCenter?.text = context?.getString(
                R.string.percent,(percentCenter() * 100).roundToInt().toString()
        )
        mValueTitleRight?.text = context?.getString(
                R.string.percent,(percentRight() * 100).roundToInt().toString()
        )
    }
    fun startAnimationChart(){
        val propertyDrawCircle = PropertyValuesHolder.ofFloat(ANIMATION_CHART, 0f, percentCenter() * 180)
        onStartAnimation(propertyDrawCircle)
    }

    private fun buildTitleView(title: String, value: String?, isCenter: Boolean = false) :MaterialTextView {
        val containerTitleView = LinearLayout(context)
        containerTitleView.gravity = Gravity.CENTER or Gravity.BOTTOM
        containerTitleView.orientation = LinearLayout.VERTICAL

        val headerView = MaterialTextView(context)
        headerView.text = title
        headerView.gravity = Gravity.CENTER
        headerView.setTextColor(ContextCompat.getColor(context, R.color.textColorDark))
        TextViewCompat.setTextAppearance(headerView, R.style.normal)
        headerView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.normal_font_size))
        containerTitleView.addView(headerView)

        val valueView = MaterialTextView(context)
        valueView.text = value
        valueView.gravity = Gravity.CENTER
        valueView.setTextColor(ContextCompat.getColor(context, R.color.textColorDark))
        TextViewCompat.setTextAppearance(valueView, R.style.bold)
        valueView.setTextSize(
                TypedValue.COMPLEX_UNIT_PX,
                if (isCenter) {
                    context.resources.getDimension(R.dimen.x_normal_font_size)
                } else {
                    context.resources.getDimension(R.dimen.x_small_font_size)
                }
        )
        containerTitleView.addView(valueView)

        mContainerTitle.addView(containerTitleView, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)

        (containerTitleView.layoutParams as? LinearLayout.LayoutParams)?.let { layoutParams ->
            layoutParams.weight = 0.3f
            containerTitleView.requestLayout()
        }
        return valueView
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        invalidate()
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        c?.let { canvas ->
            val widthBackgroundChart = width
            val heightChart = (height.toFloat() - mContainerTitle.height)
            val radiusBackgroundChart = Math.min(
                    widthBackgroundChart / 2f,
                    heightChart
            ) - mSmallMargin * 4
            val marginChart = (width - (radiusBackgroundChart * 2f))/2
            /**
             * draw highlight chart
             * */
            mPaint.color = mColorHighLightChart
            val radiusHighLightChart = radiusBackgroundChart * 0.8f
            val startHighLightChart = (marginChart + radiusBackgroundChart - radiusHighLightChart)
            val topHighLightChart = heightChart - radiusHighLightChart
            val bottomHighLightChart = topHighLightChart + radiusHighLightChart * 2
            canvas.drawArc(
                    startHighLightChart, topHighLightChart, startHighLightChart + radiusHighLightChart * 2f,
                    bottomHighLightChart,
                    (percentLeft() + 1) * 180, mPercentCenter, true, mPaint
            )

            /**
             * draw  chart
             * */
            mPaint.color = mColorChart
            val radiusChart = radiusBackgroundChart  * 0.65f
            val startChart = (marginChart + radiusBackgroundChart - radiusChart)
            val topChart = heightChart - radiusChart
            val bottomChart = topChart + radiusChart * 2
            canvas.drawArc(
                    startChart, topChart, startChart + radiusChart * 2f,
                    bottomChart,
                     180f, 180f, true, mPaint
            )

            /**
             * draw background chart
             * */
            val topPos = Math.abs(heightChart - radiusBackgroundChart)
            val bottomPosRadius = topPos + radiusBackgroundChart * 2
            val bottomPos = topPos + radiusBackgroundChart
            mPaintBackground.strokeWidth = mStrokeWithDiver
            canvas.drawArc(
                    marginChart, topPos, marginChart + radiusBackgroundChart * 2f,
                    bottomPosRadius,
                    180f, 180f, true, mPaintBackground
            )
            canvas.drawLine(0f, bottomPos, marginChart,
                    bottomPos, mPaintBackground)
            canvas.drawLine(marginChart + radiusBackgroundChart * 2f, bottomPos ,
                    width.toFloat(),
                    bottomPos, mPaintBackground)

        }

    }

    override fun interpolatorDefaultAnimation(): TimeInterpolator = BounceInterpolator()
    private fun percentLeft():Float{
       return if(totalPercent() == 0f){
            0f
        }else mValueLeft/totalPercent()
    }
    private fun percentCenter():Float {
        return if(totalPercent() == 0f){
            0f
        }else mValueCenter/totalPercent()
    }
    private fun percentRight():Float{
        if(percentLeft() == 0f && percentCenter() == 0f){
            return 0f
        }
        return 1f - percentLeft() - percentCenter()
    }
    override fun totalPercent(): Float = (mValueLeft + mValueCenter + mValueRight)

}