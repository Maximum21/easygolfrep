package com.minhhop.easygolf.framework.network


import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import okhttp3.MultipartBody
import retrofit2.http.*
import rx.Observable

/**
 * Created by pdypham on 3/30/18.
 */

interface UserService {

    @GET("1.0/users/me")
    fun profile(): Observable<UserEntity>

    @GET("1.0/users/me")
    fun fetchProfile(): ResponseRequest<User>

    @POST("1.0/social/{type}")
    fun loginSocial(@Path("type") type: String = "facebook", @Query("access_token") token: String,
                    @Body socicalRequest: SocicalRequest): ResponseRequest<Authorization>

    @POST("1.0/users")
    fun signUp(@Body registerRequest: RegisterRequest): ResponseRequest<VerificationMessage>


    @POST("1.0/login")
    fun login(@Body loginRequest: LoginRequest): ResponseRequest<Authorization>


    @POST("1.0/users/validate")
    fun verificationCode(@Body code: VerifyCodeRequest): ResponseRequest<Authorization>

    @POST("1.0/auth/token")
    fun resendCode(@Body resendCodeRequest: ResendCodeRequest): ResponseRequest<VerificationMessage>


    @POST("1.0/users/password")
    fun updatePassword(@Body resetPassword: ResetPassword): Observable<UserEntity>

    @Multipart
    @POST("1.0/users/avatar")
    fun updateAvatar(@Part avatar: MultipartBody.Part): Observable<UserEntity>

    @PUT("1.0/users/me")
    fun updateProfile(@Body user: UpdateUser): Observable<UserEntity>
    

    @POST("1.0/users/me/friends")
    fun inviteFriend(@Body contactRetrofit: ContactRetrofit): Observable<ContactResult>
}