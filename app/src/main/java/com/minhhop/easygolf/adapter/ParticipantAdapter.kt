package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.ProfileUser
import com.minhhop.easygolf.utils.AppUtil

class ParticipantAdapter(context: Context) : BaseRecyclerViewAdapter<ProfileUser>(context) {
    private val mMarginTopAnotherMassage:Int = AppUtil.dp2px(20f, context).toInt()
    override fun setLayout(viewType: Int): Int {
        return R.layout.item_nearby_friend
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if (holder is ParticipantHolder) {
            val model = mDataList[position]
            holder.mTxtName.text = model.getShortName()
            Glide.with(context).load(model.avatar)
                    .circleCrop()
                    .error(R.drawable.ic_icon_user_default)
                    .into(holder.mImgAvatar)

            val param = holder.viewRoot.layoutParams as ViewGroup.MarginLayoutParams
            param.marginEnd = if(position == itemCount - 1){
                 mMarginTopAnotherMassage
            }else{
                0
            }
            holder.viewRoot.requestLayout()
        }
    }

    fun removeItem(idUser:String){
        for (index in 0 until mDataList.size){
            if(mDataList[index]._id == idUser){
                mDataList.removeAt(index)
                notifyItemRemoved(index)
                notifyItemChanged(index - 1)
                break
            }
        }
    }

    override fun addItem(item: ProfileUser?) {
        super.addItem(item)
        notifyItemChanged(mDataList.size - 2)
    }

    fun getListCurrentMembers():String{
        val result = StringBuilder()
        for (item in mDataList){
            if(result.count() > 0) {
                result.append("-")
            }
            result.append(item._id)
        }
        if(result.count() > 0) {
            result.deleteCharAt(result.count() - 1)
        }
        return result.toString()
    }


    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder {
        return ParticipantHolder(viewRoot)
    }

    private inner class ParticipantHolder(itemView: View) : CoreRecyclerViewHolder(itemView) {
        val viewRoot:View = itemView.findViewById(R.id.viewRoot)
        val mImgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val mTxtName: TextView = itemView.findViewById(R.id.txtName)

    }
}