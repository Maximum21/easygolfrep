package com.minhhop.easygolf.listeners


/*
 * Created by woz on 4/5/18.
 */

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

abstract class EndlessRecyclerEndStackScrollListener protected constructor(private val mLayoutManager: RecyclerView.LayoutManager) : RecyclerView.OnScrollListener() {

    private var mLoading: Boolean = false
    private var mVisibleThreshold = 2
    private var mPreviousTotalItemCount = 0

    init {
        if (mLayoutManager is StaggeredGridLayoutManager) {

            mVisibleThreshold *= mLayoutManager.spanCount

        } else if (mLayoutManager is GridLayoutManager) {

            mVisibleThreshold *= mLayoutManager.spanCount
        }

    }


    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {

        var maxSize = 0
        val length = lastVisibleItemPositions.size
        if (length > 0) {
            maxSize = lastVisibleItemPositions[0]
        }
        for (i in 1 until length) {
            if (maxSize < lastVisibleItemPositions[i]) {
                maxSize = lastVisibleItemPositions[i]
            }
        }

        return maxSize
    }

    //    Callback method to be invoked when the RecyclerView has been scrolled.

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        var lastVisibleItemPosition = 0

        //        total item in recycler view
        val totalItemCount = mLayoutManager.itemCount

        when (mLayoutManager) {
            is StaggeredGridLayoutManager -> {
                val firstVisibleItemPositions = mLayoutManager.findLastVisibleItemPositions(null)

                lastVisibleItemPosition = getLastVisibleItem(firstVisibleItemPositions)
            }
            is GridLayoutManager -> lastVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
            is LinearLayoutManager -> lastVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()
        }


        if (totalItemCount > mPreviousTotalItemCount) {
            if(totalItemCount == 0)
            this.mLoading = true
        }

        if (mLoading && (totalItemCount > mPreviousTotalItemCount)) {
            mLoading = false
            mPreviousTotalItemCount = totalItemCount
        }

        if (!mLoading && lastVisibleItemPosition == 0) {
            mLoading = true
            onLoadMore()
        }
    }

    abstract fun onLoadMore()

    fun resetState() {

        this.mPreviousTotalItemCount = 0
        this.mLoading = true
    }


}
