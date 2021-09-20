package com.minhhop.easygolf.presentation.burn

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import com.minhhop.easygolf.R
import kotlin.math.sqrt

class BurnLoadingView : FrameLayout {
    companion object{
        private val DST_IN_PORTER_DUFF_X_FER_MODE = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
    }

    constructor(context: Context) : super(context) { initView(context,null) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView(context,attrs) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView(context,attrs) }


    enum class MaskAngle {
        CW_0,  // left to right
        CW_90,  // top to bottom
        CW_180,  // right to left
        CW_270
        // bottom to top
    }

    private class Mask {
        var angle: MaskAngle? = null
        var tilt = 0f
        var dropoff = 0f
        var fixedWidth = 0
        var fixedHeight = 0
        var intensity = 0f
        var relativeWidth = 0f
        var relativeHeight = 0f
        fun maskWidth(width: Int): Int {
            return if (fixedWidth > 0) fixedWidth else (width * relativeWidth).toInt()
        }

        fun maskHeight(height: Int): Int {
            return if (fixedHeight > 0) fixedHeight else (height * relativeHeight).toInt()
        }

        val gradientColors: IntArray
            get() = intArrayOf(Color.TRANSPARENT, Color.BLACK, Color.BLACK, Color.TRANSPARENT)

        val gradientPositions: FloatArray
            get() = floatArrayOf(
                    ((1.0f - intensity - dropoff) / 2).coerceAtLeast(0.0f),
                    ((1.0f - intensity) / 2).coerceAtLeast(0.0f),
                    ((1.0f + intensity) / 2).coerceAtMost(1.0f),
                    ((1.0f + intensity + dropoff) / 2).coerceAtMost(1.0f))

    }

    private class MaskTranslation {
        var fromX = 0
        var fromY = 0
        var toX = 0
        var toY = 0
        operator fun set(fromX: Int, fromY: Int, toX: Int, toY: Int) {
            this.fromX = fromX
            this.fromY = fromY
            this.toX = toX
            this.toY = toY
        }
    }

    private var mAlphaPaint: Paint = Paint()
    private var mMaskPaint: Paint = Paint()

    private lateinit var mMask: Mask
    private lateinit var mMaskTranslation: MaskTranslation

    private var mRenderMaskBitmap: Bitmap? = null
    private var mRenderUnmaskBitmap: Bitmap? = null

    private var mAutoStart = false
    private var mDuration = 0
    private var mRepeatCount = 0
    private var mRepeatDelay = 0
    private var mRepeatMode = 0

    private var mMaskOffsetX = 0
    private var mMaskOffsetY = 0

    private var mAnimationStarted = false
    private var mGlobalLayoutListener: OnGlobalLayoutListener? = null

    private var mAnimator: ValueAnimator? = null
    private var mMaskBitmap: Bitmap? = null

