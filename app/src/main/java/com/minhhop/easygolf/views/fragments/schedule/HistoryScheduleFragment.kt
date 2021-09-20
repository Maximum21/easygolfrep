package com.minhhop.easygolf.views.fragments.schedule

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ScheduleAdapter
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.framework.models.TournamentPaging
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.views.fragments.schedule.listeners.OnScheduleListener

class HistoryScheduleFragment : WozBaseFragment(), OnScheduleListener {


    private var mStartPaging = 0
    private val mMaxItemNeedToLoad = 20

    private var mScheduleAdapter: ScheduleAdapter? = null

    override fun setLayout(): Int = R.layout.fragment_history_schedule

    override fun initView(viewRoot: View) {
        context?.let {
            mScheduleAdapter = ScheduleAdapter(it,this@HistoryScheduleFragment)

            val listSchedule: RecyclerView = viewRoot.findViewById(R.id.listSchedule)
            val layout = LinearLayoutManager(it)
            listSchedule.apply {
                layoutManager = layout
                adapter = mScheduleAdapter
            }

            mScheduleAdapter?.registerLoadMore(layout,listSchedule,({
                if(mStartPaging > 0) {
                    loadData()
                }
            }))

        }
    }

    override fun onClick(imgCourse: ImageView, txtName: TextView, tournament: Tournament) {

    }

    override fun refreshData() {
        mStartPaging = 0
        mScheduleAdapter?.openReachEnd()
        super.refreshData()
    }

    override fun loadData() {
        registerResponse(ApiService.getInstance().golfService.getTournaments(mStartPaging,mMaxItemNeedToLoad,"past"),
                object : HandleResponse<TournamentPaging> {
                    override fun onSuccess(result: TournamentPaging) {
                        if(mState == STATE.REFRESH || mState == STATE.FIRST_LOAD){
                            hideLayoutRefresh()
                            mScheduleAdapter?.setDataList(result.items)
                            mState = STATE.LOAD_MORE
                        }else{
                            mScheduleAdapter?.addListItem(result.items)
                        }

                        mStartPaging = result.paginator.start
                        if(result.paginator.start < 0){
                            mScheduleAdapter?.onReachEnd()

                        }


                    }

                })
    }
}