package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.listeners.EventSelectPeople
import com.squareup.picasso.Picasso

class AddPeopleAdapter(context: Context, private val mSizeBlackList: Int,
                       private val mEventSelectPeople: EventSelectPeople,
                       private val mIsLimit:Boolean = false) : BaseRecyclerViewAdapter<UserEntity>(context) {
    private var mSizeItemCheck = 0

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_add_chat
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is AddPeopleHolder) {
            val data = mDataList[position]
            Picasso.get().load(data.avatar)
                    .placeholder(R.drawable.ic_icon_user_default)
                    .error(R.drawable.ic_icon_user_default)
                    .into(holder.mImgAvatar)

            holder.mTxtName.text = data.fullName
            holder.mTxtPhone.text = data.phoneNumber

//            if (!data.isCheck) {
//                holder.mViewCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_uncheck_user))
//            } else {
//                holder.mViewCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_check_user))
//            }
        }
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder {
        return AddPeopleHolder(viewRoot)
    }

    private inner class AddPeopleHolder(itemView: View) : CoreRecyclerViewHolder(itemView) {
        val mViewCheck: ImageView = itemView.findViewById(R.id.viewCheck)
        val mImgAvatar: ImageView
        val mTxtName: TextView
        val mTxtPhone: TextView

        init {
            itemView.setOnClickListener {


//                val isCheckData = mDataList[adapterPosition].isCheck
//                if (isCheckData) {
//                    mEventSelectPeople.onUnSelect(mDataList[adapterPosition])
//                    mViewCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_uncheck_user))
//                    mSizeItemCheck--
//                    mDataList[adapterPosition].isCheck = false
//                } else {
//                    if(!mIsLimit){
//                        mEventSelectPeople.onSelect(mDataList[adapterPosition])
//                        mViewCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_check_user))
//                        mSizeItemCheck++
////                        mDataList[adapterPosition].isCheck = true
//                    }else {
//                        if (mSizeItemCheck + mSizeBlackList < 3) {
//                            mEventSelectPeople.onSelect(mDataList[adapterPosition])
//                            mViewCheck.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_icon_check_user))
//                            mSizeItemCheck++
////                            mDataList[adapterPosition].isCheck = true
//                        } else {
//                            Toast.makeText(context,
//                                    "Maximum player in the battle are 4 player",
//                                    Toast.LENGTH_LONG).show()
//                        }
//                    }
//                }
            }

            mImgAvatar = itemView.findViewById(R.id.imgAvatar)
            mTxtName = itemView.findViewById(R.id.txtName)
            mTxtPhone = itemView.findViewById(R.id.txtPhone)
        }
    }
}
