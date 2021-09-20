package com.minhhop.easygolf.presentation.club.detail

import android.Manifest
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayoutMediator
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.core.domain.profile.People
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.extension.loadImage
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.club.detail.overview.ClubOverviewFragment
import com.minhhop.easygolf.presentation.club.detail.photo.ClubPhotoFragment
import com.minhhop.easygolf.presentation.club.detail.review.ClubReviewFragment
import kotlinx.android.synthetic.main.acitivity_user_profile_demo.*
import kotlinx.android.synthetic.main.activity_easy_golf_course_detail.*
import kotlinx.android.synthetic.main.layout_loading_map.*
import kotlinx.android.synthetic.main.layout_loading_white.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.android.viewmodel.ext.android.viewModel


class EasyGolfClubDetailActivity : EasyGolfActivity<EasyGolfClubDetailViewModel>() {
    private var followersAdapter: FollowersAdapter? = null
    override val mViewModel: EasyGolfClubDetailViewModel by viewModel()

    private var mClubOverviewFragment: ClubOverviewFragment? = null
    private var mClubPhotoFragment: ClubPhotoFragment? = null
    private var mClubReviewFragment: ClubReviewFragment? = null

    override fun setLayout(): Int = R.layout.activity_easy_golf_course_detail

    override fun initView() {
        EasyGolfNavigation.easyGolfCourseDetailBundle(intent)?.let { courseBundle ->
            photoCourse.loadImage(courseBundle.photo)
            initViewPager()
            mViewModel.getClubDetail(courseBundle.id)
        } ?: finish()

        collapsingToolbarLayout.setExpandedTitleColor(ContextCompat.getColor(this,
                android.R.color.transparent))

        collapsingToolbarLayout.setCollapsedTitleTextColor(ContextCompat.getColor(this,
                R.color.colorWhite))
        actionCall.setOnClickListener {
            mViewModel.clubDataLive.value?.phone_number?.let { phoneNumber ->
                EasyGolfNavigation.makeAPhoneCall(this, phoneNumber)
            }
        }

        actionDirection.setOnClickListener {
            mViewModel.clubDataLive.value?.coordinate?.let { coordinate ->
                EasyGolfNavigation.launchTurnByTurnNavigation(this, coordinate.lat, coordinate.lon) {
                    showCommonMessage(it ?: getString(R.string.unknown_error))
                }
            }
        }

        actionScorecards.setOnClickListener {
            mViewModel.currentCourseLiveData.value?.let { course ->
                EasyGolfNavigation.scoreCardCourseDirection(this, course.scorecard)
            }
        }
        actionFollow.setOnClickListener {
            if(progressingFollow.visibility != View.VISIBLE){
                mViewModel.onFollowOrUnFollowClub()
                progressingFollow.visibility = View.VISIBLE
            }
        }
    }

    fun viewLoadingMap() {
        layoutLoading.visibility = View.VISIBLE
    }

    private fun hideLoadingMap() {
        layoutLoading.visibility = View.GONE
    }

    override fun onResume() {
        hideLoadingMap()
        super.onResume()
    }
    private fun setFollowers(followers: FollowersResponse) {
        followers?.data?.let {
            if(it.isNotEmpty()){
                followers_group.visibility = View.VISIBLE
                val mLayoutManager = LinearLayoutManager(applicationContext)
                mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                followers_recyclerviw.layoutManager = mLayoutManager
                followersAdapter = FollowersAdapter(this,1)
                followers_recyclerviw.adapter = followersAdapter
                val tempUsers : ArrayList<People> = ArrayList()
                if(followers.data.size>6){
                    for(x in 0..5){
                        tempUsers.add(followers.data[x])
                    }
                    followers_tv.text = "+${(it.size-6).toString()}"
                }
                followersAdapter?.setDataList(tempUsers)
            }
        }
    }

    override fun viewMask() {
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            super.viewMask()
            viewMask.startBurnViewAnimation()
        } else {
            layoutLoadingLight.visibility = View.VISIBLE
        }
    }

    override fun hideMask() {
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            super.hideMask()
            viewMask.stopBurnViewAnimation()
            mStatusLoading = StatusLoading.FINISH_LOAD
        } else {
            layoutLoadingLight.visibility = View.GONE
        }
    }

    override fun loadData() {
        viewMask()
        mViewModel.clubDataLive.observe(this, Observer {
            collapsingToolbarLayout.title = it.name
            hideMask()
        })
        mViewModel.followLiveData.observe(this, Observer { isFollowing->
            if(isFollowing == true){
                actionFollow.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this,R.drawable.ic_icon_following),null,null)
                actionFollow.text = getString(R.string.following)
            }else{
                actionFollow.setCompoundDrawablesRelativeWithIntrinsicBounds(null,
                        ContextCompat.getDrawable(this,R.drawable.ic_icon_follow),null,null)
                actionFollow.text = getString(R.string.follow)
            }
            progressingFollow.visibility = View.GONE
        })
    }

    private fun initViewPager() {
        mClubOverviewFragment = (supportFragmentManager.findFragmentByTag(ClubOverviewFragment.TAG) as? ClubOverviewFragment)
                ?: ClubOverviewFragment()
        mClubPhotoFragment = (supportFragmentManager.findFragmentByTag(ClubPhotoFragment.TAG) as? ClubPhotoFragment)
                ?: ClubPhotoFragment()
        mClubReviewFragment = (supportFragmentManager.findFragmentByTag(ClubReviewFragment.TAG) as? ClubReviewFragment)
                ?: ClubReviewFragment()

        val listFragment = ArrayList<Fragment>()
        listFragment.add(mClubOverviewFragment!!)
        listFragment.add(mClubPhotoFragment!!)
        listFragment.add(mClubReviewFragment!!)

        val pagerAdapter = ClubDetailPagerAdapter(this, listFragment)
        pagerCourseDetail.adapter = pagerAdapter
        pagerCourseDetail.isUserInputEnabled = false
        TabLayoutMediator(tabCourseDetail, pagerCourseDetail, false, false) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.overview)
                1 -> getString(R.string.photos)
                else -> getString(R.string.reviews)
            }
        }.attach()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Contains.REQUEST_CODE_PHONE_CALL_PERMISSION && permissions.contains(Manifest.permission.CALL_PHONE)) {
            mViewModel.clubDataLive.value?.phone_number?.let { phoneNumber ->
                EasyGolfNavigation.makeAPhoneCall(this, phoneNumber)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: MessageEvent?) {
        finish()
    }
}