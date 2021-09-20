package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.Hole

class PickerHoleAdapter(mContext: Context, private val mPickerHoleItemListener: (Hole)->Unit) : BaseRecyclerViewAdapter<Hole>(mContext) {

    private var mPositionSelected = 0

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_picker_hole
    }


    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder {
        return PickerHoleHolder(viewRoot)
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? PickerHoleHolder)?.apply {
            setValue(mDataList[position].number)
            setSelected(mPositionSelected == position)
        }

    }

    fun setSelected(pos:Int){
        mPositionSelected = pos
        notifyDataSetChanged()
    }

    inner class PickerHoleHolder(itemView:View?)
        : CoreRecyclerViewHolder(itemView), View.OnClickListener{

        init {
            itemView!!.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            mPickerHoleItemListener(mDataList[adapterPosition])
            setSelected(adapterPosition)
        }

        private var mTxtValueHole:TextView = itemView!!.findViewById(R.id.txtValueHole)


        fun setValue(value:Int){
            mTxtValueHole.text = value.toString()
        }

        fun setSelected(selected:Boolean){
            if(selected){
                mTxtValueHole.setBackgroundResource(R.drawable.background_select_hole)
            }else{
                mTxtValueHole.setBackgroundResource(0)
            }
        }
    }
}