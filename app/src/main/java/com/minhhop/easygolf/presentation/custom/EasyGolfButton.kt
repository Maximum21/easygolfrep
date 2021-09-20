package com.minhhop.easygolf.presentation.custom

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.AppUtils

class EasyGolfButton : FrameLayout {

    enum class Status {
        IDLE,
        PROCESS
    }

    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mEasyGolfButtonStatus: Status = Status.IDLE

    private var mIsThemePrimary: Boolean = true
    private lateinit var mNameButtonView: MaterialTextView
    private lateinit var mProcessView: ProgressBar
    private var mStartColor:Int = AppUtils.mColorBezierStart.toColor()
    private var mEndColor:Int = AppUtils.mColorBezierEnd.toColor()
    private var mTag: Int = 0

    constructor(context: Context) : super(context) {
        initView(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
        val sizeProcessView = context.resources.getDimension(R.dimen.large_padding).toInt()
        val smallPaddingButton = context.resources.getDimension(R.dimen.small_padding).toInt()
        val paddingButton = context.resources.getDimension(R.dimen.normal_padding).toInt()
        setPadding(paddingButton, smallPaddingButton, paddingButton, smallPaddingButton)
        mNameButtonView = MaterialTextView(context)
        mNameButtonView.text = context.getString(R.string.sign_in)
        TextViewCompat.setTextAppearance(mNameButtonView, R.style.normal)

        mNameButtonView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimension(R.dimen.small_font_size))
        mNameButtonView.isAllCaps = true
        mNameButtonView.gravity = Gravity.CENTER
        mNameButtonView.compoundDrawablePadding = smallPaddingButton / 2
        addView(mNameButtonView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        (mNameButtonView.layoutParams as? LayoutParams)?.gravity = Gravity.CENTER

        mProcessView = ProgressBar(context)
        mProcessView.indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
        addView(mProcessView)
        (mProcessView.layoutParams as? LayoutParams)?.let {
            it.gravity = Gravity.CENTER
            it.width = sizeProcessView
            it.height = sizeProcessView
        }
        mProcessView.visibility = View.INVISIBLE

        attrs?.let { attributeSet ->
            val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.EasyGolfButton)
            mNameButtonView.text = typedArray.getString(R.styleable.EasyGolfButton_textButton)
                    ?: context.getString(R.string.app_name)
            mIsThemePrimary = typedArray.getBoolean(R.styleable.EasyGolfButton_isThemePrimary, true)
            val iconLeading = typedArray.getDrawable(R.styleable.EasyGolfButton_iconLeading)
            mNameButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(iconLeading, null, null, null)

            mStartColor = typedArray.getColor(R.styleable.EasyGolfButton_startColor,AppUtils.mColorBezierStart.toColor())
            mEndColor = typedArray.getColor(R.styleable.EasyGolfButton_endColor,AppUtils.mColorBezierEnd.toColor())
            typedArray.recycle()
        }

        if (mIsThemePrimary) {
            mNameButtonView.setTextColor(ContextCompat.getColor(context, R.color.colorWhite))
            TextViewCompat.setCompoundDrawableTintList(mNameButtonView,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorWhite)))
        } else {
            mNameButtonView.setTextColor(ContextCompat.getColor(context, R.color.colorLinker))
            TextViewCompat.setCompoundDrawableTintList(mNameButtonView,
                    ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorLinker)))
        }

        setWillNotDraw(false)
        mPaint.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }
    }

    fun getMTag(): Int {
        return this.mTag
    }

    fun setMTag(mTag: Int) {
        this.mTag = mTag
    }

    fun getText(): String {
        return mNameButtonView.text.toString().trim()
    }

    fun setText(text: String) {
        mNameButtonView.text = text
    }

    fun setTextColor(id: Int) {
        mNameButtonView.setTextColor(ContextCompat.getColor(context, id))
    }

    fun setDrawable(drawable: Drawable?) {
        mNameButtonView.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null)
    }

    fun getStatus() = mEasyGolfButtonStatus
    fun setStatus(status: Status) {
        mEasyGolfButtonStatus = status
        if (mEasyGolfButtonStatus == Status.IDLE) {

            mProcessView.alpha = 1f
            mProcessView.scaleX = 1f
            mProcessView.scaleY = 1f

            val progressViewAnimation = mProcessView.animate().scaleX(0f).scaleY(0f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(LinearInterpolator())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(p0: Animator?) {

                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            mProcessView.visibility = View.GONE
                            mNameButtonView.pivotX = mNameButtonView.width / 2f
                            mNameButtonView.pivotY = mNameButtonView.height.toFloat()
                            mNameButtonView.animate().scaleX(1f).scaleY(1f)
                                    .alpha(1f)
                                    .setDuration(200)
                                    .setListener(null)
                                    .setInterpolator(LinearInterpolator())
                                    .start()
                        }

                        override fun onAnimationCancel(p0: Animator?) {

                        }

                        override fun onAnimationStart(p0: Animator?) {

                        }

                    })
            progressViewAnimation.start()

        } else {
            mNameButtonView.pivotX = mNameButtonView.width / 2f
            mNameButtonView.pivotY = mNameButtonView.height.toFloat()

            mProcessView.pivotX = mProcessView.width / 2f
            mProcessView.pivotY = mProcessView.height.toFloat()

            mNameButtonView.animate().scaleX(0f).scaleY(0f)
                    .alpha(0f)
                    .setDuration(200)
                    .setInterpolator(LinearInterpolator())
                    .setListener(object : Animator.AnimatorListener {
                        override fun onAnimationRepeat(p0: Animator?) {

                        }

                        override fun onAnimationEnd(p0: Animator?) {
                            mProcessView.visibility = View.VISIBLE
                            mProcessView.alpha = 0f
                            mProcessView.scaleX = 0f
                            mProcessView.scaleY = 0f
                            mProcessView.animate().scaleX(1f).scaleY(1f)
                                    .alpha(1f)
                                    .setDuration(200)
                                    .setListener(null)
                                    .setInterpolator(LinearInterpolator())
                                    .start()
                        }

                        override fun onAnimationCancel(p0: Animator?) {

                        }

                        override fun onAnimationStart(p0: Animator?) {

                        }

                    })
                    .start()
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(0f, 0f, width * 1f, height * 1f, height / 2f, height / 2f, mPaint)
        super.dispatchDraw(canvas)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (mIsThemePrimary) {
            mPaint.shader = LinearGradient(0f, h/2f, w.toFloat(), h/2f, mEndColor,
                    mStartColor, Shader.TileMode.CLAMP)
        } else {
            mPaint.color = ContextCompat.getColor(context, R.color.colorBackgroundThemeLinkerEasyGolfButton)
        }
    }
}