package com.minhhop.easygolf.presentation.dialog.course

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.golf.Course
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfDialog
import kotlinx.android.synthetic.main.dialog_select_course.*

class SelectCourseDialog(context: Context) : EasyGolfDialog(context) {

    private var mSelectCourseAdapter: SelectCourseAdapter? = null
    private var mSelectCourseListener: SelectCourseListener? = null
    private var mListData:List<Course>? = null

    override fun initView() {
        textTitle.text = context.getString(R.string.select_course)
        mSelectCourseAdapter = SelectCourseAdapter(context,object : SelectCourseListener {
            override fun onSelected(course: Course) {
                mSelectCourseListener?.onSelected(course)
                dismiss()
            }

        })
        mListData?.let{data->
            mSelectCourseAdapter?.setDataList(data)
        }
        listData.layoutManager = LinearLayoutManager(context)
        listData.adapter = mSelectCourseAdapter
    }

    fun setData(listData:List<Course>?, selectCourseListener: SelectCourseListener): SelectCourseDialog {
        mSelectCourseListener = selectCourseListener
        mListData = listData
        return this
    }

    override fun cancelTouchOutside(): Boolean = true

    override fun resContentView(): Int = R.layout.dialog_select_course

    interface SelectCourseListener{
        fun onSelected(course: Course)
    }
}