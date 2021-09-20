package com.minhhop.easygolf.presentation.rules

import android.content.Context
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.expansionpanel.ExpansionLayout
import com.github.florent37.expansionpanel.viewgroup.ExpansionLayoutCollection
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import kotlinx.android.synthetic.main.item_rule_golf.view.*

class GolfRuleAdapter(context: Context,val mRecyclerView: RecyclerView) : EasyGolfRecyclerViewAdapter<GolfRule>(context) {
    private var mItemRuleGolfAdapter: GolfRuleItemAdapter? = null
    private val expansionsCollection = ExpansionLayoutCollection()
    override fun setLayout(viewType: Int): Int {
        return R.layout.item_rule_golf
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.expansionLayout.addListener(ExpansionLayout.Listener { _: ExpansionLayout?, expanded: Boolean ->
                if (!expanded) {
                    itemView.diverView.visibility = View.VISIBLE
                } else {
                    itemView.diverView.visibility = View.INVISIBLE
                }
            })
            itemView.expansionLayout.addListener(ExpansionLayout.Listener { _: ExpansionLayout?, expanded: Boolean ->
                if (expanded) {
                    mRecyclerView.smoothScrollToPosition(adapterPosition)
                }
            })
        }
        fun bindData(position: Int){
            Log.e("TETINGKS","$position == ${getItem(position)}")
            val model = getItem(position)
            val listItem: RecyclerView = itemView.findViewById(R.id.list_item)
            val itemViewTitle: String = formatIndexItem(model.index) + "   " + model.title
            mItemRuleGolfAdapter = GolfRuleItemAdapter(context, model.index, itemViewTitle)
            listItem.layoutManager = LinearLayoutManager(context)
            listItem.adapter = mItemRuleGolfAdapter

            expansionsCollection.add(itemView.expansionLayout)
            itemView.expansionLayout.collapse(false)

            itemView.txtIndex.text = formatIndexItem(model.index)
            itemView.txtName.text = model.title
            Log.e("TETINGKS","$position =finish= ")
            model.rules?.let { mItemRuleGolfAdapter?.setDataList(it) }
        }
    }

    private fun formatIndexItem(index: Int): String? {
        var result: String = if (index < 10) {
            "0$index"
        } else {
            index.toString()
        }
        result += "."
        return result
    }
}