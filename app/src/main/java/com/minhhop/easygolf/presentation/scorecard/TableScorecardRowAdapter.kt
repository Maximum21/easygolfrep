package com.minhhop.easygolf.presentation.scorecard

import android.content.Context
import android.view.View
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder

class TableScorecardRowAdapter(context:Context) : EasyGolfRecyclerViewAdapter<String>(context) {

    override fun setLayout(viewType: Int): Int =  R.layout.tableview_cell_score_layout

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = TableScorecardRowViewHolder(viewRoot)

    inner class TableScorecardRowViewHolder(itemView:View) : EasyGolfRecyclerViewHolder(itemView)
}