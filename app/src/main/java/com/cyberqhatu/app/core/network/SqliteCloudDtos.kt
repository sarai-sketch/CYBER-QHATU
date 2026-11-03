package com.cyberqhatu.app.core.network

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class SqliteCloudQueryRequest(
    @SerializedName("database") val database: String = "cyberQhatu.sqlite",
    @SerializedName("sql") val sql: String
)

data class SqliteCloudQueryResponse(
    @SerializedName("status") val status: String? = null,
    @SerializedName("data") val data: JsonElement? = null,
    @SerializedName("error") val error: String? = null
)

fun JsonElement?.parseAsListOfMaps(): List<Map<String, Any?>> {
    val element = this ?: return emptyList()
    if (!element.isJsonArray) return emptyList()
    val array = element.asJsonArray
    val result = mutableListOf<Map<String, Any?>>()
    for (item in array) {
        if (item.isJsonObject) {
            val obj = item.asJsonObject
            val map = mutableMapOf<String, Any?>()
            for ((key, value) in obj.entrySet()) {
                map[key] = when {
                    value.isJsonNull -> null
                    value.isJsonPrimitive -> {
                        val prim = value.asJsonPrimitive
                        when {
                            prim.isBoolean -> prim.asBoolean
                            prim.isNumber -> prim.asNumber
                            else -> prim.asString
                        }
                    }
                    else -> value.toString()
                }
            }
            result.add(map)
        }
    }
    return result
}
