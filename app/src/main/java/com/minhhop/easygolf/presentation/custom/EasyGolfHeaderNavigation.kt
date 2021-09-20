package com.minhhop.easygolf.presentation.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.AppUtils

class EasyGolfHeaderNavigation : ConstraintLayout {

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPath = Path()
    private var mSizeBezierBottom = 0

    constructor(context: Context) : super(context) { initView(null) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView(attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(attrs) }

    private fun initView(attrs: AttributeSet?){
        setWillNotDraw(false)
        mPaint.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
        var padding = 0
        attrs?.let { attributeSet->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyGolfHeaderNavigation)
            mSizeBezierBottom = typedArray.getDimension(R.styleable.EasyGolfHeaderNavigation_sizeBezierBottom,
            context.resources.getDimension(R.dimen.easygolf_size_bezier_bottom_default)).toInt()

            padding = typedArray.getDimension(R.styleable.EasyGolfHeaderNavigation_paddingHeader,
                        mSizeBezierBottom.toFloat()
                    ).toInt()
            typedArray.recycle()
        }
        setPadding(padding,padding * 3,padding,padding * 3)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mPaint.shader = LinearGradient(w * 1f, 0f, 0f, height.toFloat(), AppUtils.mColorPrimaryStart.toColor(),
                AppUtils.mColorPrimaryEnd.toColor(), Shader.TileMode.CLAMP)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPath.reset()
        mPath.moveTo(0f, 0f)

        mPath.lineTo(width.toFloat(),0f)
        mPath.lineTo(width.toFloat(),height.toFloat())

        mPath.cubicTo(
                width * 1f,height * 1f,
                width - mSizeBezierBottom/3f,height - mSizeBezierBottom.toFloat(),
                width - mSizeBezierBottom.toFloat() * 1.5f,height - mSizeBezierBottom.toFloat()
        )
        mPath.lineTo(mSizeBezierBottom.toFloat() * 2,height - mSizeBezierBottom.toFloat())
        mPath.cubicTo(
                mSizeBezierBottom.toFloat(),height - mSizeBezierBottom.toFloat(),
                mSizeBezierBottom/3f,height - mSizeBezierBottom.toFloat(),
                0f,height * 1f
        )
        mPath.close()
        canvas?.drawPath(mPath, mPaint)
    }
}