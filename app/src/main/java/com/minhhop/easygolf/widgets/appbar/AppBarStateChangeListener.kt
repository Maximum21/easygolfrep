package com.minhhop.easygolf.widgets.appbar

import com.google.android.material.appbar.AppBarLayout

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {

    enum class STATE{
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private var mCurrentState = STATE.IDLE
    private var mCurrentOffset = 0f

    override fun onOffsetChanged(appBar: AppBarLayout?, i: Int) {
        appBar?.let {
            when {
                i == 0 -> {
                    if (mCurrentState != STATE.EXPANDED) {
                        onStateChanged(it, STATE.EXPANDED)
                    }
                    mCurrentState = STATE.EXPANDED
                }
                Math.abs(i) >= it.totalScrollRange -> {
                    if (mCurrentState != STATE.COLLAPSED) {
                        onStateChanged(it, STATE.COLLAPSED)
                    }
                    mCurrentState = STATE.COLLAPSED
                }
                else -> {
                    if (mCurrentState != STATE.IDLE) {
                        onStateChanged(it, STATE.IDLE)
                    }
                    mCurrentState = STATE.IDLE
                }
            }
            val offset = Math.abs(i / it.totalScrollRange.toFloat())
            if (offset != mCurrentOffset) {
                mCurrentOffset = offset
                onOffsetChanged(mCurrentState, offset)
            }
        }

    }

    fun getCurrentOffset(): Float = mCurrentOffset

    abstract fun onStateChanged(appBarLayout: AppBarLayout, state: STATE)

    abstract fun onOffsetChanged(state: STATE, offset: Float)
}