package com.minhhop.easygolf.presentation.gallery

import android.content.Context
import android.net.Uri
import android.view.View
import com.minhhop.core.domain.GalleryMedia
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.extension.loadImage
import kotlinx.android.synthetic.main.item_gallery_view.view.*

class EasyGolfGalleryAdapter(context: Context, private val maxPhotoSelect: Int = 1) : EasyGolfRecyclerViewAdapter<GalleryMedia?>(context) {
    private var mEasyGolfGalleryListener: EasyGolfGalleryListener? = null
    private val mListGalleryMediaIdSelected = HashMap<String, GalleryMedia>()
    override fun setLayout(viewType: Int): Int = R.layout.item_gallery_view

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfGalleryViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfGalleryViewHolder)?.bindData(position)
    }

    fun autoSelectPicFromCamera() {
        if (itemCount > 1) {
            getItem(1)?.let { model ->
                if (mListGalleryMediaIdSelected.size < maxPhotoSelect) {
                    mListGalleryMediaIdSelected[model.id] = model
                    notifyItemChanged(1)
                    mEasyGolfGalleryListener?.onSelect(mListGalleryMediaIdSelected.size)
                }
            }
        }
    }

    fun addListener(easyGolfGalleryListener: EasyGolfGalleryListener) {
        this.mEasyGolfGalleryListener = easyGolfGalleryListener
    }

    fun getResult() = mListGalleryMediaIdSelected.map { it.value }

    inner class EasyGolfGalleryViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {
        init {
            itemView.photoGalleryContainer.setOnClickListener {
                getItem(adapterPosition)?.let { model ->
                    if (maxPhotoSelect > 1) {
                        if (mListGalleryMediaIdSelected.containsKey(model.id)) {
                            mListGalleryMediaIdSelected.remove(model.id)
                            itemView.iconChecker.visibility = View.GONE
                            mEasyGolfGalleryListener?.onSelect(mListGalleryMediaIdSelected.size)
                        } else {
                            if (mListGalleryMediaIdSelected.size < maxPhotoSelect) {
                                mListGalleryMediaIdSelected[model.id] = model
                                itemView.iconChecker.visibility = View.VISIBLE
                                mEasyGolfGalleryListener?.onSelect(mListGalleryMediaIdSelected.size)
                            } else {
                                mEasyGolfGalleryListener?.onLimitSelect(maxPhotoSelect)
                            }
                        }
                    } else {
                        mListGalleryMediaIdSelected[model.id] = model
                        mEasyGolfGalleryListener?.onFinish(getResult())
                    }
                } ?: mEasyGolfGalleryListener?.onOpenCamera()
            }
        }

        fun bindData(position: Int) {
            getItem(position)?.let { model ->
                clearCameraView()
                itemView.photoGallery.loadImage(Uri.parse(model.path))
                itemView.iconChecker.visibility = if (mListGalleryMediaIdSelected.containsKey(model.id)) View.VISIBLE else View.GONE
            } ?: initCameraView()
        }

        private fun clearCameraView() {
            itemView.photoCamera.visibility = View.GONE
            itemView.photoGallery.visibility = View.VISIBLE
        }

        private fun initCameraView() {
            itemView.photoCamera.visibility = View.VISIBLE
            itemView.photoGallery.visibility = View.GONE
            itemView.iconChecker.visibility = View.GONE
        }
    }

    interface EasyGolfGalleryListener {
        fun onOpenCamera()
        fun onSelect(counter: Int)
        fun onFinish(result: List<GalleryMedia>)
        fun onLimitSelect(limit: Int)
    }
}