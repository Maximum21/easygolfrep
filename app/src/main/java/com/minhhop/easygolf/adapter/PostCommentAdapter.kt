package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.news_feed_row_layout.view.*

class PostCommentAdapter (context: Context, val newsFeedEvent: NewsFeedEvent) : EasyGolfRecyclerViewAdapter<NewsFeed>(context) {

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
                newsFeedEvent.onClickListener(mDataList[adapterPosition])
            }
            itemView.post_share_group.setOnClickListener {
                newsFeedEvent.postClickAction(2,mDataList[adapterPosition].id)
            }
            itemView.post_like_group.setOnClickListener {
                newsFeedEvent.postClickAction(0, mDataList[adapterPosition].id)
            }
            itemView.post_comment_group.setOnClickListener {
                newsFeedEvent.postClickAction(1, mDataList[adapterPosition].id)
            }
        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
//            Picasso.get().load(model.created_by.avatar).placeholder(R.drawable.placeholder).into(itemView.post_profile_iv)
//            itemView.post_desription_tv.text = model.content
//            itemView.player_name_tv.text = model.created_by.fullName
//            itemView.post_publish_time_tv.text = AppUtils.generatePublishTime(model.date_created.toLong())
        }
    }




    interface NewsFeedEvent{
        fun onClickListener(images: NewsFeed)
        abstract fun postClickAction(actionId: Int, postId: String)
    }
}