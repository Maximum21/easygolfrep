package com.minhhop.easygolf.presentation.custom.bottomnavigation

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.presentation.custom.bottomnavigation.EasyGolfBottomBehavior.Tab.*

class EasyGolfBottomNavigation : FrameLayout {

    companion object{
        interface CallbackBehavior{
            fun onChangeTab(tab:EasyGolfBottomBehavior.Tab)
        }
        private const val PROPERTY_MOVE_TAB = "PROPERTY_MOVE_TAB"
        private const val PROPERTY_MOVE_ACTIVE_ICON_TAB = "PROPERTY_MOVE_ACTIVE_ICON_TAB"
        private const val PROPERTY_MOVE_HIDE_ICON_TAB = "PROPERTY_MOVE_HIDE_ICON_TAB"
        private const val PROPERTY_COLOR_ICON_TAB = "PROPERTY_COLOR_ICON_TAB"
    }
    private var mCallbackBehavior:CallbackBehavior? = null
    private val mColorActiveHolder = ColorRGBHolder(255,255,255)

    private val mSizeShadow = context.resources.getDimension(R.dimen.shadow_bottom_navigation).toInt()
    private val mHeightTab = context.resources.getDimension(R.dimen.bottom_navigation_height).toInt()
    private var mSizeBezierView = context.resources.getDimension(R.dimen.bottom_navigation_height_bezier).toInt()
    private var mHeightNavigationBottom  = 0
    private var mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintShadow = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mPaintCircle = Paint(Paint.ANTI_ALIAS_FLAG)
    private var mShadowPath = Path()
    private var mMainPath = Path()

    private var mSizeIconTab = mHeightTab/2
    private var mPositionHideYIconTab = mSizeBezierView + mHeightNavigationBottom/2f + mHeightTab / 4
    private var mPositionActiveYIconTab = context.resources.getDimension(R.dimen.easygolf_bottom_navigation_height_bezier).toInt()/2f - mHeightTab / 4

    private var mTabActiveBehavior:EasyGolfBottomBehavior.Tab = GOLF
    private var mPositionHolderBezier = 0f

    /**
     * init pointF for position path
     * */
    private val mPositionBezier = PointF()
    private val mPointStart = PointF()
    private val mPointEnd = PointF()

    private val mA1ControlPoint = PointF()
    private val mB1ControlPoint = PointF()
    private val mC1EndPoint = PointF()


    private val mA2ControlPoint = PointF()
    private val mB2ControlPoint = PointF()
    private val mC2EndPoint = PointF()


    private lateinit var mContainerTab:LinearLayout
    private lateinit var mTabNewFeed:ViewGroup
    private lateinit var mTabGolf:ViewGroup
    private lateinit var mTabGroupChat:ViewGroup

    private lateinit var mIconTabNewFeed:AppCompatImageView
    private lateinit var mIconTabGolf:AppCompatImageView
    private lateinit var mIconTabGroupChat:AppCompatImageView

    private var mValueAnimator:ValueAnimator = ValueAnimator()

