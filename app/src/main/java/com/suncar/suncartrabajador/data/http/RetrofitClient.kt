package com.suncar.suncartrabajador.data.http

import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val okHttpClient =
            OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(2, TimeUnit.MINUTES)
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
