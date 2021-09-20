package com.minhhop.easygolf.presentation.course

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.golf.TeeUtils
import kotlinx.android.synthetic.main.item_scorecard_course.view.*

class ScorecardCourseAdapter(context: Context) : EasyGolfRecyclerViewAdapter<Scorecard>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.item_scorecard_course
    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = ScorecardCourseHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? ScorecardCourseHolder)?.setData(position)
    }

    inner class ScorecardCourseHolder(itemView: View): EasyGolfRecyclerViewHolder(itemView){

        fun setData(position: Int){
            val scorecard = getItem(position)
            itemView.txtName.text = scorecard.type
            itemView.imgTee.backgroundTintList = ColorStateList.valueOf(TeeUtils.getColorByType(scorecard.type))
            itemView.txtDistance.text = scorecard.distance.toString()
            itemView.txtPar.text = scorecard.par.toString()
            itemView.txtCR.text = scorecard.cr.toString()
            itemView.txtSR.text = scorecard.sr.toString()
        }
    }
}
