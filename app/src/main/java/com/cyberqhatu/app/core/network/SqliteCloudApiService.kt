package com.cyberqhatu.app.core.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface SqliteCloudApiService {

    @Headers(
        "Authorization: Bearer t4pCQWrW0zPXHfbRF68ttrJXMijVP4m9PFmnfTiWO8s",
        "Content-Type: application/json"
    )
    @POST("webrequest")
    suspend fun ejecutarSql(
        @Body request: SqliteCloudQueryRequest
    ): Response<SqliteCloudQueryResponse>
}
