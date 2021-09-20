package com.minhhop.easygolf.presentation.like

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.minhhop.core.domain.feed.Like
import com.minhhop.core.domain.profile.People
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.adapter.LikedByAdapter
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.presentation.home.chat.ChatHomeViewModel
import com.minhhop.easygolf.presentation.more.MoreViewModel
import kotlinx.android.synthetic.main.likes_fragment_layout.view.*
import org.koin.android.ext.android.inject
import java.util.ArrayList

class LikesFragment : EasyGolfFragment<LikesViewModel>(), LikedByAdapter.LikedByEvent{

    override val mViewModel: LikesViewModel by inject()

    private var likesList: List<Like> = ArrayList()
    private var followersList: List<People> = ArrayList()
    private var likesAdapter: LikedByAdapter? = null
    private var followersAdapter: FollowersAdapter? = null
    override fun setLayout(): Int = R.layout.likes_fragment_layout

    override fun initView(viewRoot: View) {
        context?.let {
            val tag = arguments?.getInt("tag",0)
            setData(tag?:0)
            viewRoot.back_btn.setOnClickListener {
                fragmentManager!!.popBackStack()
            }
        }
    }

    private fun setData(tag: Int) {
        when(tag){
            0->{
                val myString:String = arguments!!.getString("data","")
                Log.e("moreImages","===${myString}")
                likesList = Gson().fromJson<List<Like>>(myString, object : TypeToken<List<Like>>(){}.type)
                Log.e("moreImages","===${likesList}")
                if(likesList!=null && likesList.isNotEmpty()){
                    viewRoot.likes_fragment_heading_tv.text = "Likes (${likesList.size})"
                    viewRoot.likes_fragment_rv.layoutManager = LinearLayoutManager(context)
                    likesAdapter = LikedByAdapter(context!!,0,this)
                    viewRoot.likes_fragment_rv.adapter = likesAdapter
                    likesAdapter!!.setDataList(likesList)
                }else{
                    viewRoot.likes_fragment_heading_tv.text = "Likes (0)"
                }
            }
            else->{
                val myString:String = arguments!!.getString("data","")
                followersList = Gson().fromJson<List<People>>(myString, object : TypeToken<List<People>>(){}.type)
                if(followersList!=null && followersList.isNotEmpty()){
                    viewRoot.likes_fragment_heading_tv.text = "Followers (${followersList.size})"
                    viewRoot.likes_fragment_rv.layoutManager = LinearLayoutManager(context)
                    followersAdapter = FollowersAdapter(context!!,0)
                    viewRoot.likes_fragment_rv.adapter = followersAdapter
                    followersAdapter!!.setDataList(followersList)
                }else{
                    viewRoot.likes_fragment_heading_tv.text = "Followers (0)"
                }
            }
        }
    }

    override fun onClickListener(profileId: String) {
        activity?.let {
            EasyGolfNavigation.startUserDetailsActivity(activity!!,profileId)
        }
    }

    override fun loadData() {

    }
}