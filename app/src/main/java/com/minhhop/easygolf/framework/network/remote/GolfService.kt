package com.minhhop.easygolf.framework.network.remote

import com.google.android.gms.common.internal.safeparcel.SafeParcelable
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
import com.minhhop.easygolf.adapter.FollowersAdapter
import com.minhhop.easygolf.framework.models.request.AddReviewRequest
import com.minhhop.easygolf.framework.models.request.StartRoundRequest
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface GolfService {

    @GET("2.0/golf/clubs/nearby")
    fun courseNearby(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double,
                     @Query("start") start: Int, @Query("keyword") keyword: String):
            ResponseRequest<ClubPager>

    @GET("2.0/golf/clubs/suggest")
    fun fetchSuggestedClubs(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("start") start: Int, @Query("keyword") keyword: String):
            ResponseRequest<ClubPager>

    @GET("2.0/golf/clubs/recommend")
    fun fetchRecommendClubs(@Query("latitude") latitude: Double, @Query("longitude") longitude: Double, @Query("start") start: Int):
            ResponseRequest<ClubPager>

    @POST("1.0/golf/courses/{course_id}")
    fun startRound(@Path("course_id") courseId: String, @Body data: StartRoundRequest): ResponseRequest<RoundGolf>

    @GET("1.0/rounds/{id}")
    fun getRound(@Path("id") roundId: String): ResponseRequest<RoundGolf>

    @GET("2.0/golf/clubs/{id}")
    fun getClubDetail(@Path("id") id:String):ResponseRequest<Club>

    @GET("2.0/golf/clubs/{id}/followers")
    fun getClubDetailFollowers(@Path("id") id:String,
                               @Query("start") start:String):ResponseRequest<FollowersResponse>

    @GET("2.0/golf/clubs/{id}/feeds")
    fun getClubFeeds(@Path("id") id:String,
                               @Query("start") start:String):ResponseRequest<NewsFeedResponse>

    @GET("1.0/feeds")
    fun getPosts(@Query("type") type: String, @Query("start") start: Int): ResponseRequest<NewsFeedResponse>

    @FormUrlEncoded
    @POST("1.0/posts/{id}/comments")
    fun addComment(@Path("id") id: String,@Field("comment") comment: String): ResponseRequest<Comment>

    @POST("1.0/posts/{id}/likes")
    fun likePost(@Path("id") id: String): ResponseRequest<NewsFeed>

    @DELETE("1.0/posts/{id}/likes")
    fun removeLike(@Path("id") id: String): ResponseRequest<NewsFeed>

    @POST("1.0/posts/{id}/share")
    fun sharePost(@Path("id") id: String,
                  @Body comment: Any): ResponseRequest<NewsFeed>

    @GET("1.0/posts/{id}/comments")
    fun listComment(@Path("id") id: String): ResponseRequest<CommentResponse>

    @GET("1.0/users/me/friends")
    fun getFriends(): ResponseRequest<ArrayList<User>>

    @Multipart
    @POST("1.0/posts")
    fun createPostImage(@Part("content") content: RequestBody,
                        @Part("club") club: RequestBody?,
                        @Part("friends") friends: ArrayList<RequestBody>,
                        @Part files:List<MultipartBody.Part>): ResponseRequest<NewsFeed>

    @Multipart
    @POST("1.0/posts")
    fun createPost(@Part("content") content: RequestBody,
                   @Part("club") club: RequestBody?,
                   @Part("friends") friends: ArrayList<RequestBody>): ResponseRequest<NewsFeed>
    @Multipart
    @POST("1.0/posts/{id}")
    fun updatePostImage(@Part("content") content: RequestBody,
                        @Part("club") club: RequestBody?,
                       @Path("id") id: String,
                        @Part("friends") friends: ArrayList<RequestBody>,
                        @Part files:List<MultipartBody.Part>): ResponseRequest<NewsFeed>

    @Multipart
    @POST("1.0/posts/{id}")
    fun updatePost(@Part("content") content: RequestBody,
                   @Part("club") club: RequestBody?,
                   @Path("id") id: String,
                   @Part("friends") friends: ArrayList<RequestBody>): ResponseRequest<NewsFeed>

    @DELETE("1.0/posts/{postId}/comments/{commentId}")
    fun deleteComment(@Path("postId") postId: String,
                      @Path("commentId") commentId: String): ResponseRequest<ResponseBody>

    @PUT("1.0/posts/{postId}/comments/{commentId}")
    fun editComment(@Path("postId") postId: String,
                    @Path("commentId") commentId: String,
                    @Body comment: Any): ResponseRequest<Comment>

    @GET("2.0/golf/clubs/{id}/photos")
    fun fetchClubPhotos(@Path("id") clubId:String): ResponseRequest<List<ClubPhoto>>

    @GET("2.0/golf/clubs/{id}/reviews")
    fun fetchClubReview(@Path("id") clubId:String): ResponseRequest<List<Review>>

    @POST("2.0/golf/clubs/{clubId}/reviews")
    fun addReviewForClub(@Path("clubId") clubId:String,@Body data: AddReviewRequest): ResponseRequest<Review>

    @POST("2.0/golf/clubs/{clubId}/reviews/{id}")
    fun addReviewForClub(@Path("clubId") clubId:String,@Body data: AddReviewRequest,@Path("id") id:String): ResponseRequest<Review>

    @DELETE("2.0/golf/clubs/{clubId}/photos/{id}")
    fun deleteClubPhoto(@Path("clubId") clubId:String,@Path("id") id:String): ResponseRequest<Unit?>

    @DELETE("1.0/posts/{postId}")
    fun deletePost(@Path("postId") postId:String): ResponseRequest<ResponseBody>


    @DELETE("1.0/posts/{postId}/images/{imageId}")
    fun deleteImage(@Path("postId") postId:String,
                    @Path("imageId") imageId:String): ResponseRequest<ResponseBody>

    @Multipart
    @POST("2.0/golf/clubs/{id}/photos")
    fun uploadPhotoForClub(@Path("id") clubId: String, @Part filePhotos: List<MultipartBody.Part>) : ResponseRequest<List<ClubPhoto>>

    @Multipart
    @POST("2.0/people/me/pass-scores")
    fun uploadHistoryPhotoForClub(@Part filePhotos: List<MultipartBody.Part>) : ResponseRequest<List<ClubPhoto>>

    @GET("2.0/golf/courses/{id}/explore")
    fun exploreCourse(@Path("id") clubId:String,@Query("latitude") latitude:Double,
                      @Query("longitude") longitude:Double, @Query("tee") teeType:String): ResponseRequest<RoundGolf>

    @GET("2.0/people/{id}")
    fun fetchUserProfile(@Path("id") id: String): ResponseRequest<PeopleResponse>

    @POST("2.0/people/me/friends")
    fun addFriend(@Body data: Any): ResponseRequest<PeopleResponse>

    @PUT("2.0/people/me/{action}")
    fun requestAction(@Path("action") action:String,@Body data: Any): ResponseRequest<PeopleResponse>

    @PUT("2.0/people/me/friends/{action}")
    fun requestActionAcceptOrDecline(@Path("action") action:String,@Body data: Any): ResponseRequest<PeopleResponse>


    @GET("1.0/golf/rules")
    fun getGolfRules():ResponseRequest<List<GolfRule>>

    @GET("2.0/notifications")
    fun getNotifications(@Query("start") start: Int,
                         @Query("search") keyword: String):ResponseRequest<NotificationPager>

    @GET("2.0/notifications/{id}")
    fun readNotifications(@Path("id") start: String):ResponseRequest<GolfNotification>

    @DELETE("2.0/notifications/{id}")
    fun deleteNotifications(@Path("id") start: String):ResponseRequest<ResponseBody>

    @GET("1.0/golf/performances")
    fun getRoundHistory(@Query("start") start: Int):ResponseRequest<RoundHistoryPager>

    @GET("2.0/people/{id}/rounds")
    fun getUserRoundHistory(@Path("id") id:String,
                            @Query("start") start: Int,
                            @Query("max") max:String):ResponseRequest<RoundHistoryPager>

    @POST("1.0/feedback")
    fun sendFeedback(@Body feedback: Feedback): ResponseRequest<VerificationMessage>

    @POST("2.0/round/pass-score")
    fun passScoreGolf(@Body passScore: PassScore): ResponseRequest<VerificationMessage>

    @POST("2.0/round/score-card")
    fun uploadPhotoScorecardGolf(@Body data: RequestBody) : ResponseRequest<ResponseBody>

    @PUT("1.0/rounds/{roundId}")
    fun completeRound(@Path("roundId") roundId: String, @Body data: RoundHost?): ResponseRequest<Round>

    @POST("2.0/golf/clubs/{club_id}/follow")
    fun onFollowClub(@Path("club_id") clubId: String):ResponseRequest<FollowResponse>

    @POST("1.0/requests")
    fun requestCourse(@Body requestCourse: RequestCourse):ResponseRequest<RequestedCourse>

    @GET("2.0/people/{id}/feeds")
    fun getUserPosts(@Path("id") id:String,
                 @Query("start") start: Int): ResponseRequest<NewsFeedResponse>

    @PUT("1.0/rounds/{round_id}/approve")
    fun friendApproveRound(@Path("round_id") roundId: String) : ResponseRequest<VerificationMessage>

    @PUT("1.0/rounds/{round_id}/cancel")
    fun friendNotAcceptRound(@Path("round_id") roundId: String) : ResponseRequest<VerificationMessage>
}