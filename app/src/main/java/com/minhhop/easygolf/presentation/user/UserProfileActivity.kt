package com.minhhop.easygolf.presentation.user

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.gson.Gson
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.profile.*
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.dialogs.ConfirmationDialog
import com.minhhop.easygolf.presentation.custom.EasyGolfButton
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.presentation.like.LikesFragment
import kotlinx.android.synthetic.main.acitivity_user_profile_demo.*
import kotlinx.android.synthetic.main.activity_user_profile.add_user_feed_post_group
import kotlinx.android.synthetic.main.activity_user_profile.add_user_round_group
import kotlinx.android.synthetic.main.activity_user_profile.countDownFollowing
import kotlinx.android.synthetic.main.activity_user_profile.countDownFriends
import kotlinx.android.synthetic.main.activity_user_profile.countDownHandicap
import kotlinx.android.synthetic.main.activity_user_profile.include_round_statistics_row
import kotlinx.android.synthetic.main.activity_user_profile.settings_act_user_profile_iv
import kotlinx.android.synthetic.main.activity_user_profile.user_details_statistics_group
import kotlinx.android.synthetic.main.activity_user_profile.user_profile_name_main_tv
import kotlinx.android.synthetic.main.news_feed_row_layout.*
import kotlinx.android.synthetic.main.round_row_for_user.*
import org.koin.android.ext.android.inject


class UserProfileActivity : EasyGolfActivity<UserProfileViewModel>(), View.OnClickListener,
        SendRequestBottomFragment.SendRequestBottomFragmentListener {
    private var mineUser: Boolean = false
    private var followersAdapter: FollowersAdapter? = null
    private var mainData: PeopleResponse? = null
    override val mViewModel: UserProfileViewModel
            by inject()
    var id: String = ""
    override fun setLayout(): Int = R.layout.acitivity_user_profile_demo

    companion object {
        const val FRIENDS = 1
        const val ADD_FRIEND = 2
        const val PENDING = 3
        const val ACCEPT = 4
        const val DECLINE = 5
        const val FOLLOWING = 6
        const val FOLLOW = 7
    }

    override fun initView() {
        id = intent.getStringExtra("id") ?: ""
        btnFriends.setOnClickListener(this)
        btnFollow.setOnClickListener(this)
        back_btn.setOnClickListener(this)
        feedPostFwdArrow.setOnClickListener(this)
        RHFwdArrow.setOnClickListener(this)
        followers_tv.setOnClickListener(this)
        mViewModel.profileDetails.observe(this, Observer {
            setData(it)
            mainData = it
            hideMask()
        })
        mViewModel.mCommonErrorLive.observe(this, Observer {
            mainData?.stage?.let {
                setStages(it)
            }
            hideMask()
        })
        mViewModel.requestAction.observe(this, Observer {
            setData(it)
            btnFriends.setStatus(EasyGolfButton.Status.IDLE)
            btnFollow.setStatus(EasyGolfButton.Status.IDLE)
            hideMask()
        })
    }

    private fun setData(data: PeopleResponse) {
        if (data.profile.avatar != null && data.profile.avatar!!.isNotEmpty()) {
            Glide.with(this).load(data.profile.avatar)
                    .error(R.drawable.ic_icon_user_default)
                    .into(settings_act_user_profile_iv)
        }
        data.profile.let { user ->
            countDownHandicap.setValueCountDown(user.handicap)
            countDownFollowing.setValueCountDown(user.following)
            countDownFriends.setValueCountDown(user.friends)
            user.fullname?.let {
                user_profile_name_main_tv.text = it
            }
            user.code?.let{
                user_profile_member_id_tv.text = "Member ID: $it"
            }
        }
        data.feed?.let { feed ->
            if (feed.isNotEmpty()) {
                add_user_feed_post_group.visibility = View.GONE
                setPostData(feed[0],data.profile)
            }
        }
        data.stage?.let { stage ->
            mineUser = true
            buttons_group.visibility = View.VISIBLE
            setStages(stage)
        }
        data.round?.let {
            if (it.id != null && it.id.isNotEmpty()) {
                add_user_round_group.visibility = View.GONE
                setRoundData(it)
            }
        }
        data.statistic?.let {
            if (it?.isNotEmpty()) {
                user_details_statistics_group.visibility = View.GONE
                setStatistics(it)
            }
        }
        data.followers?.let {
            setFollowers(it)
        }
    }

    private fun setFollowers(people: PeopleObject) {
        people?.data?.let {
            if(it.isNotEmpty()){
                followers_group.visibility = View.VISIBLE
                val mLayoutManager = LinearLayoutManager(applicationContext)
                mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
                followers_recyclerviw.layoutManager = mLayoutManager
                followersAdapter = FollowersAdapter(this,1)
                followers_recyclerviw.adapter = followersAdapter
                val tempUsers : ArrayList<People> = ArrayList()
                if(people.data.size>6){
                    for(x in 0..5){
                        tempUsers.add(people.data[x])
                    }
                    followers_tv.text = "+${(it.size-6).toString()}"
                    followers_tv.visibility = View.VISIBLE
                }else{
                    followers_tv.visibility = View.GONE
                }
                followersAdapter?.setDataList(tempUsers)
            }
        }
    }

    private fun setStatistics(stats: ArrayList<Statistic>) {
        val barChart = findViewById<View>(R.id.barchart) as BarChart
        if (stats.isEmpty()) {
            include_round_statistics_row.layoutParams.height = Utils.setLayoutHeight(500.0)
            include_round_statistics_row.requestLayout()
        }
        val entries: ArrayList<BarEntry> = ArrayList()
        var x = 1
        for (temp in stats) {
            entries.add(BarEntry(x.toFloat(), temp.score.toFloat()))
            x++
        }
        val set = BarDataSet(entries, "")
        set.setColor(ContextCompat.getColor(this, R.color.colorPrimary))
        val data = BarData(set)
        data.barWidth = 0.5f
        barChart.data = data
        barChart.setTouchEnabled(false);
        barChart.isClickable = false;
        barChart.isDoubleTapToZoomEnabled = false;
        barChart.isDoubleTapToZoomEnabled = false;

        barChart.setDrawBorders(false);
        barChart.setDrawGridBackground(false);

        barChart.description.isEnabled = false;
        barChart.legend.isEnabled = false;

        barChart.xAxis.setDrawGridLines(false);
        barChart.xAxis.setDrawLabels(false);
        barChart.xAxis.setDrawAxisLine(false);

        barChart.axisRight.setDrawGridLines(false);
        barChart.axisRight.setDrawLabels(false);
        barChart.axisRight.setDrawAxisLine(false);
        barChart.invalidate()

    }

    private fun setRoundData(round: Round) {
        var clubName = ""
        round.club_name?.let {
            if (it.isNotEmpty()) {
                clubName = it
            }
        }
        round.course_name?.let {
            if (it.isNotEmpty()) {
                clubName = "$clubName($it)"
            }
        }

        round_history_row_club_name.text = clubName
        round.date?.let {
            round_history_row_date_tv.text = AppUtil.formatStringDate(it, "dd/MM/yyyy HH:mm")
        }

        round_history_row_tepar_count.text = round.hdc.toInt().toString()
        round_history_row_strokes_count.text = round.scores.toInt().toString()
        round_history_row_thru_count.text = round.thru.toString()

        round.over.let {
            if (0 == it.toInt()) {
                round_history_row_grossnet_values_tv.text = "E"
            } else {
                round_history_row_grossnet_values_tv.text = it.toInt().toString()
            }
        }
    }

    private fun setStages(stage: Stage) {
        if (stage.waiting) {
            setButtonState(ACCEPT)
        } else if (stage.pending) {
            setButtonState(PENDING)
        } else if (stage.friend) {
            setButtonState(FRIENDS)
        } else {
            setButtonState(ADD_FRIEND)
        }

        if (stage.waiting) {
            setButtonState(DECLINE)
        } else if (stage.following) {
            setButtonState(FOLLOWING)
        } else {
            setButtonState(FOLLOW)
        }
    }

    private fun setButtonState(tag: Int) {
        when (tag) {
            //friends
            FRIENDS -> {
                btnFriends.background = ContextCompat.getDrawable(this, R.drawable.option_selected_gray_border_background_transparent)
                btnFriends.setText(resources.getString(R.string.friends))
                ContextCompat.getDrawable(this, R.drawable.ic_icon_correct_big_brother)?.let {
                    btnFriends.setDrawable(it)
                }
                btnFriends.setTextColor(R.color.colorwriteBlue)
                btnFriends.setMTag(tag)
            }
            //add friends
            ADD_FRIEND -> {
                ContextCompat.getDrawable(this, R.drawable.ic_add_user_white_clear)?.let {
                    btnFriends.setDrawable(it)
                }
                btnFriends.background = ContextCompat.getDrawable(this, R.drawable.background_easygolf_button)
                btnFriends.setText(resources.getString(R.string.add_friend))
                btnFriends.setTextColor(R.color.colorWhite)
                btnFriends.setMTag(tag)
            }
            //pending
            PENDING -> {
                btnFriends.setDrawable(null)
                btnFriends.background = ContextCompat.getDrawable(this, R.drawable.gray_background_filled)
                btnFriends.setText(resources.getString(R.string.pending))
                btnFriends.setTextColor(R.color.colorWhite)
                btnFriends.setMTag(tag)
            }
            //accept
            ACCEPT -> {
                btnFriends.setDrawable(null)
                btnFriends.background = ContextCompat.getDrawable(this, R.drawable.gray_background_filled)
                btnFriends.setText(resources.getString(R.string.accept))
                btnFriends.setTextColor(R.color.colorWhite)
                btnFriends.setMTag(tag)
            }
            //decline
            DECLINE -> {
                btnFollow.setDrawable(null)
                btnFollow.background = ContextCompat.getDrawable(this, R.drawable.option_selected_gray_border_background_transparent)
                btnFollow.setText(resources.getString(R.string.decline))
                btnFollow.setTextColor(R.color.colorwriteBlue)
                btnFollow.setMTag(tag)
            }
            //following
            FOLLOWING -> {
                ContextCompat.getDrawable(this, R.drawable.ic_icon_correct_big_brother)?.let {
                    btnFollow.setDrawable(it)
                }
                btnFollow.background = ContextCompat.getDrawable(this, R.drawable.option_selected_gray_border_background_transparent)
                btnFollow.setText(resources.getString(R.string.following))
                btnFollow.setTextColor(R.color.colorwriteBlue)
                btnFollow.setMTag(tag)
            }
            //follow
            FOLLOW -> {
                ContextCompat.getDrawable(this, R.drawable.ic_icon_follow)?.let {
                    btnFollow.setDrawable(it)
                }
                btnFollow.background = ContextCompat.getDrawable(this, R.drawable.easygolffollowbackground)
                btnFollow.setText(resources.getString(R.string.follow))
                btnFollow.setTextColor(R.color.colorDarkBlue)
                btnFollow.setMTag(tag)
//                img.setColorFilter(R.color.colorDarkBlue,PorterDuff.Mode.MULTIPLY);
            }
        }

    }

    private fun setPostData(model: NewsFeed, user: People) {
        post_profile_iv_layout.setData(model, user.id, 1)
    }

    override fun loadData() {
        if (id.isNotEmpty()) {
            viewMask()
            mViewModel.fetchProfileDetails(id)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnFriends -> {
                btnFriends.setStatus(EasyGolfButton.Status.PROCESS)
                when (btnFriends.getMTag()) {
                    ADD_FRIEND -> {
                        SendRequestBottomFragment()
                                .setName("")
                                .addListener(this)
                                .show(supportFragmentManager, SendRequestBottomFragment.TAG)
                    }
                    FRIENDS -> {
                        ConfirmationDialog(this, "Are you sure you want to delete this user from friends?", "Delete", "Cancel",
                                DialogInterface.OnCancelListener { },
                                DialogInterface.OnCancelListener {
                                    mViewModel.requestAction(id, "", 5)
                                }).show()
                    }
                    ACCEPT -> {
                        mViewModel.requestAction(id, "", 3)
                    }
                }

            }
            R.id.btnFollow -> {
                btnFollow.setStatus(EasyGolfButton.Status.PROCESS)
                when (btnFollow.getMTag()) {
                    FOLLOW -> {
                        mViewModel.requestAction(id, "", 1)
                    }
                    FOLLOWING -> {
                        mViewModel.requestAction(id, "", 2)
                    }
                    DECLINE -> {
                        mViewModel.requestAction(id, "", 4)
                    }
                }
            }
            R.id.back_btn->{
                finish()
            }
            R.id.feedPostFwdArrow->{
                EasyGolfNavigation.feedPostDirection(this,mainData?.profile?.profile_id?:"")
            }
            R.id.RHFwdArrow->{
                EasyGolfNavigation.roundHistoryDirection(this)

            }
            R.id.followers_tv->{
                showFollowersFragment()
            }
        }
    }

    private fun showFollowersFragment() {
        mainData?.followers?.data?.let{
            if(it.isNotEmpty()){
                val ft = supportFragmentManager.beginTransaction()
                val fragment = LikesFragment()
                val bundle = Bundle()
                bundle.putInt("tag",1)
                bundle.putString("data", Gson().toJson(it))
                fragment.arguments = bundle
                ft.replace(R.id.viewRoot,fragment,"followersfragment").addToBackStack(null).commit()
            }
        }
    }

    override fun onSubmit(message: String) {
        setButtonState(3)
        mViewModel.requestAction(id, message, 0)
    }
}