package com.minhhop.easygolf.presentation.scorecard

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.Observer
import com.minhhop.easygolf.R
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.common.GolfUtils
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.presentation.endgame.EndGameViewModel
import kotlinx.android.synthetic.main.activity_end_game.toolbarBack
import kotlinx.android.synthetic.main.activity_scorecard.*
import kotlinx.android.synthetic.main.fragment_scorecard_view.*
import org.koin.android.viewmodel.ext.android.viewModel

class ScorecardActivity : EasyGolfActivity<EndGameViewModel>() {

    override val mViewModel: EndGameViewModel
            by viewModel()
    override fun setLayout(): Int = R.layout.activity_scorecard

    override fun initView() {
        viewMask()
        val transitionFragment = supportFragmentManager.beginTransaction()
        val findFragment = supportFragmentManager.findFragmentByTag(ScorecardViewFragment.TAG)
                ?: ScorecardViewFragment()
        transitionFragment.replace(R.id.containerScorecard, findFragment, ScorecardViewFragment.TAG)
        transitionFragment.commit()

        EasyGolfNavigation.endGameBundle(intent)?.let { endGameBundle ->
            mViewModel.mEndGameBundle = endGameBundle
        } ?: finish()

        mViewModel.roundGolfLiveData.observe(this, Observer {
            toolbarBack.title = getString(R.string.format_name_course, it.club_name, it.course_name)

            it.id?.let { roundGolfId ->
                mViewModel.fetchDataScore(roundGolfId)
            } ?: finish()
        })


        buttonRotation.setOnClickListener {
            requestedOrientation = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } else {
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }
        }
    }

    override fun loadData() {
        mViewModel.mEndGameBundle?.let { endGameBundle ->
            mViewModel.getRoundGolf(endGameBundle.roundId)
        }
    }

}