package com.minhhop.easygolf.presentation.chart

import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.DecelerateInterpolator
import com.minhhop.easygolf.framework.base.EasyGolfChartView
import com.minhhop.easygolf.framework.models.EasyGolfChartData
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.tan

class CircleChartView : EasyGolfChartView {
    override val mTypeChart: TypeChart
        get() = TypeChart.CIRCLE

    companion object {
        private const val SIZE_DESCRIPTION = 20f
    }

    private val mPaintLine = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mPaintText = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mStartBeforeAngle = 90f
    private var mStartAngle = 90f
    private var mPercentAngle: Float = 0f
    private var mIndex: Int = -1
    var mListCircleView = ArrayList<EasyGolfChartData>()

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
        mPaintLine.apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3f
        }
        mPaintText.apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
            textSize = 40f
        }
    }

    fun setData(vararg listData: EasyGolfChartData) {
        mListCircleView.clear()
        mListCircleView.addAll(listData)
        mIndex = mListCircleView.size - 1
    }

    private fun resetAnimationChart(){
        getEasyGolfData()?.let { easyGolfChartData ->
            val percent = easyGolfChartData.value / totalPercent()
            val propertyDrawCircle = PropertyValuesHolder.ofFloat(ANIMATION_CHART,
                    0f, percent * 360)
            onStartAnimation(propertyDrawCircle, mIndex + 1)
        }
    }

    fun startAnimationChart() {
        mIndex = 0
        mStartAngle = 90f
        resetAnimationChart()
    }

    private fun getStartAngleAt(index: Int): Float {
        return if (index >= 0 && index < mListCircleView.size) {
            var result = 90f
            for (i in 0 until index) {
                result += getPercentAt(i) * 360f
            }
            result
        } else {
            90f
        }
    }

    private fun getPercentAt(index: Int): Float {
        return if (index >= 0) {
            if (index >= mListCircleView.size) {
                1f - (mListCircleView[index].value / totalPercent())
            } else {
                (mListCircleView[index].value / totalPercent())
            }
        } else {
            0f
        }
    }

    private fun getEasyGolfData(): EasyGolfChartData? {
        return if (mIndex >= 0 && mIndex < mListCircleView.size) {
            mListCircleView[mIndex]
        } else {
            null
        }
    }

    override fun updateAnimationCallback(value: Float) {
        mPercentAngle = value
    }

    override fun endAnimationCallback(key: Int?) {
        mIndex++
        mStartBeforeAngle = mStartAngle
        mStartAngle += mPercentAngle
        resetAnimationChart()
    }

    override fun totalPercent(): Float {
        var total = 0f
        mListCircleView.forEach {
            total += it.value
        }
        return if (total <= 0) 1f else total
    }

    override fun onDraw(c: Canvas?) {
        super.onDraw(c)
        c?.let { canvas ->
            val mainRadius = width.coerceAtMost(height) / 3f
            val radiusCenter = mainRadius / 2f

            buildCircleView(canvas, mainRadius, radiusCenter)
            mPaint.color = Color.WHITE
            canvas.drawCircle(width / 2f, height / 2f, radiusCenter, mPaint)
        }
    }

    private fun buildCircleView(canvas: Canvas, radius: Float, radiusCenter: Float) {
        val start = (width / 2f) - radius
        val end = start + (radius * 2)
        val top = (height / 2f) - radius
        val bottom = top + (radius * 2)

        for (index in 0 until mIndex) {
            mListCircleView[index].let { data ->
                mPaint.color = data.color
                val sweepAngle = getPercentAt(index) * 360
                val startAngle = getStartAngleAt(index)

                val realAngle = (startAngle + (sweepAngle / 2))
                val alpha = if (realAngle >= 360) {
                    Math.toRadians(((sweepAngle / 2.0) - (360 - startAngle)))
                } else {
                    Math.toRadians(
                            if (realAngle >= 270) 1.0 * realAngle else -1.0 * realAngle)
                }
                val xV = (width / 2f) + (radius * 1.3f * cos(alpha).toFloat())
                val tempY = (height / 2f) + (radius * 1.3f * tan(alpha).toFloat())

                val yV = if (tempY > (height / 2f)) {
                    ((height / 2f) + (radius * 1.3f * tan(alpha).toFloat())).coerceAtMost(height - 50f)
                } else {
                    ((height / 2f) + (radius * 1.3f * tan(alpha).toFloat())).coerceAtLeast(50f)
                }

                canvas.drawArc(
                        start, top, end, bottom,
                        startAngle, sweepAngle, true, mPaint
                )
                if (mIndex >= mListCircleView.size && sweepAngle > 0) {
                    val offset = radiusCenter + (radius - radiusCenter) / 2f
                    val startLineX = if (startAngle in 90.0..180.0) {
                        (width / 2f) - offset
                    } else {
                        (width / 2f) + offset
                    }
                    val startLineY = (height / 2f) + Math.tan(alpha) * offset

                    canvas.drawLine(startLineX, startLineY.toFloat(),
                            xV, yV, mPaintLine
                    )

                    val endX = if (alpha >= 0) {
                        xV + offset / 2
                    } else {
                        xV - offset / 2
                    }
                    canvas.drawLine(xV, yV,
                            endX, yV, mPaintLine
                    )
                    mPaintText.textSize = 40f
                    if (alpha >= 0) {
                        canvas.drawText(
                                "${(getPercentAt(index) * 100).roundToInt()}%", endX + 20f, yV, mPaintText)
                    } else {
                        canvas.drawText(
                                "${(getPercentAt(index) * 100).roundToInt()}%", endX - 80f, yV, mPaintText)
                    }


                    if (index == mListCircleView.size - 1) {
                        /**
                         *  draw description
                         * */
                        mListCircleView.forEachIndexed { indexDes, dataDescription ->
                            val isDrawTop = yV > (height / 2)
                            val positionYDraw = if (isDrawTop) {
                                0f
                            } else {
                                /**
                                 * dont know this time
                                 * */
                               height - (SIZE_DESCRIPTION * 2 * mListCircleView.size)
                            }
                            mPaint.color = dataDescription.color
                            mPaintText.textSize = 30f
                            val posY = positionYDraw + (SIZE_DESCRIPTION * 1.3f * indexDes)
                            canvas.drawRect(endX, posY, endX + SIZE_DESCRIPTION,
                                    posY + SIZE_DESCRIPTION, mPaint)
                            canvas.drawText(dataDescription.description, endX + SIZE_DESCRIPTION * 2,
                                    posY + SIZE_DESCRIPTION, mPaintText)
                        }

                    }

                }
            }
        }
        getEasyGolfData()?.let { easyGolfChartData ->
            mPaint.color = easyGolfChartData.color
            canvas.drawArc(
                    start, top, end, bottom,
                    mStartAngle, mPercentAngle, true, mPaint
            )
        }
        /**
         * draw description
         * */

//        canvas.draw
    }

    override fun durationDefaultAnimation(): Long = 400L
    override fun interpolatorDefaultAnimation(): TimeInterpolator = DecelerateInterpolator()
}