package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.User
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_feed_row_layout.view.*
import kotlinx.android.synthetic.main.news_feed_row_layout.view.comment_post_tv
import kotlinx.android.synthetic.main.news_feed_row_layout.view.like_post_iv
import kotlinx.android.synthetic.main.news_feed_row_layout.view.like_post_tv
import kotlinx.android.synthetic.main.share_post_row_layout.view.*


class NewsFeedAdapter(context: Context, val newsFeedEvent: NewsFeedEvent,val currentUserEntity: User) : EasyGolfRecyclerViewAdapter<NewsFeed>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.news_feed_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                newsFeedEvent.onClickListener(mDataList[adapterPosition],adapterPosition)
            }
            itemView.post_share_group.setOnClickListener {
                newsFeedEvent.postClickAction(2,mDataList[adapterPosition],adapterPosition)
            }
            itemView.post_profile_iv.setOnClickListener {
                newsFeedEvent.onUserProfile(mDataList[adapterPosition])
            }
            itemView.post_like_group.setOnClickListener {
                newsFeedEvent.postClickAction(0, mDataList[adapterPosition],adapterPosition)
            }
            itemView.post_comment_group.setOnClickListener {
                newsFeedEvent.postClickAction(1, mDataList[adapterPosition],adapterPosition)
            }
            itemView.player_type_tv.setOnClickListener {
                newsFeedEvent.onClickClub(mDataList[adapterPosition])
            }
        }
        fun bindData(position: Int){
            val model = getItem(position)
            itemView.post_profile_iv_layout.setData(model,currentUserEntity.id,1)
            var test = false
            currentUserEntity?.id?.let{
                if (model.post_likes!=null && model.post_likes!!.isNotEmpty()) {
                    for(element in model.post_likes!!){
                        if(element!=null && element.user!=null && element.user.id == it){
                            test = true
                            break
                        }
                    }
                }
            }
            if(!test){
                itemView.like_post_iv.setImageResource(R.drawable.ic_icon_like_black)
                itemView.like_post_tv.setTextColor(ContextCompat.getColor(context,R.color.textColorGray))
            }else{
                itemView.like_post_iv.setImageResource(R.drawable.ic_like_filled_black)
                itemView.like_post_tv.setTextColor(ContextCompat.getColor(context,R.color.textColorGray))
            }
            if (model.comments > 0) {
                itemView.comment_post_count_tv.text = "(${model.comments})"
            }else{
                itemView.comment_post_count_tv.text = ""
            }
            if (model.shares > 0) {
                itemView.share_post_count_tv.text = "(${model.shares})"
            }else{
                itemView.share_post_count_tv.text = ""
            }
            if (model.likes > 0) {
                itemView.like_post_count_tv.text = "(${model.likes})"
            }else{
                itemView.like_post_count_tv.text = ""
            }
            if(model?.shared_post==null){
                itemView.post_profile_iv_layout1.visibility = View.GONE
            }else{
                model?.shared_post?.let{
                    itemView.post_profile_iv_layout1.visibility = View.VISIBLE
                    itemView.post_profile_iv_layout1.setData(it,currentUserEntity.id,1)
                }
            }
        }
    }
    interface NewsFeedEvent{
        fun onClickListener(data: NewsFeed, adapterPosition: Int)
        fun postClickAction(actionId: Int, postId: NewsFeed, adapterPosition: Int)
        fun onUserProfile(newsFeed: NewsFeed)
        fun onClickClub(newsFeed: NewsFeed)
    }
}