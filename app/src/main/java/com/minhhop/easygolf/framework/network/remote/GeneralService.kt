package com.minhhop.easygolf.framework.network.remote

import com.minhhop.core.domain.Country
import com.minhhop.core.domain.PolicyTerm
import com.minhhop.core.domain.ResultNotification
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.easygolf.framework.models.request.DataPushNotificationMembersRequest
import com.minhhop.easygolf.framework.models.SyncContact
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GeneralService {

    @GET("1.0/countries")
    fun allCountries(): ResponseRequest<List<Country>>

    @POST("1.0/contacts/sync")
    fun syncContacts(@Body sysContact: SyncContact): ResponseRequest<VerificationMessage>

    @POST("1.0/rounds/{id}/invite")
    fun pushNotificationToUserInviteBattle(@Path("id") roundId: String, @Body data: DataPushNotificationMembersRequest): ResponseRequest<ResultNotification>

    @GET("1.0/contents/{option}")
    fun getTermsPolicy(@Path("option") option: String): ResponseRequest<PolicyTerm>

}