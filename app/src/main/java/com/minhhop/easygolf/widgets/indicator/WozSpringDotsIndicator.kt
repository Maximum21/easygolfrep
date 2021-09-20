package com.minhhop.easygolf.widgets.indicator

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.viewpager.widget.ViewPager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.utils.AppUtil

class WozSpringDotsIndicator : FrameLayout {

    companion object{
        private const val DEFAULT_DAMPING_RATIO = 0.5f
        private const val DEFAULT_STIFFNESS = 300
    }

    private var mViewPager: ViewPager? = null

    private var mStrokeDots:ArrayList<ImageView> = ArrayList()
    private lateinit var mStrokeDotsLinearLayout: LinearLayout
    private var mHorizontalMargin = 0
    private var mDotIndicatorView: View? = null
    private var mDotIndicatorSpring: SpringAnimation? = null
    /**
     *  Attributes
     */
    private var mDotsStrokeSize: Int = 0
    private var mDotIndicatorSize: Int = 0
    private var mDotsSpacing: Int = 0
    private var mDotsStrokeWidth: Int = 0
    private var mDotIndicatorAdditionalSize: Int = 0
    private var mDotsCornerRadius: Int = 0
    private var mDotsStrokeColor: Int = 0
    private var mDotIndicatorColor: Int = 0
    private var mStiffness: Float = 0f
    private var mDampingRatio: Float = 0f

    private var mDotsClickable: Boolean = false
    private var mPageChangedListener: ViewPager.OnPageChangeListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){
        initView(context,attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        initView(context,attrs)
    }


    private fun initView(context: Context, attrs: AttributeSet?){

        mDotsStrokeSize = AppUtil.convertDpToPixel(16,context)
        mDotsCornerRadius = mDotsStrokeSize / 2

        mStrokeDotsLinearLayout = LinearLayout(context)
        val linearParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        mHorizontalMargin = AppUtil.convertDpToPixel(24,context)
        linearParams.setMargins(mHorizontalMargin, 0, mHorizontalMargin, 0)
        mStrokeDotsLinearLayout.layoutParams = linearParams
        mStrokeDotsLinearLayout.orientation = LinearLayout.HORIZONTAL
        addView(mStrokeDotsLinearLayout)
        mDotIndicatorAdditionalSize = AppUtil.convertDpToPixel(1,context)
        mStiffness = DEFAULT_STIFFNESS.toFloat()
        mDampingRatio = DEFAULT_DAMPING_RATIO
        mDotsClickable = true

        /**
         * Set default
         * */
        mDotIndicatorColor = ContextCompat.getColor(context,R.color.colorPrimary)

        attrs?.also {
            val typeArray = context.obtainStyledAttributes(it, R.styleable.WozSpringDotsIndicator)
            mDotIndicatorColor = typeArray.getColor(R.styleable.WozSpringDotsIndicator_dotsColor,mDotIndicatorColor)
            mDotsStrokeColor = typeArray.getColor(R.styleable.WozSpringDotsIndicator_dotsStrokeColor,mDotsStrokeColor)

            mDotsStrokeSize = typeArray.getDimension(R.styleable.WozSpringDotsIndicator_dotsSize,mDotsStrokeSize.toFloat()).toInt()
            mDotsCornerRadius = mDotsStrokeSize / 2
            mDotIndicatorSize = mDotsStrokeSize

            mDotsSpacing = AppUtil.convertDpToPixel(4,context)

            typeArray.recycle()
        }

        if (isInEditMode) {
            addView(buildDot())
        }
    }

    private fun buildDot(isIndicator: Boolean = true):ViewGroup{
        val dot = LayoutInflater.from(context).inflate(R.layout.woz_spring_dot_layout,this,false) as ViewGroup
        val dotView:ImageView = dot.findViewById(R.id.spring_dot)

        dotView.background = ContextCompat.getDrawable(context,R.drawable.woz_spring_dot_background)
        val params = dotView.layoutParams as RelativeLayout.LayoutParams
        params.height = if (isIndicator) mDotIndicatorSize else mDotsStrokeSize
        params.width = params.height

        params.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE)
        params.setMargins(mDotsSpacing, 0, mDotsSpacing, 0)
        setUpDotBackground(dotView,isIndicator)
        return dot
    }

    private fun addStrokeDots(count:Int) {
        for (i in 0 until count) {
            val dot = buildDot(false)
            dot.setOnClickListener {
            }
            mStrokeDots.add(dot.findViewById<View>(R.id.spring_dot) as ImageView)
            mStrokeDotsLinearLayout.addView(dot)
        }
    }

    private fun setUpDotBackground(dotView:View,isIndicator: Boolean) {
        val doBackground = dotView.background as GradientDrawable?
        doBackground?.apply {
            val color = if(isIndicator){
                mDotIndicatorColor
            }else{
                mDotsStrokeColor
            }
            setColor(color)
            cornerRadius = mDotsCornerRadius.toFloat()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refreshDots()
    }

    private fun refreshDots(){
        if(mDotIndicatorView == null){
            setupIndicator()
        }

        mViewPager?.also {
            if (it.adapter != null) {
                if (mStrokeDots.size < it.adapter!!.count) {
                    addStrokeDots(it.adapter!!.count - mStrokeDots.size)
                }
            }

            setUpDotsAnimators()
        }

    }

    private fun setUpDotsAnimators() {
        mViewPager?.apply {
            adapter?.let {valueAdapter->
                if(valueAdapter.count > 0){
                    if(mPageChangedListener != null){
                        this.removeOnPageChangeListener(mPageChangedListener!!)
                    }

                    setUpOnPageChangedListener()
                    mPageChangedListener?.let { pageChangeListener->
                        this.addOnPageChangeListener(pageChangeListener)
                        pageChangeListener.onPageScrolled(0, 0f, 0)
                    }

                }
            }
        }
    }

    private fun setUpOnPageChangedListener(){
        mPageChangedListener = object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixel: Int) {
                val globalPositionOffsetPixels = position * ( mDotsStrokeSize + mDotsSpacing * 2)
                                                + (mDotsStrokeSize + mDotsSpacing * 2) * positionOffset



                val indicatorTranslationX = globalPositionOffsetPixels + mHorizontalMargin - mDotIndicatorAdditionalSize / 2f
                mDotIndicatorSpring?.let { dotSpring->
                    dotSpring.spring.finalPosition = indicatorTranslationX.toFloat()
                    dotSpring.animateToFinalPosition(indicatorTranslationX.toFloat())
                }
            }

            override fun onPageSelected(p0: Int) {}

        }
    }

    private fun setupIndicator(){
        mDotIndicatorView = buildDot()
        addView(mDotIndicatorView)
        mDotIndicatorSpring = SpringAnimation(mDotIndicatorView,SpringAnimation.TRANSLATION_X)
        val springForce = SpringForce(0f)
        springForce.dampingRatio = mDampingRatio
        springForce.stiffness = mStiffness
        mDotIndicatorSpring!!.spring = springForce
    }


    fun setViewPager(viewPager: ViewPager) {
        this.mViewPager = viewPager

        refreshDots()
    }
}
