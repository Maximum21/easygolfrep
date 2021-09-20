package com.minhhop.easygolf.presentation.signin

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.framework.Interactors
import com.minhhop.easygolf.framework.base.EasyGolfViewModel
import com.minhhop.easygolf.framework.bundle.UnregisterBundle
import kotlinx.coroutines.launch

class SignInViewModel(private val interactors: Interactors) : EasyGolfViewModel() {
    val signInResultLive = MutableLiveData<ErrorResponse?>()
    val isUnRegisterLive = MutableLiveData<UnregisterBundle>()

    fun signIn(userName:String,password:String){
        fetchSignIn(userName, password)
    }

    fun signInWithFacebook(loginResult: LoginResult){
        val graphRequest = GraphRequest.newMeRequest(loginResult.accessToken) { jsonObject, _->
            try {
                val firstName = jsonObject.getString("first_name")
                val lastName = jsonObject.getString("last_name")

                var emailFromFacebook:String? = null

                if (jsonObject.has("email")) {
                    emailFromFacebook = jsonObject.getString("email")
                }

                if (AccessToken.getCurrentAccessToken() != null) {
                    LoginManager.getInstance().logOut()
                }
                fetchSignInWithFacebook(firstName,lastName,loginResult.accessToken.token,emailFromFacebook)
            }catch (e: Exception){
                mCommonErrorLive.postValue(ErrorResponse.commonError(e.localizedMessage))
            }
        }

        val parameters = Bundle()
        parameters.putString("fields", "id,email,first_name,last_name")
        graphRequest.parameters = parameters
        graphRequest.executeAsync()
    }

    private fun fetchSignInWithFacebook(firstName:String,lastName:String,accessToken:String,emailFromFacebook:String?){
        mScope.launch {
            interactors.signInWithFacebook(firstName,lastName,accessToken,{response->
                if(response.status != null){
                    isUnRegisterLive.postValue(UnregisterBundle("facebook",accessToken,emailFromFacebook))
                }else{
                    interactors.saveAccessTokenInLocal(response.access_token)
                }
            },{error->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    private fun fetchSignIn(userName: String, password: String){
         mScope.launch {
             interactors.signIn(userName, password, { result ->
                 interactors.saveAccessTokenInLocal(result.access_token)
                 getProfileUser()
             }, {
                 signInResultLive.postValue(ErrorResponse.commonError())
             })
        }
    }

    private fun getProfileUser(){
        mScope.launch {
            interactors.getProfileUser({
                signInResultLive.postValue(null)
            },{error->
                mCommonErrorLive.postValue(error)
            })
        }
    }

    fun isReadySignIn(): Boolean = interactors.isReadySignIn()
}