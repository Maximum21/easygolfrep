package com.minhhop.easygolf.presentation.golf.hole

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.golf.Hole
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder

class ListHoleAdapter(context: Context,private val onClickHole: (Hole)->Unit): EasyGolfRecyclerViewAdapter<Hole>(context) {

    private var mIndexSelected = 0

    override fun setLayout(viewType: Int): Int = R.layout.item_picker_hole

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder  = ListHoleHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ListHoleHolder)?.bindData(position)
    }

    fun updateIndexSelect(index:Int){
        if(index in 0 until itemCount) {
            mIndexSelected = index
            notifyDataSetChanged()
        }
    }

    inner class ListHoleHolder(itemView:View) : EasyGolfRecyclerViewHolder(itemView){

        private var mTxtValueHole: TextView = itemView.findViewById(R.id.txtValueHole)

        init {
            mTxtValueHole.setOnClickListener {
                if(mIndexSelected != adapterPosition) {
                    notifyItemChanged(mIndexSelected)
                    mIndexSelected = adapterPosition
                    onClickHole(mDataList[mIndexSelected])
                    notifyItemChanged(mIndexSelected)
                }
            }
        }

        fun bindData(position: Int){
            mTxtValueHole.text = (getItem(position).number).toString()
            if(mIndexSelected == position){
                mTxtValueHole.setTextColor(ContextCompat.getColor(context,R.color.colorWhite))
                mTxtValueHole.setBackgroundResource(R.drawable.background_select_hole)
            }else{
                mTxtValueHole.setTextColor(ContextCompat.getColor(context,R.color.textColorDark))
                mTxtValueHole.setBackgroundResource(0)
            }
        }
    }
}