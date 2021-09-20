package com.minhhop.easygolf.presentation.golf.component.score.picker

import android.content.Context
import android.view.View
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.listeners.EventSelectMoreScore

class ScoreListAdapter(context: Context,private val mPar:Int?,private val onSelected:(Int)->Unit) : EasyGolfRecyclerViewAdapter<Int>(context) {

    override fun setLayout(viewType: Int): Int {
        return R.layout.item_list_score
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): ScoreListViewHolder {
        return ScoreListViewHolder(viewRoot)
    }
    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ScoreListViewHolder)?.bindData(position)
    }

    inner class ScoreListViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){
        private var mTxtValue:TextView = itemView.findViewById(R.id.txtValue)
        private var mLayer:View = itemView.findViewById(R.id.layer)

        fun bindData(position: Int){
            mTxtValue.text = getItem(position).toString()
            mPar?.let { par->
                mLayer.setBackgroundResource(EasyGolfPickerScoreView.getResResource(getItem(position),par))
            }
        }
        init {
            mLayer.setOnClickListener {
                onSelected(getItem(adapterPosition))
            }
        }
    }

}