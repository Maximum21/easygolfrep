package com.minhhop.easygolf.adapter

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.utils.AppUtil

class PlayerScorecardAdapter(activity: Activity,context:Context) : BaseRecyclerViewAdapter<ProfileUser>(context) {
    private var mWithItem:Int = 0

    init {
        mWithItem = (AppUtil.getWidthScreen(activity) - AppUtil.convertDpToPixel(20,context))/4
    }

    override fun setLayout(viewType: Int): Int = R.layout.item_player_scorecard

    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder = PlayerScorecardHolder(viewRoot)

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is PlayerScorecardHolder){
            val data = mDataList[position]
            holder.onBindData(data)
        }
    }

    private inner class PlayerScorecardHolder(itemView:View?): CoreRecyclerViewHolder(itemView){
//        private var mViewRoot:View = itemView!!.findViewById(R.id.viewRoot)
        private var mImgAvatar:ImageView = itemView!!.findViewById(R.id.imgAvatar)
        private var mTxtNamePlayer:TextView = itemView!!.findViewById(R.id.txtNamePlayer)

        fun onBindData(data: ProfileUser){
            if(!TextUtils.isEmpty(data.avatar)) {
                Glide.with(context)
                        .load(data.avatar)
                        .error(R.drawable.ic_icon_user_default)
                        .circleCrop()
                        .into(mImgAvatar)
            }else{
                Glide.with(context)
                        .load(R.drawable.ic_icon_user_default)
                        .circleCrop()
                        .into(mImgAvatar)
            }

            mTxtNamePlayer.text = data.getShortName()
//
//            val param = mViewRoot.layoutParams as ViewGroup.MarginLayoutParams
//            param.width = mWithItem
//            mViewRoot.requestLayout()
        }
    }
}