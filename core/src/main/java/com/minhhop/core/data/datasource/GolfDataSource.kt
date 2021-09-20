package com.minhhop.core.data.datasource

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

interface GolfDataSource {
    suspend fun nearby(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun requestCourse(requestedCourse: RequestCourse, result: (RequestedCourse) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun fetchSuggestedClubs(latitude: Double, longitude: Double, start: Int, keyword: String, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun fetchRecommendClub(latitude: Double, longitude: Double, start: Int, result: (ClubPager) -> Unit, errorResponse: (ErrorResponse) -> Unit, callback: (Any) -> Unit)

    suspend fun startRound(courseId: String, latitude: Double, longitude: Double, teeType: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun pushNotificationToMemberInBattle(roundId: String, members: List<User>,
                                                   result: (ResultNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun fetchRound(roundId: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun getClubDetail(clubId: String, result: (Club) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun getPosts(type: String, limit: Int, tag:Int, result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun addComment(id: String, comment: String, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun likePost(id: String, type: Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun listComment(id: String, result: (CommentResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun deletePost(id: String, tag: Int, type: String, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun getFriends(result: (ArrayList<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun createPost(postDescription: String, files: ArrayList<String>, club: Club?, friends: List<User>, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun updatePost(postDescription: String, files: ArrayList<String>, club: Club?, friends: List<User>, id: String, tag: Int, result: (NewsFeed) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun deleteComment(postId: String, commentId: String, type: Int, result: (String) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun editComment(postId: String, commentId: String, comment: String, type: Int, result: (Comment) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun fetchClubPhotos(clubId: String, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun fetchClubReview(clubId: String, result: (List<Review>) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun addReviewForClub(clubId: String, rate: Int, content: String?, reviewId: String? = null, result: (Review) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun deletePhotoFromClub(clubId: String, photoId: String, position: Int, result: (Int) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun uploadPhotoForClub(clubId: String, files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun uploadHistoryPhotoForClub(files: List<File>, result: (List<ClubPhoto>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun exploreCourse(courseId: String, latitude: Double, longitude: Double, typeTee: String, result: (RoundGolf) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun fetchUserProfile(id: String, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun requestAction(id: String, message: String, tag: Int, result: (PeopleResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    fun getRoundGolfByCourseInLocal(courseId: String) : RoundGolf?
    fun getRoundGolfByIdInLocal(roundId: String) : RoundGolf?
    fun insertOrUpdateRoundGolfInLocal(roundGolf:RoundGolf,teeType: String?,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
    fun deleteRoundGolfInLocal(roundId:String,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
    fun deleteAllDataScoreGolfOfRoundInLocal(roundId:String,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
    fun insertOrUpdateDataScoreGolf(roundId: String,holeId:String,data: DataScoreGolf,onComplete:()->Unit, errorResponse: (ErrorResponse) -> Unit)
    fun getDataScoreGolfOfRound(roundId: String, errorResponse: (ErrorResponse) -> Unit): List<DataScoreGolf>?
    fun getDataScoreGolf(roundId: String,holeId: String) : DataScoreGolf?
    suspend fun getRules(result: (List<GolfRule>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun getRoundHistory(start:Int,id:String,result: (RoundHistoryPager) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun getNotifications(start: Int, text:String,result: (NotificationPager) -> Unit, errorResponse: (ErrorResponse) -> Unit): Any
    suspend fun notifyNotification(id:String, tag:Int, result: (GolfNotification) -> Unit, errorResponse: (ErrorResponse) -> Unit): Any
    suspend fun sendFeedback(feedback: Feedback,result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun passScoreGolf(passScore: PassScore,result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun uploadScorecardGolf(file: File,courseId: String,
            teeType: String, date: Long,friends: List<User>?, result: (Any) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun onCompleteRound(roundId: String, roundHost: RoundHost, result: (Round) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun onFollowClub(clubId:String, result: (FollowResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun updateMathForRoundGolfInLocal(roundId: String,hole: Hole,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
    fun changeTeeTypeForRoundGolfInLocal(roundId: String,teeType: String?,onComplete: () -> Unit,errorResponse: (ErrorResponse) -> Unit)
    fun getRoundGolfExitInLocal():RoundGolf?
    suspend fun friendApproveRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun friendNotAcceptRoundComplete(roundId: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun onFollowersClub(clubId: String, start: Int, result: (FollowersResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun fetchClubFeeds(clubId: String, start: Int, result: (NewsFeedResponse) -> Unit, errorResponse: (ErrorResponse) -> Unit)
}