package com.minhhop.easygolf.presentation.scorecard

import android.util.Log
import com.minhhop.core.domain.golf.Hole
import com.minhhop.core.domain.golf.ScorecardModel
import com.minhhop.easygolf.R
import kotlinx.android.synthetic.main.fragment_scorecard_end_game_view.view.*

class ScorecardEndgameViewFragment : ScorecardViewFragment() {
    companion object {
        const val TAG = "ScorecardEndgameViewFragment"
    }
    override fun setLayout(): Int = R.layout.fragment_scorecard_end_game_view

    override fun setDataScorecard(holes: List<Hole>, dataScore: List<ScorecardModel>?, userId: String?) {
        viewRoot.scorecardViewEndGame.initTable(holes,dataScore,userId)
    }

    override fun onOrientationChange() {
        viewRoot.scorecardViewEndGame.clearAllView()
    }
}