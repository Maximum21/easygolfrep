package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.minhhop.core.domain.feed.Comment
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.comment_row_layout.view.*


class CommentsAdapter (context: Context, val view : CommentEvent) : EasyGolfRecyclerViewAdapter<Comment>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.comment_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnLongClickListener {
                showPopup(itemView,mDataList[adapterPosition])
                return@setOnLongClickListener true
            }
            itemView.post_profile_iv.setOnClickListener {
                view.onImageClick(mDataList[adapterPosition]?.created_by?.profile_id?:"")
            }
        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            Picasso.get().load(model.created_by.avatar).placeholder(R.drawable.placeholder).into(itemView.post_profile_iv)
            itemView.player_name_tv.text = model.created_by.fullName
            itemView.player_comment_tv.text = model.comment
            itemView.publish_time_tv.text = AppUtils.generatePublishTime(model.date_created.toLong())
//            if (model.post!=null && model.post.post_likes!=null && model.post.post_likes!!.isNotEmpty()) {
//                itemView.publish_time_tv.text = model.post.post_likes!!.size.toString()
//            }
        }
    }

    private fun showPopup(itemView: View, comment: Comment) {

        //creating a popup menu

        //creating a popup menu
        val popup = PopupMenu(context, itemView)
        //inflating menu from xml resource
        //inflating menu from xml resource
        popup.inflate(R.menu.options_menu)
        //adding click listener
        //adding click listener
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.edit -> {
                    view.editComment(comment)
                }
                R.id.delete -> {
                    view.deleteComment(comment)
                }
            }
            false
        }
        //displaying the popup
        //displaying the popup
        popup.show()
    }


    interface CommentEvent{
        fun deleteComment(comment: Comment)
        fun editComment(comment: Comment)
        fun onImageClick(profileId:String)
    }
}