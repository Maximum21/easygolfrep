package com.minhhop.easygolf.framework.implementation

import android.os.Build
import com.google.firebase.messaging.FirebaseMessaging
import com.minhhop.core.data.datasource.UserDataSource
import com.minhhop.core.domain.Authorization
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.core.domain.User
import com.minhhop.core.domain.VerificationMessage
import com.minhhop.core.domain.password.ResetPassword
import com.minhhop.core.domain.rule.GolfRule
import com.minhhop.easygolf.framework.database.PreferenceManager
import com.minhhop.easygolf.framework.database.RealmManager
import com.minhhop.easygolf.framework.models.*
import com.minhhop.easygolf.framework.models.request.ForgotPasswordRequest
import com.minhhop.easygolf.framework.models.request.UpdateProfileRequest
import com.minhhop.easygolf.framework.network.remote.UserService

class UserImpl(private val preferenceManager: PreferenceManager,
               private val realmManager: RealmManager, private val userService: UserService) : UserDataSource {


    override fun logout() {
        realmManager.removeProfile({
            preferenceManager.removeToken()
        }, {
        })
    }

    override suspend fun signUp(firstName: String, lastName: String, countryCode: String, email: String, phoneNumber: String,
                                password: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.signUp(RegisterRequest(firstName, lastName, countryCode, email, phoneNumber, password))
                .run({ response ->
                    result(response)
                }, { error ->
                    errorResponse(error)
                })
    }


    override suspend fun signInWithFacebook(firstName: String, lastName: String, accessToken: String, result: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                errorResponse(ErrorResponse.commonError(it.exception?.localizedMessage))
            } else {
                it.result?.let { deviceToken ->
                    userService.loginSocial("facebook", accessToken, SocicalRequest(firstName, lastName, Build.MODEL, deviceToken))
                            .run({ response ->
                                result(response)
                            }, { error ->
                                errorResponse(error)
                            })
                }
            }
        }
    }

    override fun saveAccessTokenInLocal(accessToken: String) {
        preferenceManager.setToken(accessToken)
    }

    override fun save(rules: List<GolfRule>, onComplete: () -> Unit) {
        realmManager.saveRules(rules, onComplete)
    }

    override suspend fun forgotPassword(phoneNumber: String, countryCode: String, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.forgotPassword(ForgotPasswordRequest(phoneNumber, countryCode)).run({ response ->
            result(response)
        }, { error ->
            errorResponse(error)
        })
    }

    override suspend fun unregisterSocial(type: String, accessToken: String, phoneNumber: String, countryCode: String, email: String?, result: (VerificationMessage) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.unregister(type, accessToken, phoneNumber, countryCode, email).run({ response ->
            result(response)
        }, { error ->
            errorResponse(error)
        })
    }

    override fun updateProfileUserInLocal(user: User, onComplete: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        realmManager.insertOrUpdateProfileUser(user, onComplete)
    }

    override suspend fun updateProfileUser(firstName: String, lastName: String, email: String?, countryCode: String, phoneNumber: String?,
                                           birthday: String?, gender: String?, phoneNotification: Boolean, emailNotification: Boolean, result: (User) -> Unit,
                                           errorResponse: (ErrorResponse) -> Unit) {
        userService.updateProfileUser(
                UpdateProfileRequest(firstName, lastName, email, countryCode, phoneNumber, birthday, gender, phoneNotification, emailNotification)
        ).run({ response ->
            result(response)
        }, { error ->
            errorResponse(error)
        })
    }

    override suspend fun resetPasswrod(user: ResetPassword, result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.updatePassword(user).run({ response ->
            result(response)
        }, { error ->
            errorResponse(error)
        })
    }

    override suspend fun signIn(userName: String, password: String, result: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                errorResponse(ErrorResponse.commonError(it.exception?.localizedMessage))
            } else {
                it.result?.let { deviceToken ->
                    userService.login(LoginRequest(userName, password, Build.MODEL, deviceToken))
                            .run({ response ->
                                result(response)
                            }, { error ->
                                errorResponse(error)
                            })
                }
            }
        }
    }

    override suspend fun getProfile(result: (User) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.fetchProfile().run({ response ->
            result(response)
        }, { error ->
            errorResponse(error)
        })
    }

    override fun getProfileInLocal(errorResponse: (ErrorResponse) -> Unit): User? = realmManager.getProfile()
    override fun getRulesFromLocal(key: String, errorResponse: (ErrorResponse) -> Unit): List<GolfRule>? = realmManager.searchKeyRulesGolfV2(key)

    override fun isReadySignIn(): Boolean = preferenceManager.getToken() != null

    override suspend fun verifyCode(token: String, onComplete: (Authorization) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (!it.isSuccessful) {
                errorResponse(ErrorResponse.commonError(it.exception?.localizedMessage))
            } else {
                it.result?.let { deviceToken ->
                    userService.verificationCode(VerifyCodeRequest(token, Build.MODEL, deviceToken))
                            .run({ response ->
                                onComplete(response)
                            }, { error ->
                                errorResponse(error)
                            })
                }
            }
        }
    }

    override suspend fun resendCode(phone: String, ios: String, onComplete: (VerificationMessage) -> Unit,
                                    errorResponse: (ErrorResponse) -> Unit) {

        userService.resendCode(ResendCodeRequest(phone, ios))
                .run({ result ->
                    onComplete(result)
                }, { error ->
                    errorResponse(error)
                })
    }

    override suspend fun fetchFriends(result: (List<User>) -> Unit, errorResponse: (ErrorResponse) -> Unit) {
        userService.fetchFriends().run({
            result(it)
        }, {
            error(it)
        })
    }

}