package com.minhhop.easygolf.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.Filter
import android.widget.Filterable
import com.minhhop.core.domain.golf.Club
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import kotlinx.android.synthetic.main.courses_search_row_layout.view.*
import java.text.DecimalFormat

class SearchCourseAdapter (context: Context, val viewEvent: SearchCoursesView) : EasyGolfRecyclerViewAdapter<Club>(context), Filterable {

    private var coursesFilter: ArrayList<Club> = ArrayList()

    override fun setLayout(viewType: Int): Int{
        return R.layout.courses_search_row_layout
    }

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? EasyGolfCourseHolder)?.bindData(position)
    }

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = EasyGolfCourseHolder(viewRoot)

    inner class EasyGolfCourseHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.setOnClickListener {
                viewEvent.onClickListener(mDataList[adapterPosition])
            }
        }
        @SuppressLint("CheckResult")
        fun bindData(position: Int){
            val model = getItem(position)
            itemView.course_row_name_tv.text = model.name
            itemView.course_row_type_tv.visibility = View.GONE
            itemView.course_row_address_tv.text = model.address

            var value = DecimalFormat("##.##").format((model.distance?:0.0)/1000)
            value = context.getString(R.string.format_distance, value)
            if (!TextUtils.isEmpty(value)) {
                itemView.course_row_holes_tv.text = value
            }

//            itemView.course_row_holes_tv.text = model.distance.toString()

        }
    }


    interface SearchCoursesView{
        fun onClickListener(images: Club)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                if (charSearch.isEmpty()) {
                    coursesFilter = mDataList
                } else {
                    val resultList : ArrayList<Club> = ArrayList()
                    for (row in mDataList) {
                        if (row.name!!.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            resultList.add(row)
                        }
                    }
                    coursesFilter = resultList
                }
                val filterResults = FilterResults()
                filterResults.values = coursesFilter
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                coursesFilter = results?.values as ArrayList<Club>
                notifyDataSetChanged()
            }
        }
    }
}