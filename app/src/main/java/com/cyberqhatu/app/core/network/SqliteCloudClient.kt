package com.cyberqhatu.app.core.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object SqliteCloudClient {

    // El puerto 8080 es para HTTP/REST con Retrofit. (8860 es nativo y fallará con Retrofit)
    private const val BASE_URL = "https://clxgbyqcvz.g6.sqlite.cloud:8080/v2/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .build()

    val apiService: SqliteCloudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SqliteCloudApiService::class.java)
    }
}
