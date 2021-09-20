package com.minhhop.easygolf.presentation.club.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.minhhop.core.domain.User
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.core.domain.feed.NewsFeedResponse
import com.minhhop.core.domain.firebase.BattleRound
import com.minhhop.core.domain.golf.*
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.golf.TeeUtils
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream

class EasyGolfClubDetailViewModel(private val interactors: Interactors) : EasyGolfViewModel() {

    val clubDataLive = MutableLiveData<Club>()
    fun getClubDetail(clubId: String) {
        mScope.launch {
            interactors.getClubDetail(clubId, {
                clubDataLive.postValue(it)
                if (!it.courses.isNullOrEmpty() && currentCourseLiveData.hasActiveObservers()) {
                    setCourse(it.courses?.first())
                }
                /**
                 * update action follow
                 * */
                followLiveData.postValue(it.is_following ?: false)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
        fetchClubPhotos(clubId)
        fetchClubReviews(clubId)
        getClubFollowers(clubId)
        getClubFeeds(clubId)
    }

    val currentCourseLiveData = MutableLiveData<Course>()
    fun setCourse(course: Course?) {
        if (currentCourseLiveData.value?.id != course?.id) {
            course?.let {
                currentCourseLiveData.postValue(it)
            }
        }
    }

    fun getRoundGolfInLocal():RoundGolf?{
        return currentCourseLiveData.value?.let { course ->
            Log.e("WOW","course id: ${course.id}")
            interactors.getRoundGolfByCourseInLocal(course.id) {
                mCommonErrorLive.postValue(it)
            }
        }
    }
    fun getListScoreCard(): List<Scorecard>? {
        val courses = clubDataLive.value?.courses
        return if (!courses.isNullOrEmpty()) {
            courses.first().scorecard
        } else null
    }

    val currentScorecardLiveData = MutableLiveData<Scorecard>()

    fun setCurrentScorecardWithTeeType(teeType: String?) {
        currentCourseLiveData.value?.let { course ->
            if (!course.scorecard.isNullOrEmpty() && currentScorecardLiveData.hasActiveObservers()) {
                setCurrentScorecard(
                        TeeUtils.getTeeByType(
                                course.scorecard?.map { scorecard -> scorecard },teeType
                        )
                )
            }
        }
    }

    fun setCurrentScorecard(scorecard: Scorecard?) {
        scorecard?.let {
            currentScorecardLiveData.postValue(it)
        }
    }

    val clubPhotosLiveData = MutableLiveData<List<ClubPhoto>>()
    private fun fetchClubPhotos(clubId: String) {
        mScope.launch {
            interactors.fetchClubPhotos(clubId, {
                clubPhotosLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    val listReviewsLiveData = MutableLiveData<List<Review>>()
    private fun fetchClubReviews(clubId: String) {
        mScope.launch {
            interactors.fetchClubReview(clubId, {
                listReviewsLiveData.postValue(it)
                getCurrentReview(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    var mIndexReview: Int = -1
    private fun getCurrentReview(data: List<Review>) {
        val currentUser = getProfileUserInLocal()
        mIndexReview = -1
        data.forEachIndexed { index, review ->
            if (currentUser?.id == review.user.id) {
                mIndexReview = index
                currentReviewLiveData.postValue(review)
                return@forEachIndexed
            }
        }
    }

    val currentReviewLiveData = MutableLiveData<Review>()

    fun getProfileUserInLocal() = interactors.getProfileInLocal { mCommonErrorLive.postValue(it) }

    fun addReviewForClub(clubId: String, rate: Int, content: String?, reviewId: String?) {
        mScope.launch {
            interactors.addReviewForClub(clubId, rate, content, reviewId, {
                if (mIndexReview < 0) {
                    mIndexReview = 0
                }
                currentReviewLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    val positionDeleteOverviewLiveData = MutableLiveData<Int>()
    val positionDeleteLiveData = MutableLiveData<Int>()
    fun deleteClubPhoto(clubId: String, photoId: String, position: Int, fromOverview: Boolean = false) {
        mScope.launch {
            interactors.deletePhotoFromCurrentClub(clubId, photoId, position, {
                positionDeleteLiveData.postValue(if (fromOverview) it - 1 else it)
                positionDeleteOverviewLiveData.postValue(if (fromOverview) it else it + 1)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun createImageFileHolder(externalFilesDir: File) = interactors.createImageFile(externalFilesDir)
    fun createImageFileHolder(externalFilesDir: File, inputStream: InputStream) = interactors.createImageFileWithInputStream(externalFilesDir, inputStream)
    fun deleteFileImage(path: String) = interactors.deleteFileInLocal(path)
    fun resizeFileImage(path: String) = interactors.resizeImageInLocal(path, AppUtils.MAX_QUALITY_IMAGE_SCALE) { mCommonErrorLive.postValue(it) }

    val newPhotoInsertLiveData = MutableLiveData<List<ClubPhoto>>()
    fun uploadPhotoFileForClub(clubId: String, files: List<File>) {
        mScope.launch {
            interactors.uploadPhotoForClub(clubId, files, {
                newPhotoInsertLiveData.postValue(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    val uploadPhotoScorecardCallbackLiveData = MutableLiveData<Any>()
    fun uploadScorecardGolf(file: File, friends: List<User>?) {
        currentCourseLiveData.value?.let { course ->
            currentScorecardLiveData.value?.let { scorecard ->
                mScope.launch {
                    interactors.uploadPhotoScorecardGolf(file, course.id, scorecard.type, System.currentTimeMillis(), friends, {
                        uploadPhotoScorecardCallbackLiveData.postValue(it)
                    }, {
                        mCommonErrorLive.postValue(it)
                    })
                }
            }
        }

    }

    var mBattleRound: BattleRound? = null
    fun getBattleRoundWithCourseInFirebase(onResult: (BattleRound?) -> Unit) {
        currentCourseLiveData.value?.let { course ->
            interactors.getBattleRoundWithCourseInFirebase(course.id, null, {
                mBattleRound = it
                onResult(it)
            }, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    fun addMembersToBattle(userList: List<User>) {
        mBattleRound?.round_id?.let { roundId ->
            sendPushNotificationToFriend(roundId, userList)
            mBattleRound?.course_id?.let { courseId ->
                interactors.addMembersToBattleInFirebase(roundId, courseId, userList, {}, {})
            }
        }
    }

    private fun sendPushNotificationToFriend(roundId: String, members: List<User>) {
        mScope.launch {
            interactors.pushNotificationToMemberInBattle(roundId, members, {}, {})
        }
    }

    fun removeMembersFromBattle(user: User? = null) {
        val targetUser = user ?: getProfileUserInLocal()
        targetUser?.let { userRemove ->
            mBattleRound?.round_id?.let { roundId ->
                interactors.removeMemberFromBattleInFirebase(roundId, userRemove, {
                    currentCourseLiveData.value?.let { course ->
                        currentCourseLiveData.postValue(course)
                    }
                }, {

                })
            }
        }
    }

    fun getMemberInBattle(roundId: String, onResult: (User) -> Unit) {
        interactors.fetchMemberInBattleFirebase(roundId, {
            it?.let { user ->
                if (user.id != getProfileUserInLocal()?.id) {
                    onResult(user)
                }
            }
        }) {
            mCommonErrorLive.postValue(it)
        }
    }

    fun updateStatusPendingInBattleForUser(roundId: String) {
        getProfileUserInLocal()?.let { user ->
            interactors.updateStatusPendingInBattleForUser(false, roundId, user, {}, {
                mCommonErrorLive.postValue(it)
            })
        }
    }

    val followLiveData = MutableLiveData<Boolean>()
    fun onFollowOrUnFollowClub() {
        clubDataLive.value?.let {
            mScope.launch {
                interactors.onFollowForClub(it.id, {
                    followLiveData.value?.let { isFollow ->
                        followLiveData.postValue(!isFollow)
                    }
                }, {
                    mCommonErrorLive.postValue(it)
                })
            }
        }
    }

    val followersLiveData = MutableLiveData<FollowersResponse>()
    fun getClubFollowers(id: String, start:Int=0){
        mScope.launch {
            interactors.onFollowForClub.invoke(id,start,{
                followersLiveData.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
    val clubFeeds = MutableLiveData<NewsFeedResponse>()
    fun getClubFeeds(id: String, start:Int=0){
        mScope.launch {
            interactors.onFollowForClub.invoke(id,start,0,{
                clubFeeds.postValue(it)
            },{
                mCommonErrorLive.postValue(it)
            })
        }
    }
}