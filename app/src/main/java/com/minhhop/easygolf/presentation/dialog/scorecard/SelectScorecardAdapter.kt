package com.minhhop.easygolf.presentation.dialog.scorecard

import android.R.color
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.golf.TeeUtils
import kotlinx.android.synthetic.main.item_select_course.view.*


class SelectScorecardAdapter(context: Context,
                             private val selectScorecardListener: SelectScorecardDialog.SelectScorecardListener?) : EasyGolfRecyclerViewAdapter<Scorecard>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.item_select_course

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = SelectScorecardViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? SelectScorecardViewHolder)?.bindData(position)
    }
    inner class SelectScorecardViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.txtName.setOnClickListener {
                selectScorecardListener?.onSelected(getItem(adapterPosition))
            }
        }
        fun bindData(position:Int){
            val model = getItem(position)
            itemView.txtName.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.ic_icon_option_tee),null,null,null)
            if(itemView.txtName.compoundDrawables.isNotEmpty()){
                for (drawable in itemView.txtName.compoundDrawables){
                    drawable?.colorFilter = PorterDuffColorFilter(TeeUtils.getColorByType(model.type), PorterDuff.Mode.SRC_IN)
                }
            }
            itemView.txtName.text = context.getString(R.string.tee_format_distance,model.type,model.distance?.toString())
        }
    }
}