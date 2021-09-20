package com.minhhop.easygolf.widgets.spotlight.shape

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF

class Circle(radius:Float) : Shape {

    private var mRadius = radius
    override fun draw(canvas: Canvas, point: PointF, value: Float, paint: Paint) {
        canvas.drawCircle(point.x,point.y,value * mRadius,paint)
    }
}