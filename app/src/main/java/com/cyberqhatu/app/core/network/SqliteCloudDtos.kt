package com.cyberqhatu.app.core.network

import com.google.gson.annotations.SerializedName

data class SqliteCloudQueryRequest(
    @SerializedName("database") val database: String = "auth.sqlitecloud",
    @SerializedName("sql") val sql: String
)

data class SqliteCloudQueryResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: List<Map<String, Any?>>? = null,
    @SerializedName("error") val error: String? = null
)
