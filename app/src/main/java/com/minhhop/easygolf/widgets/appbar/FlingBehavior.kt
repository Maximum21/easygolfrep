package com.minhhop.easygolf.widgets.appbar

import android.content.Context

import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout

class FlingBehavior : AppBarLayout.Behavior {
    constructor() : super()
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)


    companion object{
        const val TOP_CHILD_FLING_THRESHOLD = 3
    }



    private var isPositive: Boolean = false


    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
                               velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {


        var mVelocityY = velocityY

        if (velocityY > 0 && !isPositive || velocityY < 0 && isPositive) {
            mVelocityY *= -1
        }

        var mConsumed = consumed
        if (target is RecyclerView && velocityY < 0) {
            val firstChild = target.getChildAt(0)
            val childAdapterPosition = target.getChildAdapterPosition(firstChild)
            mConsumed = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD
        }

        return super.onNestedFling(coordinatorLayout, child, target, velocityX, mVelocityY, mConsumed)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
                                   dx: Int, dy: Int, consumed: IntArray, type: Int) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        isPositive = dy > 0
    }
}