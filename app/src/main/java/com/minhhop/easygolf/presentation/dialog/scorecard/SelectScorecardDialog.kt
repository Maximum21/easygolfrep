package com.minhhop.easygolf.presentation.dialog.scorecard

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfDialog
import kotlinx.android.synthetic.main.dialog_select_course.*

class SelectScorecardDialog(context: Context) : EasyGolfDialog(context) {

    private var mSelectScorecardAdapter: SelectScorecardAdapter? = null
    private var mSelectCourseListener: SelectScorecardListener? = null
    private var mListData:List<Scorecard>? = null

    override fun initView() {
        textTitle.text = context.getString(R.string.select_tee)
        mSelectScorecardAdapter = SelectScorecardAdapter(context,object : SelectScorecardListener {
            override fun onSelected(scorecard: Scorecard) {
                mSelectCourseListener?.onSelected(scorecard)
                dismiss()
            }
        })
        mListData?.let{data->
            mSelectScorecardAdapter?.setDataList(data)
        }
        listData.layoutManager = LinearLayoutManager(context)
        listData.adapter = mSelectScorecardAdapter
    }

    fun setData(listData:List<Scorecard>?, selectCourseListener: SelectScorecardListener): SelectScorecardDialog {
        mSelectCourseListener = selectCourseListener
        mListData = listData
        return this
    }

    override fun cancelTouchOutside(): Boolean = true

    override fun resContentView(): Int = R.layout.dialog_select_course

    interface SelectScorecardListener{
        fun onSelected(scorecard: Scorecard)
    }
}