package com.minhhop.easygolf.presentation.notification

import android.content.Context
import android.view.View
import com.minhhop.core.domain.notifications.GolfNotification
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.notification_row_layout.view.*
import kotlinx.android.synthetic.main.notification_row_layout.view.player_name_tv

class NotificationAdapter (context: Context, val notificationEvent: NotificationEvent) : EasyGolfRecyclerViewAdapter<GolfNotification>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.notification_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                if (!mDataList[adapterPosition].seen) {
                    notificationEvent.onClickListener(mDataList[adapterPosition],adapterPosition)
                }
            }
        }
        fun bindData(position: Int){
            val notif = getItem(position)
            notif?.let{ data ->
                data.body?.let{
                    itemView.player_name_tv.text = it
                }
                data.user?.avatar?.let{
                    Picasso.get().load(it).placeholder(R.drawable.placeholder).into(itemView.notification_profile_iv)
                }
                data.date?.let{
                    itemView.player_comment_tv.text = AppUtils.generatePublishTime(it.toLong())
                }
                if(data.seen){
                    itemView.notification_notifier_iv.visibility = View.GONE
                }else{
                    itemView.notification_notifier_iv.visibility = View.VISIBLE
                }
                data.heading?.let{
                    itemView.notificationHeadingTv.visibility = View.VISIBLE
                    itemView.notificationHeadingTv.text = it
                }
                if(data.heading.isNullOrEmpty()){
                   itemView.notificationHeadingTv.visibility = View.GONE
                }
            }

        }
    }
    interface NotificationEvent{
        fun onClickListener(data: GolfNotification, adapterPosition: Int)
        fun onDeleteListener(position: GolfNotification)
    }
}