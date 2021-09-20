package com.minhhop.easygolf.framework.network;

import com.minhhop.core.domain.ResultNotification;
import com.minhhop.easygolf.framework.models.request.DataPushNotificationMembersRequest;
import com.minhhop.easygolf.framework.models.Club;
import com.minhhop.easygolf.framework.models.CoursePager;
import com.minhhop.easygolf.framework.models.DataLocation;
import com.minhhop.easygolf.framework.models.DataLocationMissing;
import com.minhhop.easygolf.framework.models.NewsPager;
import com.minhhop.easygolf.framework.models.Participant;
import com.minhhop.easygolf.framework.models.RoundMatch;
import com.minhhop.easygolf.framework.models.RoundPager;
import com.minhhop.easygolf.framework.models.RuleGolf;
import com.minhhop.easygolf.framework.models.Scorecard;
import com.minhhop.easygolf.framework.models.Tournament;
import com.minhhop.easygolf.framework.models.TournamentEntry;
import com.minhhop.easygolf.framework.models.TournamentPaging;
import com.minhhop.easygolf.framework.models.Video;
import com.minhhop.easygolf.framework.models.VideoPager;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

public interface GolfService {

    @GET("2.0/golf/clubs/nearby")
    Observable<CoursePager> getNearby(@Query("latitude") double latitude, @Query("longitude") double longitude, @Query("start") int start);

    @POST("1.0/golf/courses/{id_course}")
    Observable<RoundMatch> getRoundMath(@Path("id_course") String idCourse, @Body DataLocation data);

    @POST("1.0/golf/courses/{id_course}")
    Observable<RoundMatch> getRoundMath(@Path("id_course") String idCourse, @Body DataLocationMissing data);

    @GET("1.0/golf/channels/videos")
    Observable<VideoPager> getVideoPager(@Query("start") int start);

    @GET("1.0/golf/news")
    Observable<NewsPager> getNews(@Query("start") int start);


    @GET("1.0/golf/channels/recommend/{video_id}")
    Observable<List<Video>> getRecommendedVideo(@Path("video_id") String video_id);


    @PUT("1.0/rounds/{id_round}")
    Observable<RoundMatch> finishGround(@Path("id_round") int id_round);

    @PATCH("1.0/rounds/{id_round}")
    Observable<RoundMatch> updateGround(@Path("id_round") int id_round, @Body Object round);


    @GET("2.0/golf/clubs/nearby")
    Observable<CoursePager> searchCourses(@Query("latitude") double latitude,
                                          @Query("longitude") double longitude,
                                          @Query("keyword") String keyword, @Query("start") int start,@Query("debug") String debug);


    @GET("1.0/golf/performances")
    Observable<RoundPager> getPlan(@Query("start") int start);

    @GET("1.0/rounds/{id}")
    Observable<RoundMatch> getRoundDetail(@Path("id") String idRound);



    @GET("1.0/rounds/{id}/scorecard")
    Observable<Scorecard> getScoreCard(@Path("id") int idRound);

    @GET("2.0/golf/clubs/{id}")
    Observable<Club> getClubDetails(@Path("id") String idClub);

//
//    @GET("1.0/golf/courses/{id}/setting")
//    Observable<CourseSetting> getCourseSetting(@Path("id") String idCourse);


    @GET("1.0/golf/rules")
    Observable<List<RuleGolf>> getRuleGolf();

    @GET("1.0/booking/find/tee-times")
    Observable<CoursePager> findTeeTimes(@Query("latitude") double latitude,
                                         @Query("longitude") double longitude,
                                         @Query("date") long date,
                                         @Query("start") int start);

    @PUT("1.0/rounds/{id_round}")
    Observable<RoundMatch> RoundHost(@Path("id_round") String id_round, @Body Object data);

    @GET("1.0/tournaments")
    Observable<TournamentPaging> getTournaments(@Query("offset") int start,@Query("max") int max,@Query("type") String type);

    @GET("1.0/tournaments/{id}")
    Observable<Participant> getTournamentDetail(@Path("id") int id);

    @POST("1.0/tournaments")
    Observable<Tournament> createTournament(@Body TournamentEntry tournamentEntry);

    @Multipart
    @POST("1.0/tournaments/{id}/photo")
    Observable<Tournament> uploadPhotoTournament(@Path("id") int id,@Part MultipartBody.Part photo);


    @PUT("1.0/tournaments/{id}/{type}")
    Observable<Participant> isRejectAcceptTournament(@Path("id") int id,@Path("type") String type);


    @PUT("1.0/tournaments/{id}")
    Observable<Tournament> addFriendTournament(@Path("id") int id, @Body Object friends);

    @DELETE("1.0/tournaments/{id}")
    Observable deleteTournament(@Path("id") int id);

    @DELETE("1.0/tournaments/{id}/delete-friends")
    Observable<Tournament> removeFriendTournament(@Path("id") int id,@Query("friend") int friend);

    @PUT("1.0/tournaments/{id}")
    Observable<Tournament> updateTournament(@Path("id") int id, @Body HashMap<String,String> data);


    @POST("1.0/rounds/{id}/invite")
    Observable<ResultNotification> pushNotificationToUserInviteBattle(@Path("id") String roundId, @Body DataPushNotificationMembersRequest data);
}


