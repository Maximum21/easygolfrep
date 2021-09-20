package com.minhhop.easygolf.services

import com.google.gson.GsonBuilder
import com.minhhop.easygolf.BuildConfig
import com.minhhop.easygolf.framework.network.GeneralService
import com.minhhop.easygolf.framework.network.GolfService
import com.minhhop.easygolf.framework.network.UserService
import com.minhhop.easygolf.services.retrofit.RxErrorHandlingCallAdapterFactory
import com.minhhop.easygolf.framework.common.Contains
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiService private constructor() {


    private val retrofitWithRetry: Retrofit

    val userService: UserService
        get() = retrofitWithRetry.create(UserService::class.java)

    val generalService: GeneralService
        get() = retrofitWithRetry.create(GeneralService::class.java)

    val golfService: GolfService
        get() = retrofitWithRetry.create(GolfService::class.java)

    init {

        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val clientWithRetry = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(object : Interceptor{
                    override fun intercept(chain: Interceptor.Chain): Response {
                        val token = PreferenceService.getInstance().getToken()

                        val builder = chain.request().newBuilder()
                        if (token != null) {
                            builder.addHeader("Authorization", "Bearer $token")
                        }

                        return chain.proceed(builder.build())
                    }

                })
                .build()

        retrofitWithRetry = Retrofit.Builder()
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder()
                                .enableComplexMapKeySerialization()
                                .serializeNulls()
                                .setPrettyPrinting()
                                .create())
                )
                .client(clientWithRetry)
                .baseUrl(urlApi)
                .build()
    }

    companion object {

        private var instance: ApiService? = null

        private val urlApi: String
            get() = Contains.urlApi

        fun getInstance(): ApiService {
            if (instance == null) {
                instance = ApiService()
            }
            return ApiService()
        }
    }
}
