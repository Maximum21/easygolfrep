package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.entity.UserEntity

class ContactSearchAdapter(context:Context,val contactSearchClick: ContactSerchClick) : BaseRecyclerViewAdapter<UserEntity>(context){

    override fun setLayout(viewType: Int): Int = R.layout.item_contact_search
    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder = ContactSearchViewHolder(viewRoot)

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is ContactSearchViewHolder){
            holder.bindData(mDataList[position])
        }
    }

    inner class ContactSearchViewHolder(itemView:View) : CoreRecyclerViewHolder(itemView){
        private val mImageAvatar:ImageView = itemView.findViewById(R.id.imgAvatar)
        private val mTxtName:TextView = itemView.findViewById(R.id.txtName)
        private val mTxtPhone:TextView = itemView.findViewById(R.id.txtPhone)

        init {
            itemView.setOnClickListener {
                contactSearchClick.onClickMe(mDataList[adapterPosition])
            }
        }

        fun bindData(data: UserEntity){
            if(!data.avatar.isNullOrEmpty()) {
                Glide.with(context)
                        .load(data.avatar)
                        .placeholder(R.drawable.ic_icon_user_default)
                        .error(R.drawable.ic_icon_user_default)
                        .into(mImageAvatar)
            }else{
                Glide.with(context)
                        .load(R.drawable.ic_icon_user_default)
                        .into(mImageAvatar)
            }

            mTxtName.text = data.fullName
            mTxtPhone.text = data.phoneNumber
        }
    }

    interface ContactSerchClick{
        fun onClickMe(userEntity: UserEntity)
    }
}