package com.minhhop.easygolf.framework.network.retrofit

import android.util.Log
import com.google.gson.Gson
import com.minhhop.core.domain.ErrorResponse
import com.minhhop.easygolf.framework.network.ConnectActivity
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException

class ResponseRequest<R>(val call: Call<R>,private val connectActivity: ConnectActivity) {

    private var onSuccessCallback : ((R) -> Unit)? = null
    private var onSuccessEmptyContentCallback : ((R?) -> Unit)? = null

    fun run(onSuccess:(R)->Unit,onError: (ErrorResponse)->Unit){
        onSuccessCallback = onSuccess
        enqueue(onError)
    }

    fun runNoContent(onSuccess:(R?)->Unit,onError: (ErrorResponse)->Unit){
        onSuccessEmptyContentCallback = onSuccess
        enqueue(onError)
    }

    private fun enqueue(onError: (ErrorResponse)->Unit){
        if(connectActivity.isHaveNetworkConnected()) {
            try {
                call.enqueue(object : Callback<R> {
                    override fun onFailure(call: Call<R>, t: Throwable) {
                        if(!call.isCanceled){
                            onError(ErrorResponse.commonError(t.localizedMessage))
                        }
                    }

                    override fun onResponse(call: Call<R>, response: Response<R?>) {
                        Log.e("trestys","${response.message()}")
                        handleResponse(response, onError)
                    }
                })

            } catch (t: IOException) {
                return if (t is SocketTimeoutException) {
                    onError(ErrorResponse.requestTimeout())
                } else {
                    onError(ErrorResponse.createExceptionInternal(t.localizedMessage))
                }
            }
        }else{
            onError(ErrorResponse.noHaveNetwork())
        }
    }

    private fun handleResponse(response: Response<R?>,onError: (ErrorResponse)->Unit){
        if(response.isSuccessful){
            Log.e("trestys","success ${response.message()}")
            if(onSuccessCallback != null){
                response.body()?.let { data->
                    onSuccessCallback?.invoke(data)
                }?: onSuccessEmptyContentCallback?.invoke(response.body())

            }else{
                onSuccessEmptyContentCallback?.invoke(response.body())
            }
        }else{
            Log.e("trestys"," $response success== $onError")
            if(response.code() in 400..511){
                if(response.errorBody() != null){
                    val gson = Gson()
                    val errorResponse:ErrorResponse =
                            try {
                                response.errorBody()?.string()?.let { jsonError->
                                    val json = JSONObject(jsonError)
                                    if(json.has(ErrorResponse::message.name)){
                                        val errorResult = gson.fromJson(jsonError,
                                                ErrorResponse::class.java)
                                        errorResult.status = response.code()
                                        errorResult
                                    }else{
                                        val message = json.get("error") as String
                                        ErrorResponse.commonError(message)
                                    }
                                }?:  ErrorResponse.commonError()


                            }catch (e:Exception){
                                ErrorResponse.createExceptionInternal()
                            }

                    onError(errorResponse)

                }else{
                    onError(ErrorResponse.createExceptionInternal(response.message()))
                }
            }else{
                if(onSuccessCallback != null){
                    response.body()?.let { data->
                        onSuccessCallback?.invoke(data)
                    }?: onSuccessEmptyContentCallback?.invoke(response.body())

                }else{
                    onSuccessEmptyContentCallback?.invoke(response.body())
                }
            }
        }
    }
}