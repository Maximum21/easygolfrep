package com.minhhop.core.data.repository

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
import java.io.File

class GolfRepository(private val golfDataSource: GolfDataSource) {
    suspend fun nearby(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.nearby(latitude, longitude, start, keyword, result, errorResponse)
    suspend fun requestCourse(requestedCourse: RequestCourse, result: (RequestedCourse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.requestCourse(requestedCourse, result, errorResponse)

    suspend fun fetchSuggestedClubs(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.fetchSuggestedClubs(latitude, longitude, start, keyword, result, errorResponse)

    suspend fun fetchRecommendClub(latitude: Double, longitude:Double,  start: Int, result: (ClubPager) -> Unit,
                                   errorResponse: (ErrorResponse) -> Unit,callback: (Any)->Unit)
            = golfDataSource.fetchRecommendClub(latitude, longitude, start, result, errorResponse, callback)

    suspend fun startRound(courseId: String, latitude: Double, longitude:Double, teeType:String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.startRound(courseId, latitude, longitude, teeType, result, errorResponse)
    suspend fun pushNotificationToMemberInBattle(roundId: String, members: List<User>,
                                                   result: (ResultNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.pushNotificationToMemberInBattle(roundId, members, result, errorResponse)
    suspend fun fetchRound(roundId: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.fetchRound(roundId, result, errorResponse)

    suspend fun getClubDetail(clubId: String, result: (Club) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.getClubDetail(clubId, result, errorResponse)

    suspend fun getPosts(type: String,start:Int,tag:Int,result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
        = golfDataSource.getPosts(type,start,tag,result, errorResponse)

    suspend fun addComment(id:String, comment:String, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.addComment(id, comment, result, errorResponse)

    suspend fun likePost(id: String, type:Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.likePost(id,type,result, errorResponse)

    suspend fun listComment(id:String, result: (CommentResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.listComment(id, result, errorResponse)
    suspend fun deletePost(id:String,tag:Int,type:String ,result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.deletePost(id,tag,type, result, errorResponse)

    suspend fun getFriends(result: (ArrayList<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.getFriends( result, errorResponse)

    suspend fun createPost(postDescription: String, files: ArrayList<String>, club: Club?, friends:List<User>,result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.createPost(postDescription,files,club,friends, result, errorResponse)
    suspend fun updatePost(postDescription: String, files: ArrayList<String>, club: Club?, friends:List<User>,id:String,tag:Int ,result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.updatePost(postDescription,files,club,friends,id,tag, result, errorResponse)

    suspend fun fetchClubPhotos(clubId: String, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit) = golfDataSource.fetchClubPhotos(clubId, result, errorResponse)
    suspend fun deleteComment(postId: String, commentId: String, type:Int, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.deleteComment(postId,commentId,type, result, errorResponse)

    suspend fun editComment(postId: String, commentId: String, comment: String, type:Int, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.editComment(postId,commentId,comment,type, result, errorResponse)

    suspend fun fetchClubReview(clubId: String, result: (List<Review>) -> Unit, errorResponse: (ErrorResponse) -> Unit) = golfDataSource.fetchClubReview(clubId, result, errorResponse)

    suspend fun addReviewForClub(clubId: String,rate:Int,content:String?, reviewId: String? = null,result: (Review) -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.addReviewForClub(clubId,rate, content, reviewId, result, errorResponse)

    suspend fun deletePhotoFromClub(clubId: String,photoId:String,position:Int,result: (Int) -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.deletePhotoFromClub(clubId, photoId, position, result, errorResponse)
    suspend fun uploadPhotoForClub(clubId: String,  files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.uploadPhotoForClub(clubId, files, result, errorResponse)
    suspend fun uploadHistoryPhotoForClub(files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.uploadHistoryPhotoForClub(files, result, errorResponse)
    suspend fun exploreCourse(courseId: String, latitude: Double, longitude: Double, typeTee:String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.exploreCourse(courseId, latitude, longitude, typeTee, result, errorResponse)

    suspend fun getUserProfile(id: String, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.fetchUserProfile(id, result, errorResponse)
    suspend fun requestAction(id: String, message:String, tag:Int, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.requestAction(id, message, tag, result, errorResponse)

    fun getRoundGolfByCourseInLocal(courseId: String) : RoundGolf? = golfDataSource.getRoundGolfByCourseInLocal(courseId)
    fun getRoundGolfByIdInLocal(roundId: String) : RoundGolf? = golfDataSource.getRoundGolfByIdInLocal(roundId)
    fun insertOrUpdateRoundGolfInLocal(roundGolf:RoundGolf,teeType: String?,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.insertOrUpdateRoundGolfInLocal(roundGolf, teeType, onComplete, errorResponse)
    fun deleteRoundGolfInLocal(roundId:String,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.deleteRoundGolfInLocal(roundId, onComplete, errorResponse)
    fun deleteAllDataScoreGolfOfRoundInLocal(roundId:String,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.deleteAllDataScoreGolfOfRoundInLocal(roundId, onComplete, errorResponse)
    fun insertOrUpdateDataScoreGolf(roundId: String,holeId:String,data: DataScoreGolf,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.insertOrUpdateDataScoreGolf(roundId, holeId,data, onComplete, errorResponse)
    fun getDataScoreGolfOfRound(roundId: String, errorResponse: (ErrorResponse) -> Unit): List<DataScoreGolf>?
            = golfDataSource.getDataScoreGolfOfRound(roundId, errorResponse)
    fun getDataScoreGolf(roundId: String,holeId: String) : DataScoreGolf? = golfDataSource.getDataScoreGolf(roundId, holeId)
    suspend fun getRules( result: (List<GolfRule>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.getRules( result, errorResponse)
    suspend fun getRoundHistory(start:Int,id:String,result: (RoundHistoryPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.getRoundHistory(start,id, result, errorResponse)
    suspend fun getNotifications(start:Int,text:String,result: (NotificationPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.getNotifications(start, text,result, errorResponse)

    suspend fun sendFeedback(feedback: Feedback,result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.sendFeedback(feedback, result, errorResponse)
    suspend fun notifyNotification(id:String, tag:Int,result: (GolfNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.notifyNotification(id,tag, result, errorResponse)
    suspend fun passScoreGolf(passScore: PassScore,result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.passScoreGolf(passScore, result, errorResponse)
    suspend fun uploadScorecardGolf(file: File,courseId: String,
                                    teeType: String, date:Long, friends: List<User>?, result: (Any) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.uploadScorecardGolf(file, courseId, teeType, date, friends, result, errorResponse)
    suspend fun onCompleteRound(roundId: String, roundHost: RoundHost, result: (Round) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.onCompleteRound(roundId, roundHost, result, errorResponse)
    suspend fun onFollowClub(clubId:String, result: (FollowResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.onFollowClub(clubId, result, errorResponse)

    suspend fun onFollowersClub(clubId: String, start: Int, result: (FollowersResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.onFollowersClub(clubId,start,result,errorResponse)

    suspend fun fetchClubFeeds(clubId: String, start: Int, result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.fetchClubFeeds(clubId,start,result,errorResponse)
    fun updateMathForRoundGolfInLocal(roundId: String,hole: Hole,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.updateMathForRoundGolfInLocal(roundId, hole, onComplete, errorResponse)
    fun changeTeeTypeForRoundGolfInLocal(roundId: String,teeType: String?,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.changeTeeTypeForRoundGolfInLocal(roundId, teeType, onComplete, errorResponse)
    fun getRoundGolfExitInLocal():RoundGolf? = golfDataSource.getRoundGolfExitInLocal()

    suspend fun friendApproveRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.friendApproveRoundComplete(roundId, result, errorResponse)
    suspend fun friendNotAcceptRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = golfDataSource.friendNotAcceptRoundComplete(roundId, result, errorResponse)
}