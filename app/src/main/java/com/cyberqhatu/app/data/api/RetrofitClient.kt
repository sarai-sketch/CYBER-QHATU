package com.cyberqhatu.app.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Apunta al servidor local (ejemplo: Spring Boot, Express, etc.) en el emulador de Android
    // 10.0.2.2 es la IP para acceder a localhost de tu computadora desde el emulador
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val authApiService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }
}
