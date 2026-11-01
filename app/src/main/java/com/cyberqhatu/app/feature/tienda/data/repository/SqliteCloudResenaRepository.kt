package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.ResenaCompradorRequest
import com.cyberqhatu.app.core.network.ResenasRequest
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient

class SqliteCloudResenaRepository {

    private val apiService = SqliteCloudFunctionsClient.apiService

    suspend fun consultarResenas(): Boolean {
        return try {
            val response = apiService.resenas(ResenasRequest(filter = "a"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando reseñas: ${e.message}", e)
            false
        }
    }

    suspend fun guardarResenaComprador(): Boolean {
        return try {
            val response = apiService.resenaComprador(ResenaCompradorRequest(filter = "a"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando reseña comprador: ${e.message}", e)
            false
        }
    }
}
