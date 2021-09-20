package com.minhhop.easygolf.presentation.club.history

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.RoundAdapter
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.framework.models.RoundMatch
import com.minhhop.easygolf.listeners.EventLoadMore
import com.minhhop.easygolf.listeners.RoundListener
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.models.RoundPager
import com.minhhop.easygolf.presentation.endgame.EndGameActivity
import java.util.*

class PlanFragment : WozBaseFragment(), RoundListener, EventLoadMore {
    private var mStart = 0
    private var mRoundAdapter: RoundAdapter? = null
    private var isReadyClick = false

    override fun initView(viewRoot: View) {
        val view = viewRoot.findViewById<RecyclerView>(R.id.listCourses)
        val linearLayoutManager = LinearLayoutManager(context)
        view.layoutManager = linearLayoutManager
        mRoundAdapter = RoundAdapter(Objects.requireNonNull<Context>(context), this)
        view.adapter = mRoundAdapter
        mRoundAdapter!!.registerLoadMore(linearLayoutManager, view, this)

    }

    override fun setLayout(): Int {
        return R.layout.fragment_plan
    }

    override fun loadData() {

        registerResponse<RoundPager>(ApiService.getInstance().golfService.getPlan(mStart),
                object: HandleResponse<RoundPager>{
                    override fun onSuccess(result: RoundPager) {
                        if (mState === STATE.FIRST_LOAD || mState === STATE.REFRESH) {
                            mRoundAdapter!!.setDataList(result.rounds)
                            mState = STATE.LOAD_MORE
                            hideLayoutRefresh()
                        } else {
                            mRoundAdapter!!.addListItem(result.rounds)
                        }

                        mStart = result.paginator.start
                        if (mStart < 0) {
                            mRoundAdapter!!.onReachEnd()
                        }
                        hideLoading()
                    }

                })
    }

    override fun onClick(roundMatch: RoundMatch, imgCourse: ImageView?, txtName: TextView) {
        if (!isReadyClick) {
            isReadyClick = true
            val bundle = Bundle()
            bundle.putString(Contains.EXTRA_ID_ROUND_BATTLE, roundMatch.id.toString())
            bundle.putBoolean(Contains.EXTRA_IS_SHOW_LIKE_HISTORY, true)
            bundle.putString(Contains.EXTRA_ID_CLUB, roundMatch.course_id)
            bundle.putString(Contains.EXTRA_NAME_COURSE, roundMatch.course.name)
            startActivity(EndGameActivity::class.java, bundle)
        }
    }

    override fun onResume() {
        super.onResume()
        isReadyClick = false
    }

    override fun refreshData() {
        mStart = 0
        mRoundAdapter!!.openReachEnd()
        super.refreshData()
    }


    internal fun openRefreshData() {
        refreshData()
    }

    override fun onLoadMore() {
        if (mStart > 0) {
            loadData()
        }
    }
}
