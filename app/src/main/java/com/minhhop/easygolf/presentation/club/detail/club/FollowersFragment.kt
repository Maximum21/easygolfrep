package com.minhhop.easygolf.presentation.club.detail.club

import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.profile.People
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.adapter.LikedByAdapter
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import kotlinx.android.synthetic.main.likes_fragment_layout.view.*
import org.koin.android.ext.android.inject
import java.util.ArrayList

class FollowersFragment : EasyGolfFragment<FollowersViewModel>(), LikedByAdapter.LikedByEvent{

    private var clubId : String = "0"
    override val mViewModel: FollowersViewModel by inject()
    private var followersList: List<People> = ArrayList()
    private var followersAdapter: FollowersAdapter? = null
    override fun setLayout(): Int = R.layout.likes_fragment_layout

    override fun initView(viewRoot: View) {
        context?.let {
            clubId = arguments?.getString("clubId","0")?:"0"
            val layoutManager = LinearLayoutManager(context)
            viewRoot.likes_fragment_rv.layoutManager = layoutManager
            followersAdapter = FollowersAdapter(context!!,0)
            viewRoot.likes_fragment_rv.adapter = followersAdapter
            followersAdapter?.setDataList(followersList)
            followersAdapter?.registerLoadMore(layoutManager, viewRoot.likes_fragment_rv) {
                if (mStatusLoading === StatusLoading.FINISH_LOAD) {
                    mViewModel.getFollowers(clubId,followersAdapter?.itemCount?:0)
                }
            }
            viewRoot.likes_fragment_heading_tv.text = resources.getString(R.string.followers)
            mViewModel.followersLiveData.observe(this, Observer {
                hideMask()
                if (it?.data?.isNotEmpty()?:false) {
                    if (mStatusLoading == StatusLoading.REFRESH_LOAD
                            || mStatusLoading == StatusLoading.FIRST_LOAD) {
                        followersAdapter?.setDataList(it.data)
                        hideRefreshLoading()
                    } else {
                        followersAdapter?.addListItem(it.data)
                    }
                    if (it.paginator.start <= -1) {
                        followersAdapter?.onReachEnd()
                    }
                } else {
                    hideRefreshLoading()
                    followersAdapter?.onReachEnd()
                    if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                        followersAdapter?.clearAll()
                        followersAdapter?.onReachEnd()
                    }
                }

                if (mStatusLoading == StatusLoading.FIRST_LOAD || mStatusLoading == StatusLoading.REFRESH_LOAD) {
                    mStatusLoading = StatusLoading.FINISH_LOAD
                }
                hideRefreshLoading()
            })
            viewRoot.back_btn.setOnClickListener {
                fragmentManager!!.popBackStack()
            }
        }
    }

    private fun setData(tag: String) {
           
    }

    override fun onClickListener(profileId: String) {
        activity?.let {
            EasyGolfNavigation.startUserDetailsActivity(activity!!,profileId)
        }
    }

    override fun loadData() {
        clubId?.let{
            mViewModel.getFollowers(clubId)
        }
    }
}