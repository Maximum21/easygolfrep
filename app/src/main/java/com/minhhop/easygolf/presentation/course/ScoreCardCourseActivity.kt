package com.minhhop.easygolf.presentation.course

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import org.koin.android.ext.android.inject

class ScoreCardCourseActivity : EasyGolfActivity<ScoreCardCourseViewModel>() {
    private var mScoreCardCourseAdapter: ScorecardCourseAdapter? = null
    override fun initView() {
        val listDataTee: RecyclerView = findViewById(R.id.listDataTee)
        listDataTee.layoutManager = LinearLayoutManager(this)
        mScoreCardCourseAdapter = ScorecardCourseAdapter(this)
        listDataTee.adapter = mScoreCardCourseAdapter

    }

    override fun loadData() {
        EasyGolfNavigation.scoreCardCourseBundle(intent)?.let { scorecardScoreBundle ->
            scorecardScoreBundle.getResult()?.let { dataScorecard->
                mScoreCardCourseAdapter?.setDataList(dataScorecard)
            }
        }?:finish()

    }

    override val mViewModel: ScoreCardCourseViewModel
        by inject()

    override fun setLayout(): Int = R.layout.activity_scorecard_course
}