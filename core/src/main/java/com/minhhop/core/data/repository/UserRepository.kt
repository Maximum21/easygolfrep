package com.minhhop.core.data.repository

import com.minhhop.core.data.datasource.UserDataSource
import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.core.domain.rule.GolfRule
import sun.security.util.Password

class UserRepository(private val userDataSource: UserDataSource) {
    suspend fun signIn(userName:String,password:String,result: (Authorization) -> Unit, errorResponse: (ErrorResponse)->Unit)
            = userDataSource.signIn(userName,password, result,errorResponse)

    suspend fun signUp(firstName: String, lastName: String, countryCode:String, email:String,
                       phoneNumber:String, password: String,
                       result: (VerificationMessage)->Unit, errorResponse: (ErrorResponse)->Unit)
            = userDataSource.signUp(firstName, lastName, countryCode, email, phoneNumber, password, result, errorResponse)

    fun logout() = userDataSource.logout()

    suspend fun signInWithFacebook(firstName:String,lastName:String,brand:String,
                                   result: (Authorization)->Unit, errorResponse: (ErrorResponse)->Unit)
            = userDataSource.signInWithFacebook(firstName,lastName, brand, result, errorResponse)
    fun getProfileInLocal(errorResponse: (ErrorResponse)->Unit) : User? = userDataSource.getProfileInLocal(errorResponse)
    fun getRulesFromLocal(key:String,errorResponse: (ErrorResponse)->Unit) : List<GolfRule>? = userDataSource.getRulesFromLocal(key,errorResponse)
    suspend fun getProfile(result: (User) -> Unit, errorResponse: (ErrorResponse)->Unit) = userDataSource.getProfile(result,errorResponse)
    fun isReadySignIn():Boolean = userDataSource.isReadySignIn()
    fun saveAccessTokenInLocal(accessToken:String) = userDataSource.saveAccessTokenInLocal(accessToken)

    suspend fun verifyCode(token:String,onComplete: (Authorization) -> Unit,errorResponse: (ErrorResponse)->Unit)
            = userDataSource.verifyCode(token, onComplete, errorResponse)

    suspend fun resendCode(phone:String,ios:String,onComplete: (VerificationMessage) -> Unit,errorResponse: (ErrorResponse)->Unit) = userDataSource.resendCode(phone, ios, onComplete, errorResponse)

    suspend fun fetchFriends(result: (List<User>) -> Unit,errorResponse: (ErrorResponse)->Unit) = userDataSource.fetchFriends(result, errorResponse)
    fun save(rules:List<GolfRule>,onComplete:()->Unit) = userDataSource.save(rules,onComplete)

    suspend fun forgotPassword(phoneNumber:String,countryCode: String,result: (VerificationMessage) -> Unit,errorResponse: (ErrorResponse)->Unit)
            = userDataSource.forgotPassword(phoneNumber, countryCode, result, errorResponse)
    suspend fun unregisterSocial(type: String, accessToken: String, phoneNumber: String, countryCode: String, email: String?,
                                 result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userDataSource.unregisterSocial(type, accessToken, phoneNumber, countryCode, email, result, errorResponse)
    fun updateProfileUserInLocal(user:User,onComplete: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit) = userDataSource.updateProfileUserInLocal(user, onComplete, errorResponse)
    suspend fun updateProfileUser(firstName: String,lastName: String,email: String?,
                                  countryCode:String,phoneNumber: String?,birthday:String?,
                                  gender:String?,phoneNotification:Boolean,emailNotification:Boolean,
                                  result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit)
            = userDataSource.updateProfileUser(firstName, lastName, email, countryCode, phoneNumber, birthday, gender, phoneNotification, emailNotification, result, errorResponse)

    suspend fun resetPasswrod(password:ResetPassword,result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit) = userDataSource.resetPasswrod(password,result,errorResponse)
}