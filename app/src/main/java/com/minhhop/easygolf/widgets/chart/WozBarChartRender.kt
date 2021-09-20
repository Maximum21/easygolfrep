package com.minhhop.easygolf.widgets.chart

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.minhhop.easygolf.R
import com.minhhop.easygolf.utils.AppUtil


class WozBarChartRender(chart: BarDataProvider,animator: ChartAnimator,
                        viewPortHandler: ViewPortHandler,imageList: ArrayList<Bitmap>,context: Context) :
        BarChartRenderer(chart,animator,viewPortHandler) {

    private val mContext: Context = context
    private val mImageList: ArrayList<Bitmap> = imageList



    override fun drawValues(c: Canvas) {
        val dataSets = mChart.barData.dataSets
        val valueOffsetPlus = AppUtil.convertDpToPixel(22,mContext)
        var negOffset: Float

        for (i in 0 until mChart.barData.dataSetCount) {

            val dataSet = dataSets[i]
            applyValueTextStyle(dataSet)
            val valueTextHeight = 20f
            negOffset = valueTextHeight + valueOffsetPlus

            val buffer = mBarBuffers[i]

            var left: Float
            var right: Float
            var top: Float
            var bottom: Float

            var j = 0
            while (j < buffer.buffer.size * mAnimator.phaseX) {

                left = buffer.buffer[j]
                right = buffer.buffer[j + 2]
                top = buffer.buffer[j + 1]
                bottom = buffer.buffer[j + 3]

                val x = (left + right) / 2f

                if (!mViewPortHandler.isInBoundsRight(x))
                    break

                if (!mViewPortHandler.isInBoundsY(top) || !mViewPortHandler.isInBoundsLeft(x)) {
                    j += 4
                    continue
                }

                val entry = dataSet.getEntryForIndex(j / 4)
                val valz = entry.y
                mValuePaint.textAlign = Paint.Align.CENTER
                if (valz > 0) {

                    drawValue(c,dataSet.valueFormatter.toString(),i.toFloat(),x, dataSet.getValueTextColor(j / 4))
                }

                val bitmap = mImageList[j / 4]

                if (bitmap != null) {
                    val scaledBitmap = getScaledBitmap(bitmap)
                    c.drawBitmap(scaledBitmap, x - scaledBitmap.width / 2f, bottom + 0.5f * negOffset - scaledBitmap.width / 2f, null)
                }
                j += 4
            }
        }
    }


    private fun getScaledBitmap(bitmap: Bitmap): Bitmap {
        val width = mContext.getResources().getDimension(R.dimen.d_20).toInt()
        val height = mContext.getResources().getDimension(R.dimen.d_20).toInt()
        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

}