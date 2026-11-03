package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.confianza.FuenteResena
import com.cyberqhatu.app.confianza.Resena
import com.cyberqhatu.app.core.network.SqliteCloudSql
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date
import java.util.UUID

class SqliteCloudResenaRepository {

    private fun e(s: String?) = SqliteCloudSql.esc(s)

    suspend fun guardarResenaRemota(idTienda: String, idUsuario: String, rating: Int, comentario: String): Boolean {
        return try {
            val idResena = "res-" + UUID.randomUUID().toString().take(8)
            val sql = "INSERT OR REPLACE INTO resenas (id_resena, id_tienda, id_usuario, rating, comentario, fecha) VALUES ('$idResena', '${e(idTienda)}', '${e(idUsuario)}', $rating, '${e(comentario)}', CURRENT_TIMESTAMP);"
            SqliteCloudSql.ejecutar(sql)
            true
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando reseña en BD: ${e.message}", e)
            false
        }
    }

    fun obtenerResenasDeTienda(idTienda: String): Flow<List<Resena>> = flow {
        try {
            val sql = "SELECT id_resena, id_tienda, id_usuario, rating, comentario, fecha FROM resenas WHERE id_tienda = '${e(idTienda)}' ORDER BY fecha DESC;"
            val dataList = SqliteCloudSql.ejecutar(sql)
            if (dataList.isNotEmpty()) {
                val lista = dataList.mapNotNull { row ->
                    val idRes = row["id_resena"]?.toString() ?: return@mapNotNull null
                    val idT = row["id_tienda"]?.toString() ?: idTienda
                    val idU = row["id_usuario"]?.toString() ?: "usuario@cyberqhatu.bo"
                    val rat = (row["rating"] as? Number)?.toInt() ?: 5
                    val com = row["comentario"]?.toString() ?: ""
                    
                    Resena(
                        idResena = idRes,
                        idTienda = idT,
                        idUsuario = idU,
                        rating = rat,
                        comentario = com,
                        source = FuenteResena.DIRECT_SEARCH,
                        isVerified = true,
                        fechaCreacion = Date()
                    )
                }
                emit(lista)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando reseñas de tienda: ${e.message}", e)
            emit(emptyList())
        }
    }
}