    private fun initView(context: Context, attrs: AttributeSet?){
        setWillNotDraw(false)
        mMask = Mask()
        mAlphaPaint = Paint()
        mMaskPaint = Paint()
        mMaskPaint.isAntiAlias = true
        mMaskPaint.isDither = true
        mMaskPaint.isFilterBitmap = true
        mMaskPaint.xfermode = DST_IN_PORTER_DUFF_X_FER_MODE
        useDefaults()
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BurnLoadingView, 0, 0)
            try {
                if (a.hasValue(R.styleable.BurnLoadingView_auto_start)) {
                    setAutoStart(a.getBoolean(R.styleable.BurnLoadingView_auto_start, false))
                }
                if (a.hasValue(R.styleable.BurnLoadingView_duration)) {
                    setDuration(a.getInt(R.styleable.BurnLoadingView_duration, 0))
                }
            } finally {
                a.recycle()
            }
        }
    }


    private fun useDefaults() {
        // Set defaults
        setAutoStart(false)
        setDuration(1000)
        mRepeatCount = ObjectAnimator.INFINITE
        mRepeatDelay = 0
        mRepeatMode = ObjectAnimator.RESTART
        mMask.angle = MaskAngle.CW_0
        mMask.dropoff = 0.5f
        mMask.fixedWidth = 0
        mMask.fixedHeight = 0
        mMask.intensity = 0.0f
        mMask.relativeWidth = 1.0f
        mMask.relativeHeight = 1.0f
        mMask.tilt = 20f
        mMaskTranslation = MaskTranslation()

        mAlphaPaint.alpha = (0.6f * 0xff).toInt()
        resetAll()
    }

    private fun setAutoStart(autoStart: Boolean) {
        mAutoStart = autoStart
        resetAll()
    }

    fun setDuration(duration: Int) {
        mDuration = duration
        resetAll()
    }

    fun startBurnViewAnimation() {
        if (mAnimationStarted) {
            return
        }
        val animator = getBurnAnimation()
        animator!!.start()
        mAnimationStarted = true
    }

    fun isBurning() = mAnimationStarted

    fun stopBurnViewAnimation() {
        mAnimator?.let { animatorClear->
            animatorClear.end()
            animatorClear.removeAllUpdateListeners()
            animatorClear.cancel()
        }
        mAnimator = null
        mAnimationStarted = false
    }


    private fun setMaskOffsetX(maskOffsetX: Int) {
        if (mMaskOffsetX == maskOffsetX) {
            return
        }
        mMaskOffsetX = maskOffsetX
        invalidate()
    }


    private fun setMaskOffsetY(maskOffsetY: Int) {
        if (mMaskOffsetY == maskOffsetY) {
            return
        }
        mMaskOffsetY = maskOffsetY
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mGlobalLayoutListener == null) {
            mGlobalLayoutListener = getLayoutListener()
        }
        viewTreeObserver.addOnGlobalLayoutListener(mGlobalLayoutListener)
    }

    private fun getLayoutListener(): OnGlobalLayoutListener? {
        return OnGlobalLayoutListener {
            val animationStarted = mAnimationStarted
            resetAll()
            if (mAutoStart || animationStarted) {
                startBurnViewAnimation()
            }
        }
    }

    override fun onDetachedFromWindow() {
        stopBurnViewAnimation()
        if (mGlobalLayoutListener != null) {
            viewTreeObserver.removeOnGlobalLayoutListener(mGlobalLayoutListener)
            mGlobalLayoutListener = null
        }
        super.onDetachedFromWindow()
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (!mAnimationStarted || width <= 0 || height <= 0) {
            super.dispatchDraw(canvas)
            return
        }
        dispatchDrawUsingBitmap(canvas)
    }

    private fun dispatchDrawUsingBitmap(canvas: Canvas): Boolean {
        val unmaskBitmap = tryObtainRenderUnmaskBitmap()
        val maskBitmap = tryObtainRenderMaskBitmap()
        if (unmaskBitmap == null || maskBitmap == null) {
            return false
        }
        // First draw a desaturated version
        drawUnmasked(Canvas(unmaskBitmap))
        canvas.drawBitmap(unmaskBitmap, 0f, 0f, mAlphaPaint)

        // Then draw the masked version
        drawMasked(Canvas(maskBitmap))
        canvas.drawBitmap(maskBitmap, 0f, 0f, null)
        return true
    }

    private fun tryObtainRenderUnmaskBitmap(): Bitmap? {
        if (mRenderUnmaskBitmap == null) {
            mRenderUnmaskBitmap = tryCreateRenderBitmap()
        }
        return mRenderUnmaskBitmap
    }

    private fun tryObtainRenderMaskBitmap(): Bitmap? {
        if (mRenderMaskBitmap == null) {
            mRenderMaskBitmap = tryCreateRenderBitmap()
        }
        return mRenderMaskBitmap
    }

    private fun tryCreateRenderBitmap(): Bitmap? {
        val width = width
        val height = height
        try {
            return createBitmapAndGcIfNecessary(width, height)
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
        }
        return null
    }

    private fun drawUnmasked(renderCanvas: Canvas) {
        super.dispatchDraw(renderCanvas)
    }

    private fun drawMasked(renderCanvas: Canvas) {
        val maskBitmap = getMaskBitmap() ?: return
        renderCanvas.clipRect(
                mMaskOffsetX,
                mMaskOffsetY,
                mMaskOffsetX + maskBitmap.width,
                mMaskOffsetY + maskBitmap.height)
        super.dispatchDraw(renderCanvas)
        renderCanvas.drawBitmap(maskBitmap, mMaskOffsetX.toFloat(), mMaskOffsetY.toFloat(), mMaskPaint)
    }

    private fun resetAll() {
        stopBurnViewAnimation()
        resetMaskBitmap()
        resetRenderedView()
    }

    private fun resetMaskBitmap() {
        if (mMaskBitmap != null) {
            mMaskBitmap!!.recycle()
            mMaskBitmap = null
        }
    }

    private fun resetRenderedView() {
        if (mRenderUnmaskBitmap != null) {
            mRenderUnmaskBitmap!!.recycle()
            mRenderUnmaskBitmap = null
        }
        if (mRenderMaskBitmap != null) {
            mRenderMaskBitmap!!.recycle()
            mRenderMaskBitmap = null
        }
    }

    private fun getMaskBitmap(): Bitmap? {
        if (mMaskBitmap != null) {
            return mMaskBitmap
        }
        val width = mMask.maskWidth(width)
        val height = mMask.maskHeight(height)
        mMaskBitmap = createBitmapAndGcIfNecessary(width, height)
        val canvas = Canvas(mMaskBitmap!!)
        val gradient: Shader
        val x1: Int
        val y1: Int
        val x2: Int
        val y2: Int

        when (mMask.angle) {
            MaskAngle.CW_0 -> {
                x1 = 0
                y1 = 0
                x2 = width
                y2 = 0
            }
            MaskAngle.CW_90 -> {
                x1 = 0
                y1 = 0
                x2 = 0
                y2 = height
            }
            MaskAngle.CW_180 -> {
                x1 = width
                y1 = 0
                x2 = 0
                y2 = 0
            }
            MaskAngle.CW_270 -> {
                x1 = 0
                y1 = height
                x2 = 0
                y2 = 0
            }
            else -> {
                x1 = 0
                y1 = 0
                x2 = width
                y2 = 0
            }
        }
        gradient = LinearGradient(
                x1.toFloat(), y1.toFloat(),
                x2.toFloat(), y2.toFloat(),
                mMask.gradientColors,
                mMask.gradientPositions,
                Shader.TileMode.REPEAT)

        canvas.rotate(mMask.tilt, width / 2.toFloat(), height / 2.toFloat())
        val paint = Paint()
        paint.shader = gradient
        val padding = (sqrt(2.0) * width.coerceAtLeast(height)).toInt() / 2
        canvas.drawRect(-padding.toFloat(), -padding.toFloat(), width + padding.toFloat(), height + padding.toFloat(), paint)
        return mMaskBitmap
    }

    private fun getBurnAnimation(): Animator? {
        if (mAnimator != null) {
            return mAnimator
        }
        val width = width
        val height = height

        when (mMask.angle) {
            MaskAngle.CW_0 -> mMaskTranslation[-width, 0, width] = 0
            MaskAngle.CW_90 -> mMaskTranslation[0, -height, 0] = height
            MaskAngle.CW_180 -> mMaskTranslation[width, 0, -width] = 0
            MaskAngle.CW_270 -> mMaskTranslation[0, height, 0] = -height
            else -> mMaskTranslation[-width, 0, width] = 0
        }
        mAnimator = ValueAnimator.ofFloat(0.0f, 1.0f + mRepeatDelay.toFloat() / mDuration)
        mAnimator?.duration = mDuration + mRepeatDelay.toLong()
        mAnimator?.repeatCount = mRepeatCount
        mAnimator?.repeatMode = mRepeatMode
        mAnimator?.addUpdateListener { animation ->
            val value = 0.0f.coerceAtLeast(1.0f.coerceAtMost((animation.animatedValue as Float)))
            setMaskOffsetX((mMaskTranslation.fromX * (1 - value) + mMaskTranslation.toX * value).toInt())
            setMaskOffsetY((mMaskTranslation.fromY * (1 - value) + mMaskTranslation.toY * value).toInt())
        }
        return mAnimator
    }

    private fun createBitmapAndGcIfNecessary(width: Int, height: Int): Bitmap? {
        return try {
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        } catch (e: OutOfMemoryError) {
            System.gc()
            Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        }
    }
}