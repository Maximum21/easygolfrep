package com.minhhop.easygolf.presentation.golf.play

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import com.minhhop.core.domain.EasyGolfApp
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.DataScoreGolf
import com.minhhop.core.domain.golf.Hole
import com.minhhop.core.domain.golf.RoundGolf
import com.minhhop.core.domain.golf.Tee
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle
import com.minhhop.easygolf.framework.bundle.PlayGolfBundle.TypeGame.*
import com.minhhop.easygolf.framework.common.Contains
import com.minhhop.easygolf.framework.models.firebase.DataScoreGolfFirebase
import com.minhhop.easygolf.framework.models.firebase.MemberEventFirebase
import com.minhhop.easygolf.framework.models.firebase.MemberFirebase
import com.minhhop.easygolf.presentation.golf.GoogleMapsHelper
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File

class PlayGolfViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    var mPlayGolfBundle: PlayGolfBundle? = null
    val unitGolfLiveData = MutableLiveData<GoogleMapsHelper.UnitGolf>()
    fun setUnit(unit: GoogleMapsHelper.UnitGolf) {
        unitGolfLiveData.postValue(unit)
    }

    fun getUnit() = unitGolfLiveData.value ?: GoogleMapsHelper.UnitGolf.YARD

    fun typeGame() = mPlayGolfBundle?.typeGame

    val roundGolfLiveData = MutableLiveData<RoundGolf>()
    fun startRound(roundId: String?, courseId: String?, latitude: Double, longitude: Double, teeType: String) {
        /**
         * reset time delay to show alert finish round
         * */
        interactors.deleteEasyGolfAppDataByRoundId(roundId)

        when (mPlayGolfBundle?.typeGame) {
            NEW_GAME -> {

                courseId?.let { courseIdSafe ->
                    interactors.getRoundGolfByCourseInLocal(courseIdSafe) {
                        mCommonErrorLive.postValue(it)
                    }?.let {
                        it.id?.let { roundId ->
                            fetchToSaveRound(roundId, courseIdSafe, latitude, latitude, teeType)
                        }
                        roundGolfLiveData.postValue(it)
                    } ?: fetchRoundGolf(courseIdSafe, latitude, longitude, teeType)
                }
                        ?: mCommonErrorLive.postValue(ErrorResponse.commonError("course id must be not null"))

                if (roundId != null) {
                    interactors.changeTeeTypeForRoundGolfInLocal(roundId, teeType, {}, {})
                }
            }
            BATTLE_GAME -> {
                if (roundId != null) {
                    interactors.getRoundGolfByIdInLocal(roundId)?.let { roundGolf ->
                        roundGolfLiveData.postValue(roundGolf)
                    } ?: getRound(roundId, courseId, latitude, longitude, teeType)
                    // update tee type
                    interactors.changeTeeTypeInTheBattle(teeType, roundId, {}, {})
                    initFirebaseDatabase(roundId)
                }
            }
            EXPLORE, UNKNOWN, null -> {
                courseId?.let { courseIdSafe ->
                    exploreCourse(courseIdSafe, latitude, longitude, teeType)
                }
                        ?: mCommonErrorLive.postValue(ErrorResponse.commonError("course id must be not null"))
            }
        }
    }

    fun fetchRoundGolf(roundId: String, onResult: (RoundGolf) -> Unit) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                onResult(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    private fun fetchRoundGolf(courseId: String, latitude: Double, longitude: Double, teeType: String) {
        mScope.launch {
            interactors.startRoundGolf(courseId, latitude, longitude, teeType, {
                roundGolfLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    private fun getRound(roundId: String, courseId: String?, latitude: Double, longitude: Double, teeType: String) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                interactors.insertOrUpdateRoundGolfInLocal(it, teeType, {}, { error ->
                    mCommonErrorLive.postValue(error)
                })
                roundGolfLiveData.postValue(it)
            }, {
                if (it.status == 404 && courseId != null) {
                    fetchRoundGolf(courseId, latitude, longitude, teeType)
                } else {
                    mCommonErrorLive.postValue(it)
                }
            })
        }
    }

    private fun fetchToSaveRound(roundId: String, courseId: String, latitude: Double, longitude: Double, teeType: String) {
        mScope.launch {
            interactors.fetchRoundGolf(roundId, {
                interactors.insertOrUpdateRoundGolfInLocal(it, teeType, {}, { error ->
                    mCommonErrorLive.postValue(error)
                })
            }, {
                if (it.status == 404) {
                    fetchRoundGolf(courseId, latitude, longitude, teeType)
                } else {
                    mCommonErrorLive.postValue(it)
                }
            })
        }
    }

    private fun exploreCourse(courseId: String, latitude: Double, longitude: Double, teeType: String) {
        mScope.launch {
            interactors.startExploreCourse(courseId, latitude, longitude, teeType, {
                roundGolfLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun getProfileUser() = interactors.getProfileInLocal { mCommonErrorLive.postValue(it) }

    val updateScoreLiveData = MutableLiveData<HashMap<String, DataScoreGolf?>?>()
    fun insertOrUpdateDataScoreGolf(roundId: String, hole: Hole, data: DataScoreGolf, userTarget: User? = null) {
        mScope.launch {
            when (mPlayGolfBundle?.typeGame) {
                NEW_GAME -> {
                    /**
                     * save game in local
                     * */
                    interactors.insertOrUpdateScoreHole(roundId, hole.hole_id, data, {
                        getProfileUser()?.let { user ->
                            val hashMap = updateScoreLiveData.value ?: HashMap()
                            hashMap.clear()
                            hashMap[user.id] = data
                            updateScoreLiveData.postValue(
                                    hashMap
                            )
                        }
                    }, {
                        mCommonErrorLive.postValue(it)
                    })
                }
                BATTLE_GAME -> {
                    userTarget?.let { user ->
                        interactors.postScoreInBattleToFirebase(roundId, hole.number.toString(), user, data, {},
                                {
                                    mCommonErrorLive.postValue(it)
                                }
                        )
                    } ?: getProfileUser()?.let { user ->
                        interactors.postScoreInBattleToFirebase(roundId, hole.number.toString(), user, data, {},
                                {
                                    mCommonErrorLive.postValue(it)
                                }
                        )
                    }
                }
                EXPLORE, UNKNOWN, null -> {
                }
            }
        }

    }

    val changeTeeLiveData = MutableLiveData<Tee>()
    fun onChangeTee(tee: Tee) {
        mPlayGolfBundle?.teeType = tee.type
        changeTeeLiveData.postValue(tee)

        /**
         * update tee type
         * */
        roundGolfLiveData.value?.id?.let { roundGolfId ->
            when (mPlayGolfBundle?.typeGame) {
                EXPLORE, UNKNOWN, null -> {
                }
                NEW_GAME -> {
                    interactors.changeTeeTypeForRoundGolfInLocal(roundGolfId, tee.type, {}, {})
                }

                BATTLE_GAME -> {
                    interactors.changeTeeTypeInTheBattle(tee.type, roundGolfId, {}, {})
                }
            }
        }

    }

    fun getScoreGolfAtHoleInLocal(roundId: String, holeId: String) {
        getProfileUser()?.let { user ->
            val hashMap = updateScoreLiveData.value ?: HashMap()
            hashMap.clear()
            hashMap[user.id] = interactors.getScoreAtHole(roundId, holeId)

            updateScoreLiveData.value = (
                    hashMap
                    )
        }
    }

    val mCurrentHoleLiveData = MutableLiveData<Hole>()
    fun setHole(hole: Hole) {
        roundGolfLiveData.value?.id?.let { roundGolfId ->
            /**
             * set math = hole in roundGolf local
             * */
            interactors.updateMathForRoundGolfInLocal(roundGolfId, hole, {}, {})

            when (mPlayGolfBundle?.typeGame) {
                NEW_GAME -> {
                    /**
                     * get data score from local
                     * */
                    getScoreGolfAtHoleInLocal(roundGolfId, hole.hole_id)
                }
                BATTLE_GAME -> {
                    /**
                     * get data score from firebase
                     * */
                    observableDataForHole(roundGolfId, hole.number.toString())
                }
                EXPLORE, UNKNOWN, null -> {
                }
            }
        }
        mCurrentHoleLiveData.value = hole
    }

    fun deleteDataRound() {
        roundGolfLiveData.value?.id?.let { roundGolfId ->
            interactors.deleteAllDataScoreGolfOfRoundInLocal(roundGolfId, {

            }, {
                mCommonErrorLive.postValue(it)
            })
        }

    }

    /**
     * ===>Zone for firebase
     * */
    private var mFirebaseRefMembers: DatabaseReference? = null
    private var mFirebaseRefHoles: DatabaseReference? = null

    val mDisableFromRound = MutableLiveData<Boolean>()
    var mMemberFirebaseLiveData = MutableLiveData<MemberEventFirebase>()
    var mCacheMembers = HashMap<String, String>()
    private var mOnValueMembersChange = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) {
            mCommonErrorLive.postValue(ErrorResponse.commonError(error.message))
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            try {
                snapshot.getValue(MemberFirebase::class.java)?.let { member ->
                    getProfileUser()?.let { currentUser ->
                        if (member.id != currentUser.id && !mCacheMembers.containsKey(member.id)) {
                            getProfileUserFromFirebase(member.id)
                        }
                    } ?: getProfileUserFromFirebase(member.id)
                }
            } catch (error: Exception) {
                mCommonErrorLive.postValue(ErrorResponse.commonError(error.message))
            }

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            try {
                snapshot.getValue(MemberFirebase::class.java)?.let { member ->
                    getProfileUser()?.let { currentUser ->
                        getProfileUserFromFirebase(member.id, true)
                        if (currentUser.id == member.id) {
                            /***
                             * current user was removed from this round
                             * */
                            roundGolfLiveData.value?.id?.let { roundGolfId ->
                                interactors.isRoundExitInFirebase(roundGolfId, {
                                    mDisableFromRound.postValue(it)
                                }, {
                                    mCommonErrorLive.postValue(it)
                                })

                            }

                        }
                    }
                }

            } catch (error: Exception) {
                mCommonErrorLive.postValue(ErrorResponse.commonError(error.message))
            }
        }
    }

    private fun getProfileUserFromFirebase(userId: String, isRemove: Boolean = false) {
        interactors.getProfileUserFromFirebase(userId, { user ->
            user?.let { userClear ->
                mCacheMembers[userClear.id] = userClear.id
                mMemberFirebaseLiveData.value = (MemberEventFirebase(isRemove, userClear))
            }
        }, {
            mCommonErrorLive.postValue(it)
        })
    }

    private var mOnValueHolesChange = object : ValueEventListener {
        override fun onCancelled(error: DatabaseError) {
            mCommonErrorLive.postValue(ErrorResponse.commonError(error.message))
        }

        override fun onDataChange(snapshot: DataSnapshot) {
            val hashMap = updateScoreLiveData.value ?: HashMap()
            hashMap.clear()
            for (element in snapshot.children) {
                try {
                    /**
                     * if cant got it, just ignore
                     * */
                    element.key?.let { id ->
                        hashMap[id] = element.getValue(DataScoreGolfFirebase::class.java)?.toDataScoreGolf()
                    }
                } catch (e: Exception) {
                    continue
                }

            }

            updateScoreLiveData.postValue(hashMap)
        }
    }

    private fun initFirebaseDatabase(roundId: String) {
        if (mFirebaseRefMembers == null) {
            mFirebaseRefMembers = FirebaseDatabase.getInstance().getReference("rounds")
                    .child(roundId)
                    .child("members")

            mFirebaseRefMembers?.addChildEventListener(mOnValueMembersChange)
            mFirebaseRefHoles?.addValueEventListener(mOnValueHolesChange)
        }
    }

    private fun observableDataForHole(roundId: String, numberHole: String) {
        mFirebaseRefHoles?.removeEventListener(mOnValueHolesChange)
        mFirebaseRefHoles = FirebaseDatabase.getInstance().getReference("rounds")
                .child(roundId)
                .child("holes").child(numberHole)
        mFirebaseRefHoles?.addValueEventListener(mOnValueHolesChange)
    }

    fun onStateFirebaseDatabaseResume() {
        mCacheMembers.clear()
        mFirebaseRefMembers?.addChildEventListener(mOnValueMembersChange)
        mFirebaseRefHoles?.addValueEventListener(mOnValueHolesChange)
    }

    fun onStateFirebaseDatabasePause() {
        mFirebaseRefMembers?.removeEventListener(mOnValueMembersChange)
        mFirebaseRefHoles?.removeEventListener(mOnValueHolesChange)
    }

    fun getBattleRoundInFirebase(roundId: String, onResult: (BattleRound?) -> Unit) {
        getProfileUser()?.let { user ->
            interactors.getBattleRoundInFirebase(roundId, user.id, {
                onResult(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun addMembersToBattle(roundId: String, courseId: String, userList: List<User>) {
        interactors.addMembersToBattleInFirebase(roundId, courseId, userList, {}, {})
        sendPushNotificationToFriend(roundId, userList)
    }

    private fun sendPushNotificationToFriend(roundId: String, members: List<User>) {
        mScope.launch {
            interactors.pushNotificationToMemberInBattle(roundId, members, {}, {})
        }
    }

    fun createBattleGame(roundId: String, courseId: String, friends: List<User>?) {
        mPlayGolfBundle?.typeGame = BATTLE_GAME
        /**
         * delete all time delay to show alert finish round
         * */
        interactors.updateEasyGolfAppDataInLocal(EasyGolfApp(roundId, 0L, true))

        initFirebaseDatabase(roundId)
        /**
         * get data score from firebase
         * */
        mCurrentHoleLiveData.value?.let { hole ->
            observableDataForHole(roundId, hole.number.toString())
        }

        mScope.launch {
            interactors.createBattleGameInFirebase(roundId, courseId, friends, {
                friends?.let { friendsExit ->
                    sendPushNotificationToFriend(roundId, friendsExit)
                }
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun removeMembersFromBattle(roundId: String, user: User) {
        interactors.removeMemberFromBattleInFirebase(roundId, user, {}, {})
    }

    fun getScoreForUserInBattle(roundId: String, hole: Hole, user: User, onResult: (DataScoreGolf?) -> Unit) {
        interactors.getScoreUserInBattleFirebase(roundId, hole.number.toString(), user, {
            onResult(it)
        }, {
            mCommonErrorLive.postValue(it)
        })
    }

    fun takeScreenShoot(snapshotMap: Bitmap, activity: PlayGolfActivity, snapshot: (File) -> Unit) {
        activity.getViewRoot()?.let { viewRoot ->
            getBitmapFromView(viewRoot)?.let { snapshotView ->
                val bmOverlay = Bitmap.createBitmap(snapshotView.width, snapshotView.height, snapshotView.config)
                val canvas = Canvas(bmOverlay)
                canvas.drawBitmap(snapshotMap, Matrix(), null)
                canvas.drawBitmap(snapshotView, 0f, 0f, null)

                interactors.createImageFile(Contains.fileDirGolfPhoto(activity))?.let { file ->
                    val bytes = ByteArrayOutputStream()
                    bmOverlay.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    interactors.writeBytesToFile(bytes, file) {
                        mCommonErrorLive.postValue(it)
                    }
                    snapshot(file)
                }
            }
        }
    }

    private fun getBitmapFromView(viewRoot: View): Bitmap? {
        val snapshotView = Bitmap.createBitmap(viewRoot.width, viewRoot.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(snapshotView)
        viewRoot.draw(canvas)
        return snapshotView
    }
}