package com.minhhop.easygolf.presentation.endgame

import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.*
import com.minhhop.core.domain.golf.DataScoreGolf.FairwayHit.*
import com.minhhop.core.domain.golf.DataScoreGolf.GreenInRegulation.HIT
import com.minhhop.core.domain.golf.DataScoreGolf.GreenInRegulation.MISS
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.EndGameBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle.TypeGame.*
import kotlinx.coroutines.launch

class EndGameViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    var mEndGameBundle: EndGameBundle? = null
    val listDataScoreGolfLiveData = MutableLiveData<List<ScorecardModel>?>()
    fun fetchDataScore(roundId: String) {
        when (mEndGameBundle?.typeGame) {
            EXPLORE -> {
            } //do nothing
            NEW_GAME -> {
                val result = interactors.getAllDataScoreGolfByRoundInLocal(roundId) {
                    mCommonErrorLive.postValue(it)
                }

                getProfileUserInLocal()?.let { user ->
                    listDataScoreGolfLiveData.postValue(
                            arrayListOf(ScorecardModel(user.id, result))
                    )
                }
            }
            BATTLE_GAME, UNKNOWN -> {
                interactors.isRoundExitInFirebase(roundId, { isExit ->
                    if (isExit) {
                        interactors.getDataScoreAtRoundInFirebase(roundId, {
                            listDataScoreGolfLiveData.postValue(it)
                        }, {
                            mCommonErrorLive.postValue(it)
                        })
                    } else {
                        roundGolfLiveData.value?.let { round ->
                            if (!round.matches.isNullOrEmpty()) {
                                listDataScoreGolfLiveData.postValue(getDataScoreInRound(round, roundId))
                            } else {
                                fetchDataScoreFromRound(roundId)
                            }
                        }
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })

            }
        }
    }

    private fun getDataScoreInRound(roundGolf: RoundGolf, roundId: String): ArrayList<ScorecardModel> {
        val result = ArrayList<ScorecardModel>()
        /**
         * get data score for current user
         * */
        getProfileUserInLocal()?.let { currentUser ->
            val listScore = ArrayList<DataScoreGolf>()
            roundGolf.matches?.forEach { dataAtHole ->

                listScore.add(dataAtHole.toDataScoreGolf(roundId))
            }
            result.add(ScorecardModel(currentUser.id, listScore))
        }
        /**
         * get data score for friends
         * */
        roundGolf.friends?.forEach { friends ->
            val listScore = ArrayList<DataScoreGolf>()
            friends.matches?.forEach { dataAtHole ->
                listScore.add(dataAtHole.toDataScoreGolf(roundId))
            }
            result.add(ScorecardModel(friends.user_id, listScore))
        }
        return result
    }

    private fun fetchDataScoreFromRound(roundId: String) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                listDataScoreGolfLiveData.postValue(getDataScoreInRound(it, roundId))
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    private var mBattleRound: BattleRound? = null
    fun getBattleRoundInFirebase(roundId: String, onResult: (BattleRound?) -> Unit) {
        getProfileUserInLocal()?.let { user ->
            interactors.getBattleRoundInFirebase(roundId, user.id, {
                mBattleRound = it
                onResult(it)
            }) {
                mCommonErrorLive.postValue(it)
            }
        }
    }

    fun getTotalPar(): Int {
        var result = 0
        roundGolfLiveData.value?.holes?.let { hole ->
            listDataScoreGolfLiveData.value?.let {
                it.find { scorecardModel ->
                    scorecardModel.userId == "94"
                }
            }?.scorecard?.forEach { dataScoreGolf ->
                dataScoreGolf?.number?.let { numberHole ->
                    hole.find {
                        it.number == numberHole
                    }?.let { findHole ->
                        result += findHole.par
                    }
                }
            }
        }
        return result
    }

    /***
     *
     * To hold dataScore user for temp
     */
    private var mScorecardModelForCurrentUser: ScorecardModel? = null
    fun getHoldScorecardModel(data: List<ScorecardModel>?): ScorecardModel? {
        if (mScorecardModelForCurrentUser != null) {
            return mScorecardModelForCurrentUser!!
        }
        return getProfileUserInLocal()?.let { currentUser ->
            data?.findLast {
                it.userId == currentUser.id
            }
        }
    }

    private var mTotalStroke: Int? = null
    private var mTotalThruAtRound: Int? = null
    fun getStrokes(data: List<ScorecardModel>?): Int {
        if (mTotalStroke != null) {
            return mTotalStroke!!
        }
        var result = 0
        var tempThru = 0
        getHoldScorecardModel(data)?.let { element ->
            element.scorecard?.forEach { item ->
                result += item?.score ?: 0
                if (item?.isComplete() == true) {
                    tempThru++
                }
            }
        }

        mTotalStroke = result
        mTotalThruAtRound = tempThru
        return result
    }

    fun getThruAtRound(data: List<ScorecardModel>?): Int {
        if (mTotalThruAtRound != null) {
            return mTotalThruAtRound!!
        }
        var result = 0
        var tempStroke = 0
        getHoldScorecardModel(data)?.let { element ->
            element.scorecard?.forEach { item ->
                tempStroke += item?.score ?: 0
                if (item?.isComplete() == true) {
                    result++
                }
            }
        }
        mTotalStroke = tempStroke
        mTotalThruAtRound = result
        return result
    }

    fun getFairHit(data: List<ScorecardModel>?, onResult: (Int, Int, Int) -> Unit) {
        var left = 0
        var center = 0
        var right = 0
        getHoldScorecardModel(data)?.let { element ->
            element.scorecard?.forEach {
                it?.let { item ->
                    when (item.getFairwayHit()) {
                        LEFT -> {
                            left++
                        }
                        CENTER -> {
                            center++
                        }
                        RIGHT -> {
                            right++
                        }
                        NONE -> {

                        }
                    }
                }
            }
        }

        onResult(left, center, right)
    }

    fun getGreenInRegulation(data: List<ScorecardModel>?, onResult: (Int, Int) -> Unit) {
        var miss = 0
        var hit = 0
        getHoldScorecardModel(data)?.let { element ->
            element.scorecard?.forEach {
                it?.let { item ->
                    when (item.getGreenInRegulation()) {
                        MISS -> {
                            miss++
                        }
                        HIT -> {
                            hit++
                        }
                        DataScoreGolf.GreenInRegulation.NONE -> {
                        }
                    }
                }
            }
        }
        onResult(miss, hit)
    }

    fun findParOfHoleInDataScoreGolf(data: List<DataScoreGolf?>?): HashMap<Int, Int> {
        val mHashMapParHole = HashMap<Int, Int>()
        roundGolfLiveData.value?.holes?.let { holes ->
            data?.forEach {
                it?.let { item ->
                    item.number?.let { numberHole ->
                        if (numberHole >= 1 && numberHole <= holes.size) {
                            mHashMapParHole[numberHole] = holes[numberHole - 1].par
                        }
                    }

                }
            }
        }
        return mHashMapParHole
    }

    val roundGolfLiveData = MutableLiveData<RoundGolf>()

    fun getRoundGolf(roundId: String) {
        interactors.getRoundGolfByIdInLocal(roundId)?.let {
            roundGolfLiveData.postValue(it)
        } ?: fetchRoundGolf(roundId)
    }

    private fun fetchRoundGolf(roundId: String) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                roundGolfLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun getProfileUserInLocal() = interactors.getProfileInLocal {
        mCommonErrorLive.postValue(it)
    }

    fun onCompleteRound(handicapCheck: Boolean) {
        roundGolfLiveData.value?.let { roundGolf ->
            val matchList = ArrayList<MatchGolf>()
            roundGolf.holes?.let { hole ->
                getProfileUserInLocal()?.let { user ->
                    listDataScoreGolfLiveData.value?.findLast {
                        it.userId == user.id
                    }?.scorecard?.forEach { dataScore ->
                        dataScore?.let { scoreGolf ->
                            scoreGolf.number?.let { numberHole ->
                                if (numberHole >= 1 && numberHole <= hole.size) {

                                    val matchGolf = MatchGolf.fromDataScoreGolf(scoreGolf,
                                            hole[numberHole - 1].hole_id)
                                    matchList.add(matchGolf)
                                }
                            }
                        }
                    }
                }
                interactors.fetchDataScoreGolfMembersInBattleFirebase(roundGolf, { dataScoreMembers ->
                    val result = RoundHost(
                            handicapCheck,
                            matchList, dataScoreMembers
                    )
                    roundGolf.id?.let { roundGolfId ->
                        onCompleteRound(roundGolfId, result)
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })
            }
            /**
             * hole id/ tee id
             * */

        }
    }

    val callbackCompleteRoundLiveData = MutableLiveData<Boolean>()
    private fun onCompleteRound(roundId: String, data: RoundHost) {
        mScope.launch {
            interactors.onCompleteRoundGolf(roundId, data, {
                deleteDataRound(roundId, true)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun deleteDataRound(roundId: String?, isComplete: Boolean = false) {
        mScope.launch {
            (roundId ?: roundGolfLiveData.value?.id)?.let { roundGolfId ->
                interactors.deleteAllDataScoreGolfOfRoundInLocal(roundGolfId, {}, {
                    mCommonErrorLive.postValue(it)
                })

                if (mEndGameBundle?.typeGame == BATTLE_GAME) {
                    if (mBattleRound?.is_host == true) {
                        interactors.removeBattleRoundInFirebase(roundGolfId, {
                            callbackCompleteRoundLiveData.postValue(isComplete)
                        }, {
                            mCommonErrorLive.postValue(it)
                        })

                    } else {
                        getProfileUserInLocal()?.let { user ->
                            interactors.removeMemberFromBattleInFirebase(roundGolfId, user, {
                                callbackCompleteRoundLiveData.postValue(isComplete)
                            }, {
                                mCommonErrorLive.postValue(it)
                            })
                        }
                    }
                } else {
                    callbackCompleteRoundLiveData.postValue(isComplete)
                }
            }
        }

    }

    val hideViewMaskLive = MutableLiveData<Boolean>()
    fun friendApproveRound(roundId: String) {
        mScope.launch {
            interactors.friendApproveRoundComplete(roundId, {
                hideViewMaskLive.postValue(true)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun friendNotAcceptRound(roundId: String) {
        mScope.launch {
            interactors.friendNotAcceptRoundComplete(roundId, {
                hideViewMaskLive.postValue(true)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }
}