package com.minhhop.easygolf.adapter

import android.content.Context
import android.view.View
import android.widget.TextView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.base.BaseRecyclerViewAdapter
import com.minhhop.easygolf.core.CoreRecyclerViewHolder
import com.minhhop.easygolf.framework.models.CourseClub
import com.minhhop.easygolf.widgets.course.EventOnCourse

class SelectCourseAdapter(context:Context) : BaseRecyclerViewAdapter<CourseClub>(context) {

    private var onSelect: EventOnCourse? = null

    override fun setLayout(viewType: Int): Int = R.layout.item_select_course

    override fun setViewHolder(viewRoot: View?, viewType: Int): CoreRecyclerViewHolder = SelectCourseHolder(viewRoot!!)

    fun setEvent(event: EventOnCourse){
        onSelect = event
    }

    override fun onBindViewHolder(holder: CoreRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        if(holder is SelectCourseHolder){
            holder.onBinding(mDataList[position])
        }
    }

    private inner class SelectCourseHolder(itemView: View) : CoreRecyclerViewHolder(itemView){
        private val mTxtName:TextView = itemView.findViewById(R.id.txtName)
        init {
            itemView.setOnClickListener {
                this@SelectCourseAdapter.onSelect?.onSelect(mDataList[adapterPosition])
            }
        }

        fun onBinding(item:CourseClub){
            mTxtName.text = item.name
        }
    }
}