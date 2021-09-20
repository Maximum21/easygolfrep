package com.minhhop.easygolf.presentation.feed

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.minhhop.core.domain.feed.CustomImageObject
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.create_post_image_row.view.*
import java.io.File

class CreatePostImagesAdapter (context: Context, val newsFeedEvent: NewsFeedEvent) : EasyGolfRecyclerViewAdapter<CustomImageObject>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.create_post_image_row
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.create_post_image_row_close_iv.setOnClickListener {
                if (mDataList[adapterPosition].uri!=null && mDataList[adapterPosition].uri.isNotEmpty()) {
                    newsFeedEvent.onClickListener(adapterPosition)
                    mDataList.removeAt(adapterPosition)
                    notifyDataSetChanged()
                }else{
                    newsFeedEvent.deleteImage(mDataList[adapterPosition],adapterPosition)
                }

            }
        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            setDimentions(itemView.viewRoot,260,200)
            if(model.uri!=null && model.uri.isNotEmpty()){
                Picasso.get().load(File(model.uri)).fit().into(itemView.create_post_image_row_iv)
            }else if(model.url!=null && model.url!!.url!=null){
                Picasso.get().load(model.url!!.url).fit().into(itemView.create_post_image_row_iv)
            }
        }
    }

    private fun setDimentions(img: ConstraintLayout, layoutWidth: Int?, layoutHeight: Int) {
        if (layoutWidth!=null) {
            img.layoutParams.width = layoutWidth
        }
        img.layoutParams.height = layoutHeight
        img.requestLayout()
    }


    interface NewsFeedEvent{
        fun onClickListener(positiom: Int)
        fun deleteImage(customImageObject: CustomImageObject, adapterPosition: Int)
    }
}