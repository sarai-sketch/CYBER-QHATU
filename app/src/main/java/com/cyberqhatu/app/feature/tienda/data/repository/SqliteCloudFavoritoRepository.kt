package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.ConsultarFavoritosRequest
import com.cyberqhatu.app.core.network.GuardarEliminarFavoritosRequest
import com.cyberqhatu.app.core.network.ProductosFavoritosRequest
import com.cyberqhatu.app.core.network.SqliteCloudFunctionsClient

class SqliteCloudFavoritoRepository {

    private val apiService = SqliteCloudFunctionsClient.apiService

    suspend fun consultarFavoritosTiendas(): Boolean {
        return try {
            val response = apiService.consultarFavoritos(ConsultarFavoritosRequest(idKey = "fav_01"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando favoritos tiendas: ${e.message}", e)
            false
        }
    }

    suspend fun consultarProductosFavoritos(): Boolean {
        return try {
            val response = apiService.productosFavoritos(ProductosFavoritosRequest(idKey = "fav_01"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando productos favoritos: ${e.message}", e)
            false
        }
    }

    suspend fun guardarOEliminarFavorito(): Boolean {
        return try {
            val response = apiService.guardarEliminarFavoritos(GuardarEliminarFavoritosRequest(filter = "a"))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error en guardar_eliminar-favoritos: ${e.message}", e)
            false
        }
    }
}