    constructor(context: Context) : super(context) { initView() }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initView() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initView() }

    private fun initView() {

        setWillNotDraw(false)
        mPaint.apply {
            isAntiAlias = true
            color = Color.WHITE
            style = Paint.Style.FILL

        }

        mPaintShadow.apply {
            isAntiAlias = true
            color = Color.RED
            style = Paint.Style.FILL
        }

        mPaintCircle.apply {
            isAntiAlias = true
            color = AppUtils.mColorBezierStart.toColor()
            style = Paint.Style.FILL
        }

        // Create tabs

        val layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mHeightTab).apply {
            gravity = Gravity.CENTER or Gravity.BOTTOM
        }
        mContainerTab = LinearLayout(context)
        mContainerTab.orientation = LinearLayout.HORIZONTAL
        addView(mContainerTab,layoutParams)

        mTabNewFeed = createTabBottomNavigation()
        mTabGolf = createTabBottomNavigation()
        mTabGroupChat = createTabBottomNavigation()

        mIconTabNewFeed = createIconTabBottomNavigation(R.drawable.ic_icon_news)
        mIconTabGolf = createIconTabBottomNavigation(R.drawable.ic_icon_course)
        mIconTabGroupChat = createIconTabBottomNavigation(R.drawable.ic_icon_group)

        mIconTabNewFeed.y = mPositionHideYIconTab
        mIconTabGolf.y = mPositionActiveYIconTab
        mIconTabGolf.imageTintList = ColorStateList.valueOf(Color.rgb(mColorActiveHolder.red,mColorActiveHolder.green,mColorActiveHolder.blue))
        mIconTabGroupChat.y = mPositionHideYIconTab

        mTabNewFeed.setOnClickListener {
            if(mTabActiveBehavior != NEWS_FEED) {
                moveBezierToTargetByBehavior(NEWS_FEED)
            }
        }

        mTabGolf.setOnClickListener {
            if(mTabActiveBehavior != GOLF) {
                moveBezierToTargetByBehavior(GOLF)
            }
        }

        mTabGroupChat.setOnClickListener {
            if(mTabActiveBehavior != GROUP_CHAT) {
                moveBezierToTargetByBehavior(GROUP_CHAT)
            }
        }
    }

    fun getTabActive() = mTabActiveBehavior
    private fun moveBezierToTargetByBehavior(tab: EasyGolfBottomBehavior.Tab){
        val startPositionTab = mPositionHolderBezier
        val targetPositionTab = getTargetPositionByBehavior(tab)

        val iconTabActiveView = getBurnIconTab(tab)
        val iconTabHideView = getBurnIconTab(mTabActiveBehavior)

        val propertyMoveTab = PropertyValuesHolder.ofFloat(PROPERTY_MOVE_TAB, startPositionTab, targetPositionTab)
        val propertyMoveActiveIconTab = PropertyValuesHolder.ofFloat(PROPERTY_MOVE_ACTIVE_ICON_TAB, iconTabActiveView.y, mPositionActiveYIconTab)
        val propertyMoveHideIconTab = PropertyValuesHolder.ofFloat(PROPERTY_MOVE_HIDE_ICON_TAB, iconTabHideView.y, mPositionHideYIconTab)

        val propertyColorIconTab = PropertyValuesHolder.ofFloat(PROPERTY_COLOR_ICON_TAB, 0f, 1f)

        mValueAnimator.removeAllUpdateListeners()
        mValueAnimator.interpolator = LinearInterpolator()
        mValueAnimator.setValues(propertyMoveTab,propertyMoveActiveIconTab,propertyMoveHideIconTab,propertyColorIconTab)
        mValueAnimator.duration = 300
        mValueAnimator.addUpdateListener { animation ->
            val positionBezierUpdate = animation.getAnimatedValue(PROPERTY_MOVE_TAB) as Float
            mPositionHolderBezier = positionBezierUpdate
            /**
             * for icon tab
             * */
            val positionIconTabActiveUpdate = animation.getAnimatedValue(PROPERTY_MOVE_ACTIVE_ICON_TAB) as Float
            val colorIconTabUpdate = animation.getAnimatedValue(PROPERTY_COLOR_ICON_TAB) as Float

            val positionIconTabHideUpdate = animation.getAnimatedValue(PROPERTY_MOVE_HIDE_ICON_TAB) as Float

            val deltaRedActive = (AppUtils.mColorBezierEnd.red + (mColorActiveHolder.red - AppUtils.mColorBezierEnd.red) * colorIconTabUpdate).toInt()
            val deltaGreenActive = (AppUtils.mColorBezierEnd.green + (mColorActiveHolder.green - AppUtils.mColorBezierEnd.green) * colorIconTabUpdate).toInt()
            val deltaBlueActive = (AppUtils.mColorBezierEnd.blue + (mColorActiveHolder.blue - AppUtils.mColorBezierEnd.blue) * colorIconTabUpdate).toInt()

            val deltaRedHide = (mColorActiveHolder.red - (mColorActiveHolder.red - AppUtils.mColorBezierEnd.red) * colorIconTabUpdate).toInt()
            val deltaGreenHide = (mColorActiveHolder.green - (mColorActiveHolder.green - AppUtils.mColorBezierEnd.green) * colorIconTabUpdate).toInt()
            val deltaBlueHide = (mColorActiveHolder.blue - (mColorActiveHolder.blue - AppUtils.mColorBezierEnd.blue) * colorIconTabUpdate).toInt()


            val colorActiveIcon = Color.rgb(deltaRedActive,deltaGreenActive,deltaBlueActive)
            iconTabActiveView.imageTintList =  ColorStateList.valueOf(colorActiveIcon)

            val colorHideIcon = Color.rgb(deltaRedHide,deltaGreenHide,deltaBlueHide)
            iconTabHideView.imageTintList = ColorStateList.valueOf(colorHideIcon)
            iconTabActiveView.y = positionIconTabActiveUpdate
            iconTabHideView.y = positionIconTabHideUpdate

            invalidate()
        }
        mValueAnimator.start()
        mTabActiveBehavior = tab
        mCallbackBehavior?.onChangeTab(mTabActiveBehavior)
    }

    fun addOnCallbackBehavior(callback: CallbackBehavior){
        this.mCallbackBehavior = callback
    }

    private fun getBurnIconTab(tab: EasyGolfBottomBehavior.Tab):AppCompatImageView{
        return when(tab){
            NEWS_FEED -> {
                mIconTabNewFeed
            }
            GOLF -> {
                mIconTabGolf
            }
            GROUP_CHAT -> {
                mIconTabGroupChat
            }
        }
    }

    private fun getTargetPositionByBehavior(tab: EasyGolfBottomBehavior.Tab):Float{
        return when(tab){
            NEWS_FEED ->{
                width /6f
            }
            GOLF ->{
                width /2f
            }
            GROUP_CHAT ->{
                width * 5f/6
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        elevation = 10f
        /**
         * init views
         * */
        mIconTabNewFeed.x = w /6f - mHeightTab / 4
        mIconTabGolf.x = w /2f - mHeightTab / 4
        mIconTabGroupChat.x = w * 5 /6f - mHeightTab / 4

        if(mPositionHolderBezier == 0f) {
            mPositionHolderBezier = w / 2f
        }

        mPaintCircle.shader = LinearGradient(0f, 0f, 0f, height.toFloat(), AppUtils.mColorBezierStart.toColor(),
                AppUtils.mColorBezierEnd.toColor(), Shader.TileMode.CLAMP)

        mPaint.setShadowLayer(mHeightNavigationBottom * 1f,0f,0f,Color.argb(100,0,0,0))
        setLayerType(View.LAYER_TYPE_SOFTWARE,mPaint)

//      mPaintShadow.maskFilter = BlurMaskFilter(mSizeShadow * 1f,BlurMaskFilter.Blur.NORMAL)//make blur but it too bad for performance
//        mPaint.setShadowLayer(h/2f, w/2f, w/2f,  Color.argb(100,0,0,0))
        mPaintShadow.shader = LinearGradient(w/2f, mSizeBezierView * 1f, w/2f, 0f, Color.argb(5,0,0,0),
                Color.argb(0,0,0,0), Shader.TileMode.MIRROR)
    }

    override fun onDraw(c: Canvas?) {
        mPositionBezier.set(mPositionHolderBezier ,mSizeShadow * 1f)

        val sizeCell = calculatorSizeSingleCell().toInt()
        val singeCell = calculatorSizeSingleCell().toInt()/3

        mPointStart.set(mPositionBezier.x - sizeCell/2,mSizeBezierView * 1f)
        mPointEnd.set(width * 2f,mSizeBezierView * 1f)

        mA1ControlPoint.set(mPositionBezier.x - singeCell /2,mPointStart.y)
        mB1ControlPoint.set(mPositionBezier.x - singeCell /2,0f)
        mC1EndPoint.set(mPositionBezier.x,0f)

        mA2ControlPoint.set(mPositionBezier.x + singeCell /2,0f)
        mB2ControlPoint.set(mPositionBezier.x + singeCell /2,mPointStart.y)
        mC2EndPoint.set(mPositionBezier.x + sizeCell/2,mPointStart.y)

        /***
         * start shadow
         */
        mShadowPath.reset()
        mShadowPath.moveTo(0f, mPointStart.y - mSizeShadow)

        mShadowPath.lineTo(mPointStart.x * 1f, mPointStart.y * 1f - mSizeShadow )
        mShadowPath.cubicTo(
                mA1ControlPoint.x * 1f,mA1ControlPoint.y * 1f - mSizeShadow,
                mB1ControlPoint.x * 1f,mB1ControlPoint.y * 1f,
                mC1EndPoint.x * 1f,mC1EndPoint.y * 1f
        )

        mShadowPath.cubicTo(
                mA2ControlPoint.x * 1f,mA2ControlPoint.y * 1f,
                mB2ControlPoint.x * 1f,mB2ControlPoint.y * 1f - mSizeShadow,
                mC2EndPoint.x * 1f,mC2EndPoint.y * 1f - mSizeShadow
        )

        mShadowPath.lineTo(mPointEnd.x * 1f, mPointEnd.y * 1f - mSizeShadow)
        mShadowPath.lineTo(mPointEnd.x * 1f, height * 1f)
        mShadowPath.lineTo(0f, height * 1f)

        mShadowPath.close()
        c?.drawPath(mShadowPath, mPaintShadow)
        /***
         * end shadow
         */

        mMainPath.reset()
        mMainPath.moveTo(0f, mSizeBezierView.toFloat())

        mMainPath.lineTo(mPointStart.x * 1f, mPointStart.y * 1f)
        mMainPath.cubicTo(
                mA1ControlPoint.x * 1f,mA1ControlPoint.y * 1f,
                mB1ControlPoint.x * 1f,mB1ControlPoint.y * 1f + mSizeShadow, //add shadow
                mC1EndPoint.x * 1f,mC1EndPoint.y * 1f + mSizeShadow //add shadow
        )

        mMainPath.cubicTo(
                mA2ControlPoint.x * 1f,mA2ControlPoint.y * 1f + mSizeShadow, //add shadow,
                mB2ControlPoint.x * 1f,mB2ControlPoint.y * 1f,
                mC2EndPoint.x * 1f,mC2EndPoint.y * 1f
        )

        mMainPath.lineTo(mPointEnd.x * 1f, mPointEnd.y * 1f)
        mMainPath.lineTo(mPointEnd.x * 1f, height * 1f)
        mMainPath.lineTo(0f, height * 1f)

        mMainPath.close()

        c?.drawPath(mMainPath, mPaint)

        c?.drawCircle(mPositionHolderBezier,height/2f,(height - mSizeBezierView)/2f,mPaintCircle)
    }

    private fun createTabBottomNavigation():ViewGroup{
        val frameLayoutContainer = FrameLayout(context)
        val params = LinearLayout.LayoutParams(
                0,
                LayoutParams.MATCH_PARENT
        ).apply {
            weight = 1f
            gravity = Gravity.CENTER
        }
        mContainerTab.addView(frameLayoutContainer,params)
        return frameLayoutContainer
    }

    private fun createIconTabBottomNavigation(@DrawableRes iconRes:Int): AppCompatImageView {
        val iconTab = AppCompatImageView(context)
        iconTab.setImageDrawable(ContextCompat.getDrawable(context,iconRes))
        iconTab.adjustViewBounds = true
        iconTab.imageTintList = ColorStateList.valueOf(AppUtils.mColorBezierEnd.toColor())

        val paramsIcon = LayoutParams(
                mSizeIconTab,
                mSizeIconTab
        ).apply {
            gravity = Gravity.START or Gravity.TOP
        }

        addView(iconTab,paramsIcon)
        return iconTab
    }

    private fun calculatorSizeSingleCell () = width/3f
}