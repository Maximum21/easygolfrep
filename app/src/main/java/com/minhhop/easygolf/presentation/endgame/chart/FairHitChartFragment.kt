package com.minhhop.easygolf.presentation.endgame.chart

import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfFragment
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import kotlinx.android.synthetic.main.fragment_fair_hit_chart.view.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class FairHitChartFragment : EasyGolfFragment<EndGameViewModel>() {

    override fun setLayout(): Int = R.layout.fragment_fair_hit_chart

    override fun onResume() {
        super.onResume()
        if(mStatusLoading == StatusLoading.FIRST_LOAD){
            mStatusLoading = StatusLoading.FINISH_LOAD
        }else{
            if(mStatusLoading == StatusLoading.REFRESH_LOAD){
                viewRoot.chartFairHit.startAnimationChart()
                mStatusLoading = StatusLoading.FINISH_LOAD
            }
        }
    }


    override val mViewModel: EndGameViewModel
            by sharedViewModel()

    override fun initView(viewRoot: View) {
        mViewModel.listDataScoreGolfLiveData.observe(viewLifecycleOwner, Observer {
            mViewModel.getFairHit(it) { left, center, right ->
                Log.e("WOW","left: $left--- center: $center---right $right")
                viewRoot.chartFairHit.setValue(left, center, right)
                if( mStatusLoading == StatusLoading.FINISH_LOAD) {
                    viewRoot.chartFairHit.startAnimationChart()
                }else{
                    mStatusLoading = StatusLoading.REFRESH_LOAD
                }
            }
        })
    }

    override fun loadData() {

    }

}