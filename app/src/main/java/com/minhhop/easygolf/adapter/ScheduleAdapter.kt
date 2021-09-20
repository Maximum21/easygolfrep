package com.minhhop.easygolf.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.Tournament
import com.minhhop.easygolf.services.image.RoundedTransformationBuilder
import com.minhhop.easygolf.utils.AppUtil
import com.minhhop.easygolf.views.fragments.schedule.listeners.OnScheduleListener
import com.minhhop.easygolf.widgets.FriendsCalendarView
import com.squareup.picasso.Picasso

class ScheduleAdapter(context:Context,private val onClickItem:OnScheduleListener) : BaseRecyclerViewAdapter<Tournament>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.item_schedule

    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder = ScheduleViewHolder(viewRoot)


    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(holder is ScheduleViewHolder){
            val model = mDataList[position]

            val transformation = RoundedTransformationBuilder()
                    .cornerRadiusDp(15f)
                    .oval(false)
                    .build()
            if(model.photo.isNullOrEmpty()){
                Picasso.get().load(R.drawable.img_holder_golf_radius)
                        .fit().centerCrop()
                        .placeholder(R.drawable.img_holder_golf_radius)
                        .transform(transformation)
                        .into(holder.mImgSchedule)
            }else{
                Picasso.get().load(model.photo)
                        .fit().centerCrop()
                        .placeholder(R.drawable.img_holder_golf_radius)
                        .transform(transformation)
                        .into(holder.mImgSchedule)

            }

            holder.mTxtName.text = model.name
            holder.mTxtDate.text = AppUtil.fromISO8601UTC(model.scheduleDate,"dd/MM/yyyy")

            holder.mViewFriends.setData(model.participants)

        }
    }

    private inner class ScheduleViewHolder(itemView:View?) : CoreRecyclerViewHolder(itemView){
        var mImgSchedule:ImageView = itemView!!.findViewById(R.id.imgSchedule)
        val mTxtName:TextView = itemView!!.findViewById(R.id.txtName)
        val mViewFriends:FriendsCalendarView = itemView!!.findViewById(R.id.viewFriends)
        val mTxtDate:TextView = itemView!!.findViewById(R.id.txtDate)
        val mActionDirection:View = itemView!!.findViewById(R.id.actionDirection)
        var mAdapter = this@ScheduleAdapter
        init {
            itemView?.setOnClickListener {
                mAdapter.onClickItem.onClick(mImgSchedule,mTxtName,mDataList[adapterPosition])
            }
            mActionDirection.setOnClickListener {
                val club = mDataList[adapterPosition].club
                val gmmIntentUri = Uri.parse("google.navigation:q=" + club.latitude
                        + "," + club.longitude + "&avoid=tf" + "(" + club.name + ")")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }
        }
    }
}