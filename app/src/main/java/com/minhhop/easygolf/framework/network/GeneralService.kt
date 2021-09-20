package com.minhhop.easygolf.framework.network

import com.minhhop.core.domain.Country
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import retrofit2.http.*
import rx.Observable

interface GeneralService {

    @GET("1.0/users/me/friends")
    fun listFriend(): Observable<List<UserEntity>>


    @FormUrlEncoded
    @POST("1.0/social/{type}")
    fun unregister(@Path("type") type: String, @Query("access_token") token: String,
                   @Field("phone_number") phoneNumber: String, @Field("country_code") countryCode: String,
                   @Field("email") email: String): Observable<VerificationMessage>

    @POST("1.0/users")
    fun register(@Body loginRequest: LoginRequest): Observable<VerificationMessage>



    @GET("1.0/contents/{option}")
    fun getTermsPolicy(@Path("option") option: String): Observable<TermsPolicy>

    @POST("1.0/feedback")
    fun sendFeedback(@Body feedback: Feedback): Observable<VerificationMessage>

    @POST("1.0/contacts/sync")
    fun syncContact(@Body syncContact: SyncContact): Observable<VerificationMessage>



    @GET("1.0/countries")
    fun allCountries(): ResponseRequest<List<Country>>


    @POST("1.0/contacts/sync")
    fun syncContacts(@Body sysContact: SyncContact): ResponseRequest<VerificationMessage>
}
