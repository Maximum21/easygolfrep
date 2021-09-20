package com.minhhop.easygolf.presentation.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.User
import com.minhhop.core.domain.golf.ClubPhoto
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.common.AppUtils
import kotlinx.coroutines.*
import java.io.File
import java.io.InputStream
import java.util.*

class EasyGolfHomeViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    fun logout() {
        interactors.logout()
    }

    val userLiveData = MutableLiveData<User>()
    fun fetchProfileUser() {
        mScope.launch {
            interactors.getProfileUser({
                userLiveData.postValue(it)
                interactors.updateUserProfileInLocal(it, {}, {})
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun getProfileUserInLocal() = interactors.getProfileInLocal { mCommonErrorLive.postValue(it) }

    private var mTypeGame: PlayGolfBundle.TypeGame? = null
    fun getTypeGame() = mTypeGame ?: PlayGolfBundle.TypeGame.NEW_GAME
    val needShowAlertFinishRoundLiveData = MutableLiveData<RoundGolf>()
    var mTeeTypeHolder: String? = null
    private var coroutineJobCheckRoundStart: Job? = null
    private var coroutineJobCheckRoundEnd: Job? = null
    fun destroyJobCheckRound() {
        coroutineJobCheckRoundStart?.cancel()
        coroutineJobCheckRoundEnd?.cancel()
    }

    fun checkNeedConfirmFinishRound() {
        mScope.launch {
            try {
                //get from local
                val easyGolfApp = interactors.getEasyGolfAppDataInLocal()
                easyGolfApp?.roundId?.let { roundId ->
                    var roundGolfHold = roundId
                    var timeToDelay = 0L
                    getProfileUserInLocal()?.let { user ->
                        interactors.getBattleRoundInFirebase(roundId, user.id, {
                            mTeeTypeHolder = it?.tee_type
                            Log.e("WOW","mTeeTypeHolder = ${ it?.tee_type}")
                            if (it?.round_id != easyGolfApp.roundId && it != null) {
                                timeToDelay = 0L
                                mTypeGame = PlayGolfBundle.TypeGame.NEW_GAME
                                roundGolfHold = it.round_id ?: roundGolfHold
                            } else {
                                if (!easyGolfApp.isOnFirebase) {
                                    mTypeGame = PlayGolfBundle.TypeGame.BATTLE_GAME
                                    easyGolfApp.timeToCheckExitRound?.let { time ->
                                        timeToDelay = getTimeDelay(time)
                                    }
                                } else {
                                    mTypeGame = PlayGolfBundle.TypeGame.NEW_GAME
                                    interactors.deleteEasyGolfAppDataByRoundId(roundGolfHold)
                                    return@getBattleRoundInFirebase
                                }
                            }

                            coroutineJobCheckRoundStart = mScope.launch {
                                delay(timeToDelay)
                                if (this.isActive) {
                                    findRoundById(roundGolfHold)
                                }
                            }

                        }, {
                            mCommonErrorLive.postValue(it)
                        })
                    }
                } ?: interactors.getBattleRoundFindFirstInFirebaseForCurrentUser({
                    Log.e("WOW","mTeeTypeHolder2 = ${ it?.tee_type}")
                    if (it != null) {
                        it.round_id?.let { roundGolfId ->
                            mTeeTypeHolder = it.tee_type
                            mTypeGame = PlayGolfBundle.TypeGame.BATTLE_GAME
                            getRoundInLocal(roundGolfId)?.let { roundGolf ->
                                if (!roundGolf.course?.scorecard.isNullOrEmpty()) {
                                    roundGolf.teeType = it.tee_type
                                    roundGolf.course_id = (it.course_id ?: roundGolf.course_id)
                                    needShowAlertFinishRoundLiveData.postValue(roundGolf)
                                } else {
                                    fetchRoundGolf(roundGolfId)
                                }
                            } ?: fetchRoundGolf(roundGolfId)
                        }
                    } else {
                        //find roundGolf in local
                        getRoundGolfExitInLocal()?.let { roundGolf ->
                            mTypeGame = PlayGolfBundle.TypeGame.NEW_GAME
                            needShowAlertFinishRoundLiveData.postValue(roundGolf)
                        }
                                ?: interactors.updateEasyGolfAppDataInLocal(EasyGolfApp()) //Remove all job
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })

            } catch (e: CancellationException) {
                e.printStackTrace()
            }
        }
    }

    private fun getTimeDelay(time: Long): Long {
        val currentTime = Calendar.getInstance().timeInMillis
        return 0L.coerceAtLeast(time - currentTime)
    }

    fun setJobToCheckRound(roundId: String) {
        val timeNeedUpdate = EasyGolfApp.buildNewTime()
        interactors.updateEasyGolfAppDataInLocal(EasyGolfApp(roundId = roundId, timeToCheckExitRound = timeNeedUpdate))
        coroutineJobCheckRoundEnd = mScope.launch {
            try {
                if (this.isActive) {
                    delay(getTimeDelay(timeNeedUpdate))
                    checkNeedConfirmFinishRound()
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
            }

        }
    }

    private fun findRoundById(roundId: String) {
        getRoundInLocal(roundId)?.let { roundGolf ->
            if (!roundGolf.course?.scorecard.isNullOrEmpty()) {
                roundGolf.teeType = mTeeTypeHolder
                needShowAlertFinishRoundLiveData.postValue(roundGolf)
            } else {
                fetchRoundGolf(roundId)
            }
        } ?: fetchRoundGolf(roundId)
    }

    fun resizeFileImage(path: String) = interactors.resizeImageInLocal(path, AppUtils.MAX_QUALITY_IMAGE_SCALE) { mCommonErrorLive.postValue(it) }

    private fun getRoundGolfExitInLocal() = interactors.getRoundGolfExitInLocal()
    private fun getRoundInLocal(roundId: String) = interactors.getRoundGolfByIdInLocal(roundId)
    private fun fetchRoundGolf(roundId: String) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                it.teeType = mTeeTypeHolder
                needShowAlertFinishRoundLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun createImageFileHolder(externalFilesDir: File, inputStream: InputStream) = interactors.createImageFileWithInputStream(externalFilesDir, inputStream)

    fun createImageFileHolder(externalFilesDir: File) = interactors.createImageFile(externalFilesDir)
    val newPhotoInsertLiveData = MutableLiveData<List<ClubPhoto>>()
    fun uploadPhotoFileForClub(files: List<File>) {
        mScope.launch {
            interactors.uploadPhotoForClub(files, {
                newPhotoInsertLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun deleteFileImage(path: String) = interactors.deleteFileInLocal(path)

    fun updateStatusPendingInBattleForUser(roundId: String) {
        interactors.getProfileInLocal {
            mCommonErrorLive.postValue(it)
        }?.let { user ->
            interactors.updateStatusPendingInBattleForUser(false, roundId, user, {}, {
                mCommonErrorLive.postValue(it)
            })
        }

    }
}