package com.suncar.suncartrabajador.data.http

import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Authorization", "Bearer suncar-token-2025")
        val request = requestBuilder.build()
        chain.proceed(request)
    }

    private val okHttpClient =
            OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .addInterceptor(authInterceptor)
                    .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
                .baseUrl(HttpConfig.BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    inline fun <reified T> createService(): T = retrofit.create(T::class.java)
}
