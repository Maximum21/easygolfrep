package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import com.minhhop.core.domain.feed.PostImage
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.single_image_row_layout.view.*


class MultipleImagesAdapter(context: Context) : EasyGolfRecyclerViewAdapter<PostImage>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.single_image_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            Observable.just(AppUtils.isImage(model.url))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result -> if (result){
                            Picasso.get().load(model.url).into(itemView.single_image_row_iv)
                        }else{
                            itemView.single_image_row_vv.setVideoPath(model.url)
                        }
                    }
        }
    }
}