package com.minhhop.easygolf.widgets.spotlight.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF

class RoundedRectangle(width:Float,height:Float,radius:Float) : Shape {

    private var mWidth = width
    private var mHeight = height
    private var mRadius = radius

    override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint) {
        val halfWidth = mWidth/2 * value
        val halfHeight = mHeight/2 * value

        val left = point.x - halfWidth
        val right = point.x + halfWidth

        val top = point.y - halfHeight
        val bottom = point.y + halfHeight

        val rect = RectF(left, top, right, bottom)
        canvas.drawRoundRect(rect,mRadius,mRadius,paint)
    }

}