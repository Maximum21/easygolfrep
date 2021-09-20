package com.minhhop.easygolf.presentation.home

import android.content.Intent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.base.EasyGolfBottomSheetFragment
import com.minhhop.easygolf.framework.bundle.NotificationBundle
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.club.list.ClubHomeFragment
import com.minhhop.easygolf.presentation.custom.bottomnavigation.EasyGolfBottomBehavior
import com.minhhop.easygolf.presentation.custom.bottomnavigation.EasyGolfBottomBehavior.Tab.*
import com.minhhop.easygolf.presentation.custom.bottomnavigation.EasyGolfBottomNavigation
import com.minhhop.easygolf.presentation.golf.fragment.AlertNeedFinishRoundFragment
import com.minhhop.easygolf.presentation.home.chat.ChatHomeFragment
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedFragment
import com.minhhop.easygolf.views.activities.SettingsActivity
import kotlinx.android.synthetic.main.activity_easy_golf_home.*
import org.koin.android.viewmodel.ext.android.viewModel

class EasyGolfHomeActivity : EasyGolfActivity<EasyGolfHomeViewModel>(),
        EasyGolfBottomNavigation.Companion.CallbackBehavior, View.OnClickListener {
    override val mViewModel: EasyGolfHomeViewModel
            by viewModel()

    private var mAlertNeedFinishFragmentHold:EasyGolfBottomSheetFragment<*>? = null
    private val mListFragment = ArrayList<Fragment>()
    private var mHomeViewPagerAdapter: HomeViewPagerAdapter? = null
    override fun setLayout(): Int = R.layout.activity_easy_golf_home

    override fun initView() {
        /**
         * parse bundle for notifications
         * */
        NotificationBundle.extraBundle(intent.extras)?.let {
            directionNotificationBundle(it)
        }

        mViewModel.needShowAlertFinishRoundLiveData.observe(this, Observer {
            if(easygolfBottomNavigation.getTabActive() == GOLF) {
                if(mAlertNeedFinishFragmentHold == null) {
                    mAlertNeedFinishFragmentHold = AlertNeedFinishRoundFragment()
                    mAlertNeedFinishFragmentHold?.onAddCallback(object : EasyGolfBottomSheetFragment.BottomSheetFragmentListener{
                        override fun onDisappear() {
                            mAlertNeedFinishFragmentHold = null
                        }
                    })
                    mAlertNeedFinishFragmentHold?.show(supportFragmentManager)
                }
            }
        })
        if (mHomeViewPagerAdapter == null) {

            mListFragment.add(NewsFeedFragment())
            mListFragment.add(ClubHomeFragment())
            mListFragment.add(ChatHomeFragment())

            mHomeViewPagerAdapter = HomeViewPagerAdapter(this, mListFragment)
            easyGolfPagerHome.adapter = mHomeViewPagerAdapter

            easyGolfPagerHome.isUserInputEnabled = false
            easyGolfPagerHome.setCurrentItem(1, false)
        }
        /**
         * Bottom navigation
         * **/
        easygolfBottomNavigation.addOnCallbackBehavior(this)
        imgHamburgerHome.setOnClickListener(this)
        imgBellHome.setOnClickListener(this)

    }

    override fun loadData() {
        mViewModel.userLiveData.observe(this, Observer { user ->
            countDownHandicap.setValueCountDown(user.handicap)
            countDownFollowing.setValueCountDown(user.following)
            countDownFriends.setValueCountDown(user.friends)
        })
        mViewModel.fetchProfileUser()
    }

    override fun onResume() {
        super.onResume()
        mViewModel.checkNeedConfirmFinishRound()
    }

    override fun onPause() {
        super.onPause()
        mViewModel.destroyJobCheckRound()
    }

    override fun onChangeTab(tab: EasyGolfBottomBehavior.Tab) {
        val position = when (tab) {
            NEWS_FEED -> 0
            GOLF -> 1
            GROUP_CHAT -> 2
        }
        if (position == 1) {
            mViewModel.checkNeedConfirmFinishRound()
        } else {
            mViewModel.destroyJobCheckRound()
        }
        easyGolfPagerHome.setCurrentItem(position, false)
    }

    override fun onClick(v: View?) {
        v?.let { view ->
            when (view.id) {
                R.id.imgHamburgerHome -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.imgBellHome -> {
                    mViewModel.logout()
                    EasyGolfNavigation.signInDirection(this)
                    finish()
                }
            }
        }
    }
}