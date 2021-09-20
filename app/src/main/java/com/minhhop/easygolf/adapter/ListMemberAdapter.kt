package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.dialogs.ConfirmDialog
import com.minhhop.easygolf.framework.models.FriendTournament

class ListMemberAdapter(context:Context,private var isRemove:Boolean = false,
                        private val listener:MemberClickListener? = null) : BaseRecyclerViewAdapter<FriendTournament>(context){

    override fun setLayout(viewType: Int): Int = R.layout.item_list_member

    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder = ListMemberHolder(viewRoot)

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is ListMemberHolder){
            holder.bindData(mDataList[position])
        }
    }

    inner class ListMemberHolder(itemView:View) : CoreRecyclerViewHolder(itemView){
        private val mImgAvatar:ImageView = itemView.findViewById(R.id.imgAvatar)
        private val mTxtName:TextView = itemView.findViewById(R.id.txtName)
        private val mTxtStage:TextView = itemView.findViewById(R.id.txtStage)
        private val mActionRemove:View = itemView.findViewById(R.id.actionRemove)

        fun bindData(model:FriendTournament){
            Glide.with(context)
                    .load(model.avatar)
                    .error(R.drawable.ic_icon_user_default)
                    .circleCrop()
                    .into(mImgAvatar)

            mTxtName.text = model.fullName
            mTxtStage.text = model.stage

            if(!isRemove){
                mActionRemove.visibility = View.GONE
            }else{
                mActionRemove.visibility = View.VISIBLE
                mActionRemove.setOnClickListener {
                    ConfirmDialog(context).setOnConfirm {
                        listener?.onRemoveItem(mDataList[adapterPosition].id,adapterPosition)
                    }.show()
                }
            }
        }
    }

    fun hideOrshowMark(){
        isRemove = !isRemove
        notifyDataSetChanged()
    }

    interface MemberClickListener{
        fun onRemoveItem(id:Int,index:Int)
    }
}