package com.minhhop.easygolf.presentation.rules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.minhhop.core.domain.rule.GolfRuleItem
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.models.RuleGolfItem
import com.minhhop.easygolf.views.activities.RuleGolfDetailActivity
import kotlinx.android.synthetic.main.item_expansion_rule_golf.view.*

class GolfRuleItemAdapter(context : Context,var mMainIndex: Int,var mTitle: String?) : EasyGolfRecyclerViewAdapter<GolfRuleItem>(context) {


    override fun setLayout(viewType: Int): Int {
        return R.layout.item_expansion_rule_golf
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnClickListener { v: View? ->
                val bundle = Bundle()
                bundle.putString(Contains.EXTRA_TITLE_RULE, mTitle)
                val targetTitle: String = itemView.txtIndex.text.toString() + "   " + itemView.txtName.text.toString()
                bundle.putString(Contains.EXTRA_TITLE_RULE_CHILD, targetTitle)
                val temp = mDataList[adapterPosition]
                val eRuleGolf = RuleGolfItem()
                eRuleGolf.index = temp.index
                eRuleGolf.id = temp.id
                eRuleGolf.content = temp.content
                eRuleGolf.slug = temp.slug
                bundle.putSerializable(Contains.EXTRA_RULE, eRuleGolf)
                val intent = Intent(context, RuleGolfDetailActivity::class.java)
                intent.putExtras(bundle)
                context.startActivity(intent)
            }
        }
        fun bindData(position: Int){
            val model = getItem(position)
            if (position == 0) {
                itemView.txtIndex.visibility = View.INVISIBLE
                itemView.txtName.setTextColor(ContextCompat.getColor(context,R.color.colorLinker))
            }

            itemView.txtIndex.text = formatIndexItem(model.index)
            itemView.txtName.text = model.title
        }
    }

    private fun formatIndexItem(index: Int): String? {
        return "$mMainIndex.$index"
    }
}