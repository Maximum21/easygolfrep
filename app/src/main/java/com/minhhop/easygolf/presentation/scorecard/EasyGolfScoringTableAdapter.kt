package com.minhhop.easygolf.presentation.scorecard

import android.content.Context
import android.view.View
import androidx.core.widget.TextViewCompat
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.models.ScoringTable
import com.minhhop.easygolf.presentation.golf.component.score.picker.EasyGolfPickerScoreView
import kotlinx.android.synthetic.main.tableview_cell_score_layout.view.*

class EasyGolfScoringTableAdapter(context: Context) : EasyGolfRecyclerViewAdapter<ScoringTable>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.tableview_row_score_layout
    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfScoringTableViewHolder(viewRoot)
    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfScoringTableViewHolder)?.bindData(position)
    }

    inner class EasyGolfScoringTableViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView) {
        fun bindData(position: Int) {
//            val model = getItem(position)
//
//            if(model.isScore()){
//                TextViewCompat.setTextAppearance(itemView.txtName, R.style.bold)
//                if(model.value == null){
//                    itemView.txtName.text = context.getString(R.string.dash)
//                    itemView.txtName.background = null
//                }else{
//                    itemView.txtName.text = model.value.toString()
//                    model.par?.let { par->
//                        itemView.txtName.setBackgroundResource(EasyGolfPickerScoreView.getResResource(model.value,par))
//                    }
//                }
//            }else{
//                TextViewCompat.setTextAppearance(itemView.txtName, R.style.normal)
//                itemView.txtName.text = model.name
//            }
        }
    }
}