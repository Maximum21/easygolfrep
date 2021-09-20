package com.minhhop.easygolf.framework.implementation

import android.util.Log
import com.google.gson.Gson
import com.minhhop.core.data.datasource.GolfDataSource
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.ResultNotification
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.club.FollowersResponse
import com.minhhop.core.domain.course.RequestCourse
import com.minhhop.core.domain.course.RequestedCourse
import com.minhhop.core.domain.feed.Comment
import com.minhhop.core.domain.feed.CommentResponse
import com.minhhop.core.domain.feed.NewsFeed
import com.minhhop.core.domain.feed.NewsFeedResponse
import com.minhhop.core.domain.golf.*
import com.minhhop.core.domain.notifications.GolfNotification
import com.minhhop.core.domain.notifications.NotificationPager
import com.minhhop.core.domain.profile.PeopleResponse
import com.minhhop.core.domain.profile.Round
import com.minhhop.core.domain.round.RoundHistoryPager
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.framework.common.AppUtils
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.models.request.AddReviewRequest
import com.minhhop.easygolf.framework.models.request.DataPushNotificationMembersRequest
import com.minhhop.easygolf.framework.models.request.StartRoundRequest
import com.minhhop.easygolf.framework.network.remote.GeneralService
import com.minhhop.easygolf.framework.network.remote.GolfService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class GolfImpl(private val golfService: GolfService, private val generalService: GeneralService, private val realmManager: RealmManager) : GolfDataSource {

    override suspend fun nearby(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.courseNearby(latitude, longitude, start, keyword).run({ coursePager ->
            result(coursePager)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun requestCourse(requestCourse: RequestCourse, result: (RequestedCourse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.requestCourse(requestCourse).run({ result ->
            result(result)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchSuggestedClubs(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.fetchSuggestedClubs(latitude, longitude, start, keyword).run({ coursePager ->
            result(coursePager)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchRecommendClub(latitude: Double, longitude: Double, start: Int, result: (ClubPager) -> Unit,
                                            errorResponse: (ErrorResponse) -> Unit, callback: (Any) -> Unit) {
        val callbackReCommend = golfService.fetchRecommendClubs(latitude, longitude, start)
        callbackReCommend.run({
            result(it)
        }, {
            errorResponse(it)
        })
        callback(callbackReCommend)
    }


    override suspend fun startRound(courseId: String, latitude: Double, longitude: Double, teeType: String,
                                    result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.startRound(courseId, StartRoundRequest(latitude, longitude, teeType)).run({ roundMatch ->
            result(roundMatch)
            insertOrUpdateRoundGolfInLocal(roundMatch, teeType, {}, {})
        }, {
            errorResponse(it)
        })
    }

    override suspend fun pushNotificationToMemberInBattle(roundId: String, members: List<User>,
                                                          result: (ResultNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit) {

        val listMemberId = ArrayList<String>()
        members.forEach {
            listMemberId.add(it.id)
        }
        generalService.pushNotificationToUserInviteBattle(roundId, DataPushNotificationMembersRequest(listMemberId)).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchRound(roundId: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getRound(roundId).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun getClubDetail(clubId: String, result: (Club) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getClubDetail(clubId).run({
            result(it)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun getPosts(type: String, start: Int,tag:Int, result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        when(tag){
            0->{
                golfService.getPosts(type, start).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            2->{
                golfService.getClubFeeds(type, start.toString()).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            else->{
                golfService.getUserPosts(type, start).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
        }

    }

    override suspend fun addComment(id: String, comment: String, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.addComment(id, comment).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun listComment(id: String, result: (CommentResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.listComment(id).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun getFriends(result: (ArrayList<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getFriends().run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchClubPhotos(clubId: String, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.fetchClubPhotos(clubId).run({
            result(it)
        }, {
            error(it)
        })
    }

    override suspend fun fetchClubReview(clubId: String, result: (List<Review>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.fetchClubReview(clubId).run({
            result(it)
        }, {
            error(it)
        })
    }

    override suspend fun addReviewForClub(clubId: String, rate: Int, content: String?, reviewId: String?, result: (Review) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        if (reviewId != null) {
            golfService.addReviewForClub(clubId, AddReviewRequest(rate, content), reviewId).run({
                result(it)
            }, {
                error(it)
            })
        } else {
            golfService.addReviewForClub(clubId, AddReviewRequest(rate, content)).run({
                result(it)
            }, {
                error(it)
            })
        }
    }

    override suspend fun deletePhotoFromClub(clubId: String, photoId: String, position: Int, result: (Int) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.deleteClubPhoto(clubId, photoId).runNoContent({
            result(position)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun deletePost(id: String, tag: Int, type: String, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        when (tag) {
            0 -> {
                golfService.deletePost(id).runNoContent({
                    result("deleted")
                    Log.e("WOW", "oke")
                }, {
                    errorResponse(it)
                })
            }
            1 -> {
                golfService.deleteImage(id, type).runNoContent({
                    result("deleted")
                    Log.e("WOW", "oke")
                }, {
                    errorResponse(it)
                })
            }
        }

    }

    override suspend fun uploadPhotoForClub(clubId: String, files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val fileUploadRequest = ArrayList<MultipartBody.Part>()
        files.forEach { file ->
            val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            fileUploadRequest.add(MultipartBody.Part.createFormData("file", file.name, requestFile))
        }

        golfService.uploadPhotoForClub(clubId, fileUploadRequest).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun uploadHistoryPhotoForClub(files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val fileUploadRequest = ArrayList<MultipartBody.Part>()
        files.forEach { file ->
            val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
            fileUploadRequest.add(MultipartBody.Part.createFormData("pass_score", file.name, requestFile))
        }

        golfService.uploadHistoryPhotoForClub(fileUploadRequest).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchUserProfile(id: String, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        Log.e("testing", "$id == ${Gson().toJson(result)}")
        golfService.fetchUserProfile(id).run({
            result(it)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun exploreCourse(courseId: String, latitude: Double, longitude: Double, typeTee: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.exploreCourse(courseId, latitude, longitude, typeTee)
                .run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
    }

    override suspend fun createPost(postDescription: String, files: ArrayList<String>, club: Club?, friends: List<User>, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val friendsList = AppUtils.getFriendsList("friends", friends)
        val courses = if (club != null) {
            AppUtils.getDescription("club", club.id)
        } else {
            null
        }
        if (files.isNotEmpty()) {
            golfService.createPostImage(AppUtils.getDescription("content", postDescription)
                    , courses
                    , friendsList
                    , AppUtils.getFiles("files", files)).run({ response ->
                result(response)
            }, {
                errorResponse(it)
            })
        } else {
            golfService.createPost(AppUtils.getDescription("content", postDescription)
                    , courses
                    , friendsList).run({ response ->
                result(response)
            }, {
                errorResponse(it)
            })
        }
    }

    override suspend fun updatePost(postDescription: String, files: ArrayList<String>, club: Club?, friends: List<User>, id: String, tag: Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        Log.e("chekcupadte", "$postDescription ---- ${files.size} --- $club ---- $friends ---- $id")
        when (tag) {
            2 -> {
                val data = HashMap<String, Any>()
                data["content"] = postDescription
                friends?.let {
                    data["friends"] = it[0].id
                }
                club?.let {
                    data["club"] = it.id
                }
                golfService.sharePost(id, data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            else -> {
                val friendsList = AppUtils.getFriendsList("friends", friends)
                val cources = if (club != null) {
                    AppUtils.getDescription("club", club.id)
                } else {
                    null
                }
                if (files.isNotEmpty()) {
                    Log.e("chekcupadte", "-----------1")
                    golfService.updatePostImage(AppUtils.getDescription("content", postDescription)
                            , cources
                            , id
                            , friendsList
                            , AppUtils.getFiles("files", files)).run({ response ->
                        result(response)
                    }, {
                        errorResponse(it)
                    })
                } else {
                    Log.e("chekcupadte", "-----------2")
                    golfService.updatePost(AppUtils.getDescription("content", postDescription)
                            , cources
                            , id
                            , friendsList).run({ response ->
                        result(response)
                    }, {
                        errorResponse(it)
                    })
                }
            }
        }
    }

    override suspend fun deleteComment(postId: String, commentId: String, type: Int, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.deleteComment(postId, commentId).runNoContent({
            result("deleted")
        }, {
            errorResponse(it)
        })

    }

    override suspend fun editComment(postId: String, commentId: String, comment: String, type: Int, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val data = HashMap<String, Any>()
        data["comment"] = comment
        golfService.editComment(postId, commentId, data).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })

    }

    override suspend fun likePost(id: String, type: Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.likePost(id).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun requestAction(id: String, message: String, tag: Int, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        when (tag) {
            //Add Friend
            0 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                if (message.isNotEmpty()) {
                    data["message"] = message
                }
                golfService.addFriend(data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            //Follow
            1 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                golfService.requestAction("follow", data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            //UnFollow
            2 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                golfService.requestAction("unfollow", data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            //Accept Request
            3 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                golfService.requestActionAcceptOrDecline("accept", data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            //decline Request
            4 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                golfService.requestActionAcceptOrDecline("decline", data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            //unfriend Request
            5 -> {
                val data = HashMap<String, Any>()
                data["user_id"] = id
                golfService.requestAction("unfriend", data).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
        }
    }

    override suspend fun getRules(result: (List<GolfRule>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getGolfRules().run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun getRoundHistory(start: Int,id:String, result: (RoundHistoryPager) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        if (id.isNullOrEmpty()) {
            golfService.getRoundHistory(start).run({ response ->
                result(response)
            }, {
                errorResponse(it)
            })
        }else{
            golfService.getUserRoundHistory(id,start,"10").run({ response ->
                result(response)
            }, {
                errorResponse(it)
            })
        }
    }

    override suspend fun getNotifications(start: Int, text: String, result: (NotificationPager) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getNotifications(start, text).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun notifyNotification(id: String, tag: Int, result: (GolfNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        when (tag) {
            0 -> {
                golfService.readNotifications(id).run({ response ->
                    result(response)
                }, {
                    errorResponse(it)
                })
            }
            else -> {
                golfService.deleteNotifications(id).run({ response ->
                    result(GolfNotification("", false, "", "", null, "", "","", "", true))
                }, {
                    errorResponse(it)
                })
            }
        }
    }

    override suspend fun sendFeedback(feedback: Feedback, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.sendFeedback(feedback).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun passScoreGolf(passScore: PassScore, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.passScoreGolf(passScore).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun uploadScorecardGolf(file: File, courseId: String, teeType: String, date: Long, friends: List<User>?,
                                             result: (Any) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        val listFriend = ArrayList<String>()
        friends?.map {
            listFriend.add(it.id)
        }

        val requestFile = file.asRequestBody("image/jpg".toMediaTypeOrNull())
        val dataRequestBuilder = MultipartBody.Builder()
                .addFormDataPart("score_card", file.name, requestFile)
                .addFormDataPart("course_id", courseId)
                .addFormDataPart("tee", teeType)
                .addFormDataPart("date", date.toString())

        if (listFriend.isNotEmpty()) {
            dataRequestBuilder.addFormDataPart("friends", Gson().toJson(listFriend))
        }
        golfService.uploadPhotoScorecardGolf(dataRequestBuilder.build()).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun onCompleteRound(roundId: String, roundHost: RoundHost, result: (Round) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.completeRound(roundId, roundHost).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun onFollowClub(clubId: String, result: (FollowResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.onFollowClub(clubId).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun onFollowersClub(clubId: String, start: Int, result: (FollowersResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getClubDetailFollowers(clubId,"0").run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun fetchClubFeeds(clubId: String, start: Int, result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.getClubFeeds(clubId,start.toString()).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override fun updateMathForRoundGolfInLocal(roundId: String, hole: Hole, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.updateMathForRoundGolfInLocal(roundId, hole, onComplete, {
            errorResponse(ErrorResponse.commonError(it))
        })
    }

    override fun changeTeeTypeForRoundGolfInLocal(roundId: String, teeType: String?, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.changeTeeTypeForRoundGolfInLocal(roundId, teeType, onComplete, {
            errorResponse(ErrorResponse.commonError(it))
        })
    }

    override fun getRoundGolfExitInLocal(): RoundGolf? = realmManager.getRoundGolfExit()
    override suspend fun friendApproveRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.friendApproveRound(roundId).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override suspend fun friendNotAcceptRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        golfService.friendNotAcceptRound(roundId).run({ response ->
            result(response)
        }, {
            errorResponse(it)
        })
    }

    override fun getRoundGolfByCourseInLocal(courseId: String): RoundGolf? = realmManager.getRoundGolfByCourse(courseId)
    override fun getRoundGolfByIdInLocal(roundId: String): RoundGolf? = realmManager.getRoundGolfById(roundId)

    override fun insertOrUpdateRoundGolfInLocal(roundGolf: RoundGolf, teeType: String?, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.insertOrUpdateRoundGolf(roundGolf, teeType, {
            onComplete()
        }, {
            errorResponse(ErrorResponse.commonError(it))
        })
    }

    override fun deleteRoundGolfInLocal(roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.deleteRoundGolf({
            onComplete()
        }, {
            errorResponse(ErrorResponse.commonError(it))
        })
    }

    override fun deleteAllDataScoreGolfOfRoundInLocal(roundId: String, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.deleteDataScoreGolfRound(onComplete) {
            errorResponse(ErrorResponse.commonError(it))
        }

        realmManager.deleteEasyGolfAppDataByRoundId(roundId)
    }

    override fun insertOrUpdateDataScoreGolf(roundId: String, holeId: String, data: DataScoreGolf, onComplete: () -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.insertOrUpdateDataScoreGolf(roundId, holeId, data, onComplete) {
            errorResponse(ErrorResponse.commonError(it))
        }
    }

    override fun getDataScoreGolfOfRound(roundId: String, errorResponse: (ErrorResponse) -> Unit): List<DataScoreGolf>? {
        return realmManager.getAllDataScoreGolfByRound(roundId) {
            errorResponse(ErrorResponse.commonError(it))
        }
    }

    override fun getDataScoreGolf(roundId: String, holeId: String): DataScoreGolf? = realmManager.getDataScoreGolf(roundId, holeId)

}