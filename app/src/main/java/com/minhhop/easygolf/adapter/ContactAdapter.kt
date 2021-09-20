package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.minhhop.core.domain.Contact
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder

class ContactAdapter(context: Context,private val mEvent: EventContact) : BaseRecyclerViewAdapter<Contact>(context){
    override fun setLayout(viewType: Int): Int = R.layout.item_contact

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is ContactHolder){
            holder.initData(mDataList[position])
        }
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): CoreRecyclerViewHolder = ContactHolder(viewRoot)

    inner class ContactHolder(itemView:View) : CoreRecyclerViewHolder(itemView){
        private val mViewRoot:View = itemView.findViewById(R.id.viewRoot)
        private val mAvatar:ImageView = itemView.findViewById(R.id.avatar)
        private val mTxtName:TextView = itemView.findViewById(R.id.txtName)
        private val mTxtPhone:TextView = itemView.findViewById(R.id.txtPhone)
        private val mIconMark:View = itemView.findViewById(R.id.iconMark)

        init {
            mViewRoot.setOnClickListener {
                val model = mDataList[adapterPosition]
                model.selected = !model.selected
                mEvent.onClick(model)
                notifyItemChanged(adapterPosition)
            }
        }

        fun initData(contact: Contact){
            if (contact.avatar.isNullOrEmpty()) {
                Glide.with(context).load(contact.avatar)
                        .error(R.drawable.ic_icon_user_default)
                        .circleCrop().into(mAvatar)
            }else{
                Glide.with(context).load(R.drawable.ic_icon_user_default)
                        .circleCrop().into(mAvatar)
            }

            mTxtName.text = contact.getFullName()
            mTxtPhone.text = contact.phone_number

            if(contact.selected){
                mIconMark.visibility = View.VISIBLE
            }else{
                mIconMark.visibility = View.GONE
            }
        }
    }

    interface EventContact{
        fun onClick(contact: Contact)
    }
}