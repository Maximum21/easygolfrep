package com.minhhop.easygolf.presentation.userfeed

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.NewsFeedAdapter
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedFragment
import com.minhhop.easygolf.presentation.home.newsfeed.NewsFeedViewModel
import com.minhhop.easygolf.views.activities.PostDetailsActivity
import kotlinx.android.synthetic.main.activity_feeds.*
import org.koin.android.ext.android.inject

class FeedsActivity : EasyGolfActivity<NewsFeedViewModel>(), NewsFeedAdapter.NewsFeedEvent, View.OnClickListener {


    private var clubId: String = ""
    private var userID: String = ""
    private var currentPositon: Int = -1
    override val mViewModel: NewsFeedViewModel by inject()
    private var newsFeedAdapter: NewsFeedAdapter? = null
    private var tags = "me"
    override fun setLayout(): Int = R.layout.activity_feeds

    companion object {
        const val POST_DETAILS_REQUESTCODE: Int = 1111
    }

    override fun initView() {
        val bundle = EasyGolfNavigation.PostFeedBundle(intent)
        bundle?.userId?.let{
            userID = it
        }
        bundle?.clubId?.let{
            clubId = it
        }

        val layoutManager = LinearLayoutManager(this)
        feedPostRecyclerView.layoutManager = layoutManager
        newsFeedAdapter = NewsFeedAdapter(this, this, mViewModel.getUser()!!)
        feedPostRecyclerView.adapter = newsFeedAdapter
        newsFeedAdapter!!.registerLoadMore(layoutManager, feedPostRecyclerView) {
            if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                mViewModel.fetchUserPosts(tags)
            }
        }

        mViewModel.newsFeedPagerLive.observe(this, Observer {
            hideMask()
            if (it?.data?.isNotEmpty()?:false) {
                if (mStatusLoading == StatusLoading.REFRESH_LOAD
                        || mStatusLoading == StatusLoading.FIRST_LOAD) {
                    newsFeedAdapter?.setDataList(it.data)
                    hideRefreshLoading()
                } else {
                    newsFeedAdapter?.addListItem(it.data)
                }
                if (it.paginator.start <= -1) {
                    newsFeedAdapter?.onReachEnd()
                }
            } else {
                hideRefreshLoading()
                newsFeedAdapter?.onReachEnd()
                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    newsFeedAdapter?.clearAll()
                    newsFeedAdapter?.onReachEnd()
                }
            }

            if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
            hideRefreshLoading()
        })

        mViewModel.newsFeedLiked.observe(this, Observer {
            if (newsFeedAdapter != null) {
                newsFeedAdapter!!.replaceItem(it, currentPositon)
            }
        })

        back_btn.setOnClickListener(this)
    }

    override fun onRefreshData() {
        super.onRefreshData()
        mViewModel.fetchUserPosts(userID)
    }

    override fun loadData() {
        userID?.let{
            if(it.isNotEmpty()){
                mViewModel.fetchUserPosts(userID)
            }
        }
        clubId?.let{
            if(it.isNotEmpty()){
                mViewModel.fetchClubPosts(clubId)
            }
        }
    }

    override fun onClickListener(data: NewsFeed, adapterPosition: Int) {
        currentPositon = adapterPosition
        EasyGolfNavigation.addPostDirection(this, POST_DETAILS_REQUESTCODE, -1, data.id, Gson().toJson(data), PostDetailsActivity::class.java)
    }

    override fun postClickAction(actionId: Int, post: NewsFeed, adapterPosition: Int) {
        currentPositon = adapterPosition
        when (actionId) {
            0 -> {
                if (checkLiked(post)) {
                    mViewModel.unLikePost(post.id)
                } else {
                    mViewModel.likePost(post.id)
                }
            }
            1 -> {
                EasyGolfNavigation.addPostDirection(this, POST_DETAILS_REQUESTCODE, -1, post.id, Gson().toJson(post), PostDetailsActivity::class.java)
            }
            2 -> {
                EasyGolfNavigation.createPostDirection(this, NewsFeedFragment.POST_DETAILS_REQUESTCODE,
                        2, post.id, Gson().toJson(post), CreatePostActivity::class.java)

            }
        }
    }

    override fun onUserProfile(newsFeed: NewsFeed) {
//        startActivity(Intent(this, UserProfileActivity::class.java).putExtra("id", newsFeed.created_by.profile_id))
    }

    override fun onClickClub(newsFeed: NewsFeed) {
        if (newsFeed.club != null && newsFeed.club.id != null) {
            EasyGolfNavigation.easyGolfCourseDetailDirection(this,newsFeed.club.id,newsFeed.club.name,newsFeed.club.image)
        }
    }

    private fun checkLiked(post: NewsFeed): Boolean {
        mViewModel.getUser()?.let { user ->
            post.post_likes?.let { postLike ->
                for (x in postLike.indices) {
                    if (postLike[x].user.id == user.id) {
                        return true
                    }
                }
            }
        }
        return false
    }

    override fun onClick(v: View?) {
        when(v?.id){
           R.id.back_btn->{
               finish()
           }
        }
    }
}