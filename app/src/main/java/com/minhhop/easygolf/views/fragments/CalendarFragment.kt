package com.minhhop.easygolf.views.fragments


import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.fragment.WozBaseFragment
import com.minhhop.easygolf.views.fragments.schedule.HistoryScheduleFragment
import com.minhhop.easygolf.views.fragments.schedule.ScheduleFragment

class CalendarFragment: WozBaseFragment() {

    enum class TAB{
        LEFT,
        RIGHT

    }

    private var mTabSelected = TAB.RIGHT
    private lateinit var mActive: Fragment

    private lateinit var mFm: FragmentManager
//    private val mScheduleFragment = ScheduleFragment()
//    private val mHistoryScheduleFragment = HistoryScheduleFragment()

    private val mScheduleFragment = ComingSoonFragment()
    private val mHistoryScheduleFragment = ComingSoonFragment()

    private lateinit var mViewTabSchedule: View
    private lateinit var mViewTabHistorySchedule: View

    override fun setLayout(): Int = R.layout.fragment_booking

    override fun loadData() {
    }

    override fun initView(viewRoot: View) {
        mViewTabSchedule = viewRoot.findViewById(R.id.tabLeft)
        mViewTabSchedule.setOnClickListener(this)

        mViewTabHistorySchedule = viewRoot.findViewById(R.id.tabRight)
        mViewTabHistorySchedule.setOnClickListener(this)


        mActive = mScheduleFragment

        mFm = childFragmentManager
        mFm.beginTransaction().add(R.id.tabContent, mScheduleFragment).commit()
        mFm.beginTransaction().add(R.id.tabContent, mHistoryScheduleFragment).hide(mHistoryScheduleFragment).commit()
    }


    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when(id){
                R.id.tabLeft->{
                    if(mActive !is ScheduleFragment) {
                        mFm.beginTransaction().hide(mActive).show(mScheduleFragment).commit()
                        mActive = mScheduleFragment
                        mTabSelected = TAB.LEFT
                        initBackgroundTab()
                    }
                }
                R.id.tabRight->{
                    if(mActive !is HistoryScheduleFragment) {
                        mFm.beginTransaction().hide(mActive).show(mHistoryScheduleFragment).commit()
                        mActive = mHistoryScheduleFragment
                        mTabSelected = TAB.RIGHT
                        initBackgroundTab()
                    }
                }
            }
        }
    }

    private fun initBackgroundTab() {
        context?.let {
            when (mTabSelected) {
                TAB.LEFT -> {
                    mViewTabSchedule.background =  ContextCompat.getDrawable(it,R.drawable.background_tab_courses_selected)
                    mViewTabHistorySchedule.background =  ContextCompat.getDrawable(it,R.drawable.background_tab_plan)
                }
                TAB.RIGHT -> {
                    mViewTabSchedule.background =  ContextCompat.getDrawable(it,R.drawable.background_tab_courses)
                    mViewTabHistorySchedule.background =  ContextCompat.getDrawable(it,R.drawable.background_tab_plan_selected)
                }
            }
        }

    }
}