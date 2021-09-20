package com.minhhop.easygolf.presentation.handicap

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.fragment.WozBaseFragment

class HandicapHomeFragment : WozBaseFragment() {

    private var mOpenTabRound = false
    private var mTabLeftActive = true
    private lateinit var mFm: FragmentManager
    private lateinit var mContentHandicapFragment: ContentHandicapFragment
    private lateinit var mRankHandicapFragment: RankHandicapFragment
    private lateinit var mActive: Fragment

    private lateinit var mViewTabLeft: View
    private lateinit var mViewTabRight: View


    override fun setLayout(): Int = R.layout.fragment_handicap_home

    override fun initView(viewRoot: View) {

        mViewTabLeft = viewRoot.findViewById(R.id.tabLeft)
        mViewTabLeft.setOnClickListener(this)
        mViewTabRight = viewRoot.findViewById(R.id.tabRight)
        mViewTabRight.setOnClickListener(this)

        mFm = childFragmentManager
        mContentHandicapFragment = ContentHandicapFragment()
        mRankHandicapFragment = RankHandicapFragment()

        mActive = mContentHandicapFragment
        mFm.beginTransaction().add(R.id.tabContent, mContentHandicapFragment).commit()
        mFm.beginTransaction().add(R.id.tabContent, mRankHandicapFragment).hide(mRankHandicapFragment).commit()
    }

    override fun loadData() {

    }

    override fun onClick(v: View?) {
        super.onClick(v)
        v?.apply {
            when (id) {
                R.id.tabLeft -> if (!mTabLeftActive) {
                    mFm.beginTransaction().hide(mActive).show(mContentHandicapFragment).commit()
                    mActive = mContentHandicapFragment
                    mTabLeftActive = true
                    initBackgroundTab()
                }
                R.id.tabRight -> if (mTabLeftActive) {
                    mFm.beginTransaction().hide(mActive).show(mRankHandicapFragment).commit()
                    mActive = mRankHandicapFragment
                    mTabLeftActive = false
                    initBackgroundTab()
                }
            }
        }
    }

    private fun initBackgroundTab() {
        if (mTabLeftActive) {
            mViewTabLeft.background = resources.getDrawable(R.drawable.background_tab_courses_selected)
            mViewTabRight.background = resources.getDrawable(R.drawable.background_tab_plan)
        } else {
            mViewTabLeft.background = resources.getDrawable(R.drawable.background_tab_courses)
            mViewTabRight.background = resources.getDrawable(R.drawable.background_tab_plan_selected)
        }
    }
}