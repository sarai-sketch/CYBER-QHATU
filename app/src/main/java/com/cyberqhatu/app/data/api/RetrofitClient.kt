package com.cyberqhatu.app.data.api

import com.cyberqhatu.app.verificacion.data.remote.api.VerificacionApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val verificacionApiService: VerificacionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(VerificacionApiService::class.java)
    }
}
