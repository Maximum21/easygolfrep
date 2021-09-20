package com.minhhop.easygolf.presentation.home.newsfeed

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.minhhop.core.domain.User
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.NewsFeedAdapter
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.feed.CreatePostActivity
import com.minhhop.easygolf.presentation.user.UserProfileActivity
import com.minhhop.easygolf.services.DatabaseService
import com.minhhop.easygolf.views.activities.PostDetailsActivity
import com.minhhop.easygolf.views.activities.SearchActivity
import kotlinx.android.synthetic.main.fragment_news_feed.view.*
import org.koin.android.ext.android.inject

class NewsFeedFragment : EasyGolfFragment<NewsFeedViewModel>(), NewsFeedAdapter.NewsFeedEvent {


    private var currentPositon: Int = -1
    override val mViewModel: NewsFeedViewModel by inject()
    private var localPostList: List<NewsFeed> = ArrayList()
    private var newsFeedAdapter: NewsFeedAdapter? = null
    private var tags = ""
    override fun setLayout(): Int = R.layout.fragment_news_feed

    companion object {
        const val POST_DETAILS_REQUESTCODE: Int = 1111
        const val POST_DETAILS_REQUESTCODE_EDIT: Int = 645
    }

    override fun initView(viewRoot: View) {
        context?.let { context ->
            val layoutManager = LinearLayoutManager(context)
            viewRoot.post_list_recyclerview.layoutManager = layoutManager
            newsFeedAdapter = NewsFeedAdapter(context, this, mViewModel.getUser()!!)
            viewRoot.post_list_recyclerview.adapter = newsFeedAdapter
            newsFeedAdapter!!.registerLoadMore(layoutManager, viewRoot.post_list_recyclerview) {
                if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                    mViewModel.fetchPosts(tags)
                }
            }
        }

        mViewModel.newsFeedPagerLive.observe(viewLifecycleOwner, Observer {
            hideMask()
            if (it.data.isNotEmpty()) {
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

        mViewModel.newsFeedLiked.observe(viewLifecycleOwner, Observer {
            if (newsFeedAdapter != null) {
                newsFeedAdapter!!.replaceItem(it, currentPositon)
            }
        })

        viewRoot.all_feed_tag.setOnClickListener {
            newsFeedAdapter!!.clearAll()
            newsFeedAdapter?.openReachEnd()
            mStatusLoading = StatusLoading.REFRESH_LOAD
            setPosts("", true)
        }
        viewRoot.all_feed_following.setOnClickListener {
            newsFeedAdapter!!.clearAll()
            newsFeedAdapter?.openReachEnd()
            mStatusLoading = StatusLoading.REFRESH_LOAD
            setPosts("following", true)
        }
        viewRoot.findViewById<View>(R.id.circled_bg_iv).setOnClickListener {
            val intent = Intent(context, CreatePostActivity::class.java)
            context?.startActivity(intent)
        }
        viewRoot.findViewById<View>(R.id.whats_group).setOnClickListener {
            val intent = Intent(context, CreatePostActivity::class.java)
            context?.startActivity(intent)
        }
    }

    private fun setPosts(s: String, onRefresh: Boolean = false) {
        context?.let { context ->
            newsFeedAdapter?.let { newsFeedAdapter ->
                this.tags = s
                mViewModel.fetchPosts(tags, onRefresh)
                when (s) {
                    "following" -> {
                        viewRoot.all_feed_following.setTextColor(ContextCompat.getColor(context, R.color.dynamic_native_color))
                        viewRoot.all_feed_tag.setTextColor(ContextCompat.getColor(context, R.color.textColorGray))
                    }
                    else -> {
                        viewRoot.all_feed_tag.setTextColor(ContextCompat.getColor(context, R.color.dynamic_native_color))
                        viewRoot.all_feed_following.setTextColor(ContextCompat.getColor(context, R.color.textColorGray))
                    }
                }
            }
        }
    }

    override fun onRefreshData() {
        super.onRefreshData()
        mViewModel.fetchPosts(tags, true);
    }

    override fun loadData() {
        mViewModel.fetchPosts(tags)
    }

    override fun onClickListener(data: NewsFeed, adapterPosition: Int) {
        currentPositon = adapterPosition
        EasyGolfNavigation.addPostDirection(this, POST_DETAILS_REQUESTCODE, -1, data.id, Gson().toJson(data), PostDetailsActivity::class.java)
    }

    override fun postClickAction(actionId: Int, post: NewsFeed, adapterPosition: Int) {
        Log.e("testingid","${post.id}")
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
                        2, post.id,Gson().toJson(post),CreatePostActivity::class.java)

            }
        }
    }

    override fun onUserProfile(newsFeed: NewsFeed) {
        startActivity(Intent(activity, UserProfileActivity::class.java).putExtra("id", newsFeed.created_by.profile_id))
    }

    override fun onClickClub(newsFeed: NewsFeed) {
        if (newsFeed.club != null && newsFeed.club.id != null) {
            activity?.let { EasyGolfNavigation.easyGolfCourseDetailDirection(it,newsFeed.club.id,newsFeed.club.name,newsFeed.club.image) }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        Log.e("TESTIMGKSLSLS", "===1=======$requestCode=====$resultCode====$currentPositon")
        if (requestCode == POST_DETAILS_REQUESTCODE && resultCode == Activity.RESULT_OK && intent != null) {
            val postblundle = EasyGolfNavigation.PostBundle(intent)
            when (postblundle?.tag) {
                0 -> {
                    if (newsFeedAdapter != null && newsFeedAdapter!!.itemCount > currentPositon && currentPositon != -1) {
                        newsFeedAdapter!!.removeItemAt(currentPositon)
                    }
                }
                1 -> {
                    if (newsFeedAdapter != null && newsFeedAdapter!!.itemCount > currentPositon && currentPositon != -1) {
                        newsFeedAdapter!!.replaceItem(postblundle.getResult()!!, currentPositon)
                    }
                }
            }
        }
    }


}