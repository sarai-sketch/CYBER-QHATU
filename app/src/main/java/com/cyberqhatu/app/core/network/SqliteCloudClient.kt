package com.cyberqhatu.app.core.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SqliteCloudClient {

    private const val BASE_URL = "https://clxgbyqcvz.g6.sqlite.cloud:8860/v2/"

    val apiService: SqliteCloudApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SqliteCloudApiService::class.java)
    }
}
