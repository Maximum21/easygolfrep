package com.minhhop.easygolf.presentation.golf.component.tee

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.golf.Tee
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.golf.TeeUtils
import kotlinx.android.synthetic.main.item_segment_tee.view.*

class SegmentTeeAdapter(context: Context, private val mOnTeeListener: OnTeeListener) : EasyGolfRecyclerViewAdapter<Tee>(context) {
    private var mIndexSelected = -1
    fun setIndexSelected(index: Int) {
        val temp = mIndexSelected
        mIndexSelected = index
        notifyItemChanged(temp)
        notifyItemChanged(mIndexSelected)
    }

    fun getSelectedTee() :Tee?{
        return if(mIndexSelected in 0 until itemCount){
            getItem(mIndexSelected)
        }else{
            null
        }
    }

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_segment_tee
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder {
        return SegmentTeeHolder(viewRoot)
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? SegmentTeeHolder)?.bindData(position)
    }

    inner class SegmentTeeHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {

        fun bindData(position: Int){
            val model = getItem(position)
            if (model.type.equals("white", ignoreCase = true)) {
                if(position == mIndexSelected) {
                    itemView.imgIcon.setImageResource(R.drawable.ic_icon_option_tee_selected_white)
                }else{
                    itemView.imgIcon.setImageResource(R.drawable.ic_icon_option_tee_white)
                }
                itemView.imgIcon.imageTintList = ColorStateList.valueOf(ContextCompat.getColor(context,R.color.colorBlack))
            }else{
                if(position == mIndexSelected) {
                    itemView.imgIcon.setImageResource(R.drawable.ic_icon_option_tee_selected)
                }else{
                    itemView.imgIcon.setImageResource(R.drawable.ic_icon_option_tee)
                }
                itemView.imgIcon.imageTintList = ColorStateList.valueOf(TeeUtils.getColorByType(model.type))
            }
            itemView.viewRoot.setOnClickListener {
                if (position != mIndexSelected) {
                    setIndexSelected(position)
                    mOnTeeListener.onClickTee(model,
                            position)
                }
            }
        }
    }

}