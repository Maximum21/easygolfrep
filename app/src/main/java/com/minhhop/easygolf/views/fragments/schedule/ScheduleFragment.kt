package com.minhhop.easygolf.views.fragments.schedule

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ScheduleAdapter
import com.minhhop.easygolf.base.fragment.HandleResponse
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.framework.models.TournamentPaging
import com.minhhop.easygolf.services.ApiService
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.views.activities.ScheduleCreateActivity
import com.minhhop.easygolf.views.activities.ScheduleDetailActivity
import com.minhhop.easygolf.views.fragments.schedule.listeners.OnScheduleListener

class ScheduleFragment : WozBaseFragment(), OnScheduleListener {
    private var mStartPaging = 0
    private val mMaxItemNeedToLoad = 20

    private var mScheduleAdapter: ScheduleAdapter? = null
    private lateinit var mFabAddSchedule: FloatingActionButton

    override fun setLayout(): Int  = R.layout.fragment_schedule

    override fun initView(viewRoot: View) {

        mFabAddSchedule = viewRoot.findViewById(R.id.fabAdd)
        context?.let {
            mScheduleAdapter = ScheduleAdapter(it,this@ScheduleFragment)

            val listSchedule: RecyclerView = viewRoot.findViewById(R.id.listSchedule)
            val layout = LinearLayoutManager(it)
            listSchedule.apply {
                layoutManager = layout
                adapter = mScheduleAdapter


                addOnScrollListener(object :RecyclerView.OnScrollListener(){
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        if(dy > 0  && mFabAddSchedule.isShown){
                            mFabAddSchedule.hide()
                        }

                        if(dy < 0  && !mFabAddSchedule.isShown){
                            mFabAddSchedule.show()
                        }

                    }
                })

            }

            mScheduleAdapter?.registerLoadMore(layout,listSchedule,({
                if(mStartPaging > 0) {
                    Log.e("WOW","load more")
                    loadData()
                }
            }))

        }

        viewRoot.findViewById<View>(R.id.fabAdd).setOnClickListener {
            startActivity(ScheduleCreateActivity::class.java)
        }
    }

    override fun onClick(imgCourse: ImageView, txtName: TextView,tournament: Tournament) {
        activity?.apply {
            val bundle = Bundle()
            bundle.putString(Contains.EXTRA_IMAGE_COURSE,tournament.photo)
            bundle.putString(Contains.EXTRA_NAME_COURSE,tournament.name)
            bundle.putInt(Contains.EXTRA_ID_TOURNAMENT,tournament.id)
            val intent = Intent(activity, ScheduleDetailActivity::class.java)
            intent.putExtras(bundle)

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                    Pair.create<View,String>(imgCourse, getString(R.string.transition_name)),
                    Pair.create<View,String>(txtName, "name"))
            startActivity(intent, options.toBundle())
        }

    }

    override fun refreshData() {
        mStartPaging = 0
        mScheduleAdapter?.openReachEnd()
        super.refreshData()
    }

    override fun loadData() {
        registerResponse(ApiService.getInstance().golfService.getTournaments(mStartPaging,mMaxItemNeedToLoad,null),
                object : HandleResponse<TournamentPaging>{
            override fun onSuccess(result: TournamentPaging) {
                if(mState == STATE.REFRESH || mState == STATE.FIRST_LOAD){
                    Log.e("WOW","result 01: ${result.paginator.total}")
                    hideLayoutRefresh()
                    mScheduleAdapter?.setDataList(result.items)
                    mState = STATE.LOAD_MORE
                }else{
                    Log.e("WOW","result 02: ${result.paginator.total}")
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