package com.minhhop.core.data.datasource

import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.core.domain.rule.GolfRule
import sun.security.util.Password

interface UserDataSource {
    suspend fun signIn(userName: String, password: String, result: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun signUp(firstName: String, lastName: String, countryCode: String, email: String,
                       phoneNumber: String, password: String,
                       result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    fun logout()

    suspend fun signInWithFacebook(firstName: String, lastName: String, accessToken: String,
                                   result: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun getProfile(result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun getProfileInLocal(errorResponse: (ErrorResponse) -> Unit): User?
    fun getRulesFromLocal(key: String, errorResponse: (ErrorResponse) -> Unit): List<GolfRule>?
    fun isReadySignIn(): Boolean
    fun saveAccessTokenInLocal(accessToken: String)

    suspend fun verifyCode(token: String, onComplete: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun resendCode(phone: String, ios: String, onComplete: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)

    suspend fun fetchFriends(result: (List<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun save(rules: List<GolfRule>, onComplete: () -> Unit)
    suspend fun forgotPassword(phoneNumber: String, countryCode: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun unregisterSocial(type: String, accessToken: String, phoneNumber: String, countryCode: String, email: String?,
                                 result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    fun updateProfileUserInLocal(user:User,onComplete: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun updateProfileUser(firstName: String,lastName: String,email: String?,
                                  countryCode:String,phoneNumber: String?,birthday:String?,
                                  gender:String?,phoneNotification:Boolean,emailNotification:Boolean,
                                  result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
    suspend fun resetPasswrod(user: ResetPassword, result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
}