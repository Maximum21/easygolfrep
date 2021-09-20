package com.minhhop.easygolf.presentation.round

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.profile.Round
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.utils.AppUtil
import kotlinx.android.synthetic.main.round_row_for_user.view.*

class RoundHistoryAdapter (context: Context, val historyEvent: RoundHistoryEvent) : EasyGolfRecyclerViewAdapter<Round>(context) {

    override fun setLayout(viewType: Int): Int{
        return R.layout.round_row_for_user
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                historyEvent.onClickListener(mDataList[adapterPosition],adapterPosition)
            }
        }
        fun bindData(position: Int){
            val round = getItem(position)
            var clubName = ""
            round.club_name?.let{
                if (it.isNotEmpty()) {
                    clubName = it
                }
            }
            round.course_name?.let{
                if(it.isNotEmpty()) {
                    clubName = "$clubName($it)"
                }
            }

            itemView.round_history_row_extra_circle_iv.setColorFilter(
                    TeeUtils.getColorByType(round.tee),PorterDuff.Mode.SRC_IN)
            itemView.round_history_row_club_name.text = clubName
            itemView.round_history_row_date_tv.text = AppUtils.convertISOToDate(round.date,context)

            itemView.round_history_row_tepar_count.text = round.hdc.toInt().toString()
            itemView.round_history_row_strokes_count.text = round.scores.toInt().toString()
            itemView.round_history_row_thru_count.text = round.thru.toString()

            round.over.let {
                if(0 == it.toInt()){
                    itemView.round_history_row_grossnet_values_tv.text = "E"
                }else{
                    itemView.round_history_row_grossnet_values_tv.text = it.toInt().toString()
                }
            }
        }
    }
    interface RoundHistoryEvent{
        fun onClickListener(data: Round, adapterPosition: Int)
    }
}