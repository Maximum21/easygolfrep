package com.minhhop.easygolf.presentation.endgame

import android.content.res.ColorStateList
import android.util.Log
import android.view.View
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.Observer
import com.google.gson.Gson
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.golf.Scorecard
import com.minhhop.easygolf.R
import com.minhhop.easygolf.adapter.ChartGamePagerAdapter
import com.minhhop.easygolf.framework.base.EasyGolfActivity
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.common.EasyGolfNavigation
import com.minhhop.easygolf.framework.common.GolfUtils
import com.minhhop.easygolf.framework.dialogs.ConfirmDialog
import com.minhhop.easygolf.framework.golf.TeeUtils
import com.minhhop.easygolf.framework.models.MessageEvent
import com.minhhop.easygolf.presentation.scorecard.ScorecardEndgameViewFragment
import kotlinx.android.synthetic.main.activity_end_game.*
import kotlinx.android.synthetic.main.layout_loading_white.*
import org.greenrobot.eventbus.EventBus
import org.koin.android.viewmodel.ext.android.viewModel
import kotlin.math.roundToInt

class EndGameActivity : EasyGolfActivity<EndGameViewModel>() {

    private var mChartPagerAdapter: ChartGamePagerAdapter? = null
    private var mHandicapUserInThisCourse: Double? = null
    override val mViewModel: EndGameViewModel
            by viewModel()

    private var mNeedToApproveOrCancelThisRound: Boolean = false
    override fun setLayout(): Int = R.layout.activity_end_game

    override fun viewMask() {
        if (mStatusLoading == StatusLoading.FIRST_LOAD) {
            super.viewMask()
            viewMask.startBurnViewAnimation()
        } else {
            layoutLoadingLight.visibility = View.VISIBLE
        }
    }

    override fun hideMask() {
        if (viewMask.isBurning()) {
            super.hideMask()
            viewMask.stopBurnViewAnimation()
            mStatusLoading = StatusLoading.FINISH_LOAD
        } else {
            layoutLoadingLight.visibility = View.GONE
        }
    }

    override fun initView() {

        val transitionFragment = supportFragmentManager.beginTransaction()
        val findFragment = supportFragmentManager.findFragmentByTag(ScorecardEndgameViewFragment.TAG)
                ?: ScorecardEndgameViewFragment()
        transitionFragment.replace(R.id.containerScorecard, findFragment, ScorecardEndgameViewFragment.TAG)
        transitionFragment.commit()

        layoutLoadingLight.setOnClickListener { }
        EasyGolfNavigation.endGameBundle(intent)?.let { endGameBundle ->
            if(endGameBundle.typeGame == PlayGolfBundle.TypeGame.BATTLE_GAME || endGameBundle.typeGame == PlayGolfBundle.TypeGame.UNKNOWN){
                viewMask()
            }
            if (endGameBundle.typeGame == PlayGolfBundle.TypeGame.BATTLE_GAME) {
                mViewModel.getBattleRoundInFirebase(endGameBundle.roundId) {
                    if (it != null) {
                        if (it.is_host == true) {
                            btSaveGame.visibility = View.VISIBLE
                            layoutCheckHandicap.visibility = View.VISIBLE
                        } else {
                            btSaveGame.visibility = View.GONE
                            layoutCheckHandicap.visibility = View.GONE
                        }
                    } else {
                        if (!mNeedToApproveOrCancelThisRound) {
                            btSaveGame.visibility = View.GONE
                            btDeleteGame.visibility = View.GONE
                        }
                    }
                }
            } else {
                btSaveGame.visibility = View.VISIBLE
                btDeleteGame.visibility = View.VISIBLE
                layoutCheckHandicap.visibility = View.VISIBLE
            }
            mViewModel.mEndGameBundle = endGameBundle
            initViewPager()
        } ?: finish()

        viewScorecard.setOnClickListener {
            mViewModel.mEndGameBundle?.let { endGameBundle ->
                EasyGolfNavigation.scorecardDirection(this, endGameBundle)
            }
        }

        mViewModel.listDataScoreGolfLiveData.observe(this, Observer {
            textThruView.text = mViewModel.getThruAtRound(it).toString()
            val totalScore = mViewModel.getStrokes(it)
            textStrokesView.text = totalScore.toString()
            val overHDC = totalScore - mViewModel.getTotalPar() - getHDC()
            textOverHandicapView.text = if (overHDC != 0.0) {
                overHDC.roundToInt().toString()
            } else {
                getString(R.string.not_available_over)
            }
            hideMask()
        })

        mViewModel.roundGolfLiveData.observe(this, Observer {
            toolbarBack.title = getString(R.string.format_name_course, it.club_name, it.course_name)
            it.date?.let { date ->
                textDateStart.text = AppUtils.convertISOToDate(date, this)
            }
            if (mViewModel.mEndGameBundle?.teeType == null) {
                mViewModel.mEndGameBundle?.teeType = it.teeType
            }
            mViewModel.mEndGameBundle?.getScorecards()?.let { dataScorecard ->

                Log.e("WOW", "----start dataScorecard-----")
                Log.e("WOW", Gson().toJson(dataScorecard))
                Log.e("WOW", "----end dataScorecard-----")
                Log.e("WOW", "mViewModel.mEndGameBundle?.teeType: ${mViewModel.mEndGameBundle?.teeType}")
                findTeeByType(dataScorecard)
            } ?: findTeeByType(it.course?.scorecard)
            TextViewCompat.setCompoundDrawableTintList(textTeeView,
                    ColorStateList.valueOf(TeeUtils.getColorByType(mViewModel.mEndGameBundle?.teeType)))

            /**
             * check this round is finish
             * */
            handicapCheckView.isEnabled = !it.isFinish()
            if (it.isFinish()) {
                if (it.is_host == false && it.getStage() == RoundGolf.Stage.PENDING) {
                    mNeedToApproveOrCancelThisRound = true
                    btSaveGame.visibility = View.VISIBLE
                    btDeleteGame.visibility = View.VISIBLE
                    Log.e("WOW", "inside here")
                    btSaveGame.setText(getString(R.string.approve))
                    btDeleteGame.text = getString(R.string.cancel)
                } else {
                    btSaveGame.visibility = View.GONE
                    btDeleteGame.visibility = View.GONE
                }
            }

            it.id?.let { roundGolfId ->
                mViewModel.fetchDataScore(roundGolfId)
            } ?: finish()
        })

        mViewModel.callbackCompleteRoundLiveData.observe(this, Observer {
            EventBus.getDefault().post(MessageEvent())
            finish()
        })

        mViewModel.hideViewMaskLive.observe(this, Observer {
            hideMask()
            if (mNeedToApproveOrCancelThisRound) {
                btSaveGame.visibility = View.GONE
                btDeleteGame.visibility = View.GONE
            }
        })
    }

    private fun findTeeByType(dataScorecard: List<Scorecard>?) {
        if (!dataScorecard.isNullOrEmpty()) {
            mViewModel.mEndGameBundle?.scorecards = PlayGolfBundle.toScorecard(dataScorecard)
        }
        TeeUtils.getTeeByType(dataScorecard, mViewModel.mEndGameBundle?.teeType)?.let { currentBaseTee ->
            textDistanceView.text = currentBaseTee.distance.toString()
            textRatingSLView.text = getString(R.string.format_rating_or_sl, currentBaseTee.cr?.toString(), currentBaseTee.sr?.toString())
            textHDCView.text = GolfUtils.roundHandicap(
                    getHDC()
            )
        }
    }

    private fun getHDC(): Double {
        return mHandicapUserInThisCourse
                ?: mViewModel.mEndGameBundle?.getScorecards()?.let { dataScorecard ->
                    TeeUtils.getTeeByType(dataScorecard, mViewModel.mEndGameBundle?.teeType)?.let { currentBaseTee ->
                        mViewModel.getProfileUserInLocal()?.let { user ->
                            val result = GolfUtils.calculateUserHandicapByCourse(currentBaseTee, user)
                                    ?: user.handicap
                            mHandicapUserInThisCourse = result
                            result
                        } ?: 0.0
                    } ?: 0.0
                } ?: 0.0
    }

    private fun initViewPager() {
        mChartPagerAdapter = ChartGamePagerAdapter(supportFragmentManager)
        viewChart.adapter = mChartPagerAdapter
        viewChart.setCurrentItem(0, false)
        indicatorChart.setViewPager(viewChart)
    }

    override fun loadData() {
        mViewModel.mEndGameBundle?.let { endGameBundle ->
            mViewModel.getRoundGolf(endGameBundle.roundId)
        }
        btSaveGame.setOnClickListener {
            mViewModel.roundGolfLiveData.value?.let { roundGolf ->
                val roundGolfId = roundGolf.id
                viewMask()
                if (mNeedToApproveOrCancelThisRound && roundGolfId != null) {
                    mViewModel.friendApproveRound(roundGolfId)
                } else {
                    mViewModel.onCompleteRound(handicapCheckView.isChecked)
                }
            }
        }
        btDeleteGame.setOnClickListener {
            mViewModel.roundGolfLiveData.value?.let { roundGolf ->
                val roundGolfId = roundGolf.id
                if (mNeedToApproveOrCancelThisRound && roundGolfId != null) {
                    ConfirmDialog(this@EndGameActivity)
                            .setContent(getString(R.string.confirm_content_delete_game))
                            .setOnConfirm {
                                viewMask()
                                mViewModel.friendNotAcceptRound(roundGolfId)
                            }.show()
                } else {
                    ConfirmDialog(this@EndGameActivity)
                            .setContent(getString(R.string.confirm_content_delete_game))
                            .setOnConfirm {
                                viewMask()
                                mViewModel.deleteDataRound(null, false)
                            }.show()
                }
            }
        }
    }

}