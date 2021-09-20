package com.minhhop.easygolf.adapter


import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.minhhop.easygolf.presentation.endgame.chart.FairHitChartFragment
import com.minhhop.easygolf.presentation.endgame.chart.GreenChartFragment
import com.minhhop.easygolf.presentation.endgame.chart.ScoreChartFragment

class ChartGamePagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){

    override fun getItem(position: Int): Fragment {
        return when(position){
            0->{
                val fm = FairHitChartFragment()
                fm
            }
            1->{
                val fm = GreenChartFragment()
                fm
            }
            2->{
                val fm = ScoreChartFragment()
                fm

            }
            else -> FairHitChartFragment()
        }
    }

    override fun getCount(): Int = 3
}