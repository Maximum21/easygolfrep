package com.minhhop.easygolf.presentation.endgame.chart

import android.view.View
import android.view.ViewTreeObserver
import androidx.lifecycle.Observer
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import kotlinx.android.synthetic.main.fragment_score_chart.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel


class ScoreChartFragment : EasyGolfFragment<EndGameViewModel>() {

    override fun setLayout(): Int = R.layout.fragment_score_chart

    override val mViewModel: EndGameViewModel
            by sharedViewModel()

    override fun initView(viewRoot: View) {
       mViewModel.listDataScoreGolfLiveData.observe(viewLifecycleOwner, Observer {
           startAnimationChart(mViewModel.getHoldScorecardModel(it)?.scorecard)
       })
    }

    private fun startAnimationChart(data:List<DataScoreGolf?>?){
        if(viewRoot.chartScore.isShown){
            viewRoot.chartScore.scaleView(data,mViewModel.findParOfHoleInDataScoreGolf(data))
        }else {
            viewRoot.chartScore.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewRoot.chartScore.scaleView(data,mViewModel.findParOfHoleInDataScoreGolf(data))
                    viewRoot.chartScore.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    override fun loadData() {

    }

}