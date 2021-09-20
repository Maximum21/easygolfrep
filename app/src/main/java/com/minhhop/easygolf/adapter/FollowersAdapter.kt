package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.minhhop.core.domain.feed.Like
import com.minhhop.core.domain.profile.People
import com.minhhop.easygolf.R
import com.minhhop.easygolf.core.Utils
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.liked_by_row.view.*

class FollowersAdapter (context: Context,var tag:Int = 0) : EasyGolfRecyclerViewAdapter<People>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.followers_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {

        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            Picasso.get().load(model.avatar).placeholder(R.drawable.placeholder).into(itemView.liked_by_row_iv)
            when(tag){
                0->{
                    itemView.liked_by_row_tv.visibility = View.VISIBLE
                    itemView.liked_by_row_tv.text = model.fullname
                }
                1->{
                    itemView.liked_by_row_tv.visibility = View.GONE
                }
            }
            setDimentions(itemView.liked_by_row_iv, Utils.setLayoutWidth(130.0), Utils.setLayoutHeight(130.0))
        }
    }

    private fun setDimentions(img: ImageView, layoutWidth: Int?, layoutHeight: Int) {
        if (layoutWidth!=null) {
            img.layoutParams.width = layoutWidth
        }
        img.layoutParams.height = layoutHeight
        img.requestLayout()
    }
}