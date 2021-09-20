package com.minhhop.easygolf.presentation.club.detail.review

import android.content.Context
import android.view.View
import com.minhhop.core.domain.golf.Review
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.extension.loadImage
import kotlinx.android.synthetic.main.view_item_review.view.*

class ClubReviewAdapter (context: Context) : EasyGolfRecyclerViewAdapter<Review>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.view_item_review

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder  = ClubReviewViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ClubReviewViewHolder)?.bindData(position)
    }

    override fun addItem(item: Review) {
        var indexFound:Int? = null
        mDataList.forEachIndexed { index, review ->
            if(item.user.id == review.user.id){
                indexFound = index
            }
        }
        indexFound?.let { index ->
            removeItemAt(index)
        }
        addItemAt(item,0)
    }

    inner class ClubReviewViewHolder(itemView:View) : EasyGolfRecyclerViewHolder(itemView){
        fun bindData(position:Int){
            val model = getItem(position)
            itemView.imgAvatar.loadImage(model.user.avatar,R.drawable.ic_icon_user_default)
            itemView.textName.text = model.user.fullName
            itemView.textDate.text = AppUtils.convertISOToDate(model.last_updated,context)
            itemView.textContent.text = model.content
            itemView.textRatingView.text = if((model.rate - model.rate.toInt()) == 0.0) model.rate.toInt().toString() else model.rate.toString()
        }
    }
}