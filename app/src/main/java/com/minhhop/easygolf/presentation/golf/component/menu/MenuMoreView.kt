package com.minhhop.easygolf.presentation.golf.component.menu

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.presentation.golf.GoogleMapsHelper

class MenuMoreView : LinearLayout {
    enum class CODE {
        VIEW_GREEN,
        REPORT_THIS_HOLE,
        SHARE,
        END_GAME,
        CHANGE_UNIT,
        CHANGE_DEFAULT_TEE
    }

    private var mHandleMenuMore: HandleMenuMore? = null
    private var mUnitGolf = GoogleMapsHelper.UnitGolf.YARD
    private lateinit var mMenuShareView: ItemMenuMoreView
    private lateinit var mMenuChangeUnitView: ItemMenuMoreView
    private lateinit var mMenuChangeTeeView: ItemMenuMoreView
    private lateinit var mMenuYardageBookView: ItemMenuMoreView
    private lateinit var mMenuReportView: ItemMenuMoreView
    private lateinit var mMenuEndGameView: ItemMenuMoreView

    constructor(context: Context?) : super(context) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView()
    }

    private fun initView() {
        background = ContextCompat.getDrawable(context, R.drawable.bg_menu_more)
        orientation = VERTICAL
        val x: Int = context.resources.getDimension(R.dimen.d_10).toInt()
        setPadding(x, 0, x, x)

        mMenuShareView = ItemMenuMoreView(context)
        mMenuShareView.setIcon(R.drawable.ic_icon_share)
        mMenuShareView.setText(context.getString(R.string.share))
        mMenuShareView.setOnClickListener {
            closeView(CODE.SHARE)
        }
        addView(mMenuShareView)

        mMenuChangeUnitView = ItemMenuMoreView(context)
        buildUnitView()
        mMenuChangeUnitView.setOnClickListener {
            mUnitGolf = if(mUnitGolf == GoogleMapsHelper.UnitGolf.YARD){
                GoogleMapsHelper.UnitGolf.METER
            }else{
                GoogleMapsHelper.UnitGolf.YARD
            }
            buildUnitView()
            closeView(CODE.CHANGE_UNIT)
        }
        addView(mMenuChangeUnitView)

        mMenuChangeTeeView = ItemMenuMoreView(context)
        mMenuChangeTeeView.setIcon(R.drawable.ic_icon_changetee)
        mMenuChangeTeeView.setText(context.getString(R.string.change_default_tee))
        mMenuChangeTeeView.setOnClickListener {
            closeView(CODE.CHANGE_DEFAULT_TEE)
        }
        addView(mMenuChangeTeeView)

        mMenuYardageBookView = ItemMenuMoreView(context)
        mMenuYardageBookView.setIcon(R.drawable.ic_icon_yardage_book)
        mMenuYardageBookView.setText(context.getString(R.string.view_green))
        mMenuYardageBookView.setOnClickListener {
            closeView(CODE.VIEW_GREEN)
        }
        addView(mMenuYardageBookView)

        mMenuReportView = ItemMenuMoreView(context)
        mMenuReportView.setIcon(R.drawable.ic_icon_report)
        mMenuReportView.setText(context.getString(R.string.report_hole))
        mMenuReportView.setOnClickListener {
            closeView(CODE.REPORT_THIS_HOLE)
        }
        addView(mMenuReportView)

        mMenuEndGameView = ItemMenuMoreView(context)
        mMenuEndGameView.setIcon(R.drawable.ic_icon_endgame)
        mMenuEndGameView.setText(context.getString(R.string.end_game))
        mMenuEndGameView.setOnClickListener {
            closeView(CODE.END_GAME)
        }
        addView(mMenuEndGameView)

    }

    private fun buildUnitView(){
        if (mUnitGolf == GoogleMapsHelper.UnitGolf.YARD){
            mMenuChangeUnitView.setText(context.getString(R.string.change_to_meter))
            mMenuChangeUnitView.setIcon(
                    R.drawable.ic_icon_change_metter
            )
        }else{
            mMenuChangeUnitView.setText(context.getString(R.string.change_to_yard))
            mMenuChangeUnitView.setIcon(
                    R.drawable.ic_icon_yard
            )
        }
    }

    fun setEvent(handleMenuMore: HandleMenuMore) {
        this.mHandleMenuMore = handleMenuMore
    }

    fun getUnit() = mUnitGolf

    fun hideYardBook() {
        mMenuYardageBookView.visibility = View.GONE
    }

    fun showYardBook() {
        mMenuYardageBookView.visibility = View.VISIBLE
    }

    fun openView() {
        if (visibility != View.VISIBLE) {
            val animation = AnimationUtils.loadAnimation(context, R.anim.show_right_to_left)
            visibility = View.VISIBLE
            startAnimation(animation)
        }
    }

    fun closeView(code: CODE? = null) {
        val animation = AnimationUtils.loadAnimation(context, R.anim.hide_left_to_right)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                code?.let { codeClear ->
                    mHandleMenuMore?.onClick(codeClear)
                }
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        startAnimation(animation)
    }

    fun isOpen(): Boolean = visibility == View.VISIBLE

    interface HandleMenuMore {
        fun onClick(code: CODE)
    }
}