package com.minhhop.easygolf.presentation.verify_code

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.inputmethod.BaseInputConnection
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.TextViewCompat
import com.google.android.material.textview.MaterialTextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.utils.TextViewUtils
import java.util.*

class VerificationCodeView : LinearLayout {
    private var mCodeTextSize = 0f
    private var mCodeTextColor = 0
    private var mCodeBottomIcon: Drawable? = null
    private var mCodeErrorBottomIcon: Drawable? = null
    private var mCodeItemCenterSpaceSize = 0
    private var mCodeStringBuilder = StringBuilder()
    private var mCodeViewList =  ArrayList<MaterialTextView>()
    private var mCodeCompleteListener: CodeCompleteListener? = null
    private var mIsOpenKeyboard = false

    private var mIsEnableOpenKeyboard = true
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.VerificationCodeView)
        mCodeTextSize = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_codeTextSize,
                resources.getDimensionPixelSize(R.dimen.code_text_size)).toFloat()
        mCodeTextColor = typedArray.getColor(R.styleable.VerificationCodeView_codeTextColor,
                ContextCompat.getColor(context,R.color.color_text_code_verify))
        mCodeBottomIcon = typedArray.getDrawable(R.styleable.VerificationCodeView_codeBottomIcon)
        if (mCodeBottomIcon == null) {
            mCodeBottomIcon = ContextCompat.getDrawable(context,R.drawable.icon_code_verify_bottom)
        }
        mCodeErrorBottomIcon = typedArray.getDrawable(R.styleable.VerificationCodeView_codeBottomIcon)
        if (mCodeBottomIcon == null) {
            mCodeBottomIcon = ContextCompat.getDrawable(context,R.drawable.icon_error_code_verify_bottom)
        }
        val viewCount = typedArray.getInt(R.styleable.VerificationCodeView_codeViewCount, DEFAULT_VIEW_COUNT)
        mCodeItemCenterSpaceSize = typedArray.getDimensionPixelSize(R.styleable.VerificationCodeView_codeItemSpaceSize,
                resources.getDimensionPixelSize(R.dimen.code_item_space_size))
        mIsOpenKeyboard = typedArray.getBoolean(R.styleable.VerificationCodeView_openKeyBoard, false)
        if (mIsOpenKeyboard) {
            openKeyboard()
        }
        openKeyboard()
        typedArray.recycle()
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        isFocusableInTouchMode = true

        for (i in 0 until viewCount) {
            val underLineCodeView = underLineCodeView()
            mCodeViewList.add(underLineCodeView)
            addView(underLineCodeView)
        }

    }

    private fun underLineCodeView() :MaterialTextView {
            val textView = MaterialTextView(context)
            textView.textSize = mCodeTextSize
            textView.setTextColor(mCodeTextColor)
            textView.gravity = Gravity.CENTER
            TextViewCompat.setTextAppearance(textView, R.style.bold)
            val padding = mCodeItemCenterSpaceSize / 2
            textView.setPadding(padding, 0, padding, 0)
            val bottomIconWidth = resources.getDimensionPixelSize(R.dimen.code_verify_bottom_icon_width)
            val bottomIconHeight = resources.getDimensionPixelSize(R.dimen.code_verify_bottom_icon_height)
            TextViewUtils.addBottomIcon(textView, mCodeBottomIcon, bottomIconWidth, bottomIconHeight)
            return textView
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN && mIsEnableOpenKeyboard) {
            openKeyboard()
            performClick()
        }
        return true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mIsOpenKeyboard) {
            openKeyboard()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mIsOpenKeyboard) {
            openKeyboard()
        }
    }

    fun freezeKeyboard(){
        mIsEnableOpenKeyboard = false
    }

    fun burnIceKeyboard(){
        mIsEnableOpenKeyboard = true
    }

    fun openKeyboard() {
        requestFocusFromTouch()
        requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }


    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        val fic = BaseInputConnection(this, false)
        outAttrs.actionLabel = null
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER
        outAttrs.imeOptions = EditorInfo.IME_ACTION_NONE
        return fic
    }

    private fun setCodeItemLineDrawable(codeBottomIcon: Drawable?) {
        for (textView in mCodeViewList) {
            val bottomIconWidth = resources.getDimensionPixelSize(R.dimen.code_verify_bottom_icon_width)
            val bottomIconHeight = resources.getDimensionPixelSize(R.dimen.code_verify_bottom_icon_height)
            TextViewUtils.addBottomIcon(textView, codeBottomIcon, bottomIconWidth, bottomIconHeight)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        //        when user press backspace
        if (keyCode == 67 && mCodeStringBuilder.isNotEmpty()) {
            mCodeStringBuilder.deleteCharAt(mCodeStringBuilder.length - 1)
            resetCodeShowView()
        } else {
            if (keyCode in 7..16 && mCodeStringBuilder.length < mCodeViewList.size) {
                mCodeStringBuilder.append(keyCode - 7)
                resetCodeShowView()
            }
        }
        if (mCodeStringBuilder.length >= mCodeViewList.size || keyCode == 66) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(windowToken, 0)
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun resetCodeShowView() {
        if (mCodeViewList.size > 0) {
            val size = mCodeViewList.size
            for (i in 0 until size) {
                mCodeViewList[i].text = ""
                if (i < mCodeStringBuilder.length) {
                    mCodeViewList[i].text = mCodeStringBuilder[i].toString()
                }
            }
            if (mCodeCompleteListener != null) {
                if (mCodeStringBuilder.length == mCodeViewList.size) {
                    mCodeCompleteListener!!.onComplete(true)
                } else {
                    mCodeCompleteListener!!.onComplete(false)
                }
            }
        }
    }

    fun setCodeItemErrorLineDrawable() {
        setCodeItemLineDrawable(mCodeErrorBottomIcon)
    }

    val textString: String
        get() = if (mCodeStringBuilder.isEmpty()) "" else mCodeStringBuilder.toString()

    interface CodeCompleteListener {
        fun onComplete(thatTrue: Boolean)
    }
    companion object {
        private const val DEFAULT_VIEW_COUNT = 4
    }
}