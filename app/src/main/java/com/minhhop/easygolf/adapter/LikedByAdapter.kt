package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import com.minhhop.core.domain.feed.Like
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.liked_by_row.view.*

class LikedByAdapter (context: Context, val tag:Int, val likedByEvent: LikedByEvent) : EasyGolfRecyclerViewAdapter<Like>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.liked_by_row
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            if (tag==0) {
                itemView.liked_by_row_iv.setOnClickListener {
                    likedByEvent.onClickListener(mDataList[adapterPosition]?.user?.profile_id?:"")
                }
            }
        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            Picasso.get().load(model.user.avatar).placeholder(R.drawable.placeholder).into(itemView.liked_by_row_iv)
            when(tag){
                0->{
                    itemView.liked_by_row_tv.visibility = View.VISIBLE
                    itemView.liked_by_row_tv.text = model.user.fullName
                    setDimentions(itemView.liked_by_row_iv, Utils.setLayoutWidth(150.0), Utils.setLayoutHeight(150.0))
                }
                1->{
                    itemView.liked_by_row_tv.visibility = View.GONE
                    setDimentions(itemView.liked_by_row_iv, Utils.setLayoutWidth(60.0), Utils.setLayoutHeight(60.0))
                }
            }
        }
    }

    private fun setDimentions(img: ImageView, layoutWidth: Int?, layoutHeight: Int) {
        if (layoutWidth!=null) {
            img.layoutParams.width = layoutWidth
        }
        img.layoutParams.height = layoutHeight
        img.requestLayout()
    }

    interface LikedByEvent{
        fun onClickListener(profileId: String)
    }
}