package com.minhhop.easygolf.framework.network.remote

import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.entity.UserEntity
import com.minhhop.easygolf.framework.models.request.ForgotPasswordRequest
import com.minhhop.easygolf.framework.models.request.UpdateProfileRequest
import com.minhhop.easygolf.framework.network.retrofit.ResponseRequest
import okhttp3.MultipartBody
import retrofit2.http.*
import rx.Observable

interface UserService {

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

    @GET("1.0/users/me/friends")
    fun fetchFriends(): ResponseRequest<List<User>>

    @POST("1.0/auth/token")
    fun forgotPassword(@Body data: ForgotPasswordRequest): ResponseRequest<VerificationMessage>

    @POST("1.0/users/password")
    fun updatePassword(@Body resetPassword: ResetPassword): ResponseRequest<User>

    @Multipart
    @POST("1.0/users/avatar")
    fun updateAvatar(@Part avatar: MultipartBody.Part): Observable<UserEntity>

    @PUT("1.0/users/me")
    fun updateProfileUser(@Body data: UpdateProfileRequest): ResponseRequest<User>


    @FormUrlEncoded
    @POST("1.0/social/{type}")
    fun unregister(@Path("type") type: String, @Query("access_token") token: String,
                   @Field("phone_number") phoneNumber: String, @Field("country_code") countryCode: String,
                   @Field("email") email: String?): ResponseRequest<VerificationMessage>
}