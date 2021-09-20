package com.minhhop.easygolf.presentation.dialog.course

import android.content.Context
import android.view.View
import com.minhhop.core.domain.golf.Course
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewAdapter
import com.minhhop.easygolf.framework.base.EasyGolfRecyclerViewHolder
import kotlinx.android.synthetic.main.item_select_course.view.*

class SelectCourseAdapter(context: Context,private val selectCourseListener: SelectCourseDialog.SelectCourseListener?) : EasyGolfRecyclerViewAdapter<Course>(context) {

    override fun setLayout(viewType: Int): Int = R.layout.item_select_course

    override fun setViewHolder(viewRoot: View, viewType: Int): EasyGolfRecyclerViewHolder = SelectCourseViewHolder(viewRoot)

    override fun onBindViewHolder(holder: EasyGolfRecyclerViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        (holder as? SelectCourseViewHolder)?.bindData(position)
    }
    inner class SelectCourseViewHolder(itemView: View) : EasyGolfRecyclerViewHolder(itemView){

        init {
            itemView.txtName.setOnClickListener {
                selectCourseListener?.onSelected(getItem(adapterPosition))
            }
        }
        fun bindData(position:Int){
            itemView.txtName.text = getItem(position).name
        }
    }
}