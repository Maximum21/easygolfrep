package com.minhhop.easygolf.presentation.club.detail.photo

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.ClubPhoto
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.extension.loadImage
import com.squareup.picasso.Callback
import kotlinx.android.synthetic.main.item_club_photo.view.*

class ClubPhotoAdapter(context:Context,private val user:User?,
                       private val mSpanCount:Int = -1) : EasyGolfRecyclerViewAdapter<ClubPhoto?>(context) {
    companion object{
        private const val TYPE_ITEM_UPLOAD = 0x0003
    }
    private var mClubPhotoAdapterListener:ClubPhotoAdapterListener? = null
    private val mNormalMargin = context.resources.getDimension(R.dimen.normal_margin).toInt()
    private val mSmallMargin = context.resources.getDimension(R.dimen.small_margin).toInt()

    override fun setLayout(viewType: Int): Int = if(viewType == TYPE_ITEM_UPLOAD) R.layout.item_club_upload_photo else R.layout.item_club_photo

    override fun customViewType(position: Int): Int {
        return if(getItem(position) == null){
            TYPE_ITEM_UPLOAD
        }else{
            super.customViewType(position)
        }
    }
    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = ClubPhotoViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ClubPhotoViewHolder)?.bindData(position)
    }

    fun addListener(clubPhotoAdapterListener:ClubPhotoAdapterListener){
        this.mClubPhotoAdapterListener = clubPhotoAdapterListener
    }

    fun addListItemAt(item: List<ClubPhoto>, position: Int){
        mDataList.addAll(position,item)
        this.notifyItemRangeInserted(position,item.size)
    }

    inner class ClubPhotoViewHolder(itemView:View) : EasyGolfRecyclerViewHolder(itemView){
        init {
            if(mSpanCount > 0) {
                itemView.viewRoot.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            itemView.requestLayout()
        }

        fun bindData(position: Int){
            if(mSpanCount < 0){
                (itemView.viewRoot.layoutParams as? ViewGroup.MarginLayoutParams)?.let { marginLayoutParams ->
                    marginLayoutParams.topMargin = mSmallMargin
                    marginLayoutParams.marginStart = mNormalMargin
                    if(adapterPosition == itemCount - 1){
                        marginLayoutParams.marginEnd = mNormalMargin
                    }else {
                        marginLayoutParams.marginEnd = 0
                    }
                }
            }
            val model = getItem(position)

            if(model == null){
                itemView.viewRoot.setOnClickListener {
                    mClubPhotoAdapterListener?.uploadPhoto()
                }
            }else{
                itemView.viewRoot.setOnClickListener {
                    mClubPhotoAdapterListener?.onClick(model,adapterPosition)
                }
                itemView.btDeletePhoto?.setOnClickListener {
                    mClubPhotoAdapterListener?.onDelete(model,adapterPosition)
                }

                itemView.btDeletePhoto?.visibility = View.INVISIBLE
                itemView.imgClubPhoto?.loadImage(model.photo,object : Callback{
                    override fun onSuccess() {
                        itemView.btDeletePhoto.visibility = if(user?.id == model.user.id) View.VISIBLE else View.INVISIBLE
                    }

                    override fun onError(e: Exception?) {
                        itemView.btDeletePhoto.visibility = if(user?.id == model.user.id) View.VISIBLE else View.INVISIBLE
                    }
                })
            }
        }
    }

    interface ClubPhotoAdapterListener{
        fun onDelete(photo: ClubPhoto,position: Int)
        fun onClick(photo: ClubPhoto,position: Int)
        fun uploadPhoto()
    }
}