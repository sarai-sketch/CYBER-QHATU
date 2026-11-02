package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudClient
import com.cyberqhatu.app.core.network.SqliteCloudQueryRequest
import com.cyberqhatu.app.tienda.Categoria
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SqliteCloudCategoriaRepository {

    private val apiService = SqliteCloudClient.apiService

    suspend fun guardarCategoriaRemota(categoria: Categoria): Boolean {
        return try {
            val idPadreStr = if (categoria.idCategoriaPadre != null) "'${categoria.idCategoriaPadre}'" else "NULL"
            
            // Reemplazar comillas simples para evitar inyección SQL básica y errores de sintaxis
            val nombreLimpio = categoria.nombre.replace("'", "''")

            val sql = """
                INSERT OR REPLACE INTO categorias (id_categoria, nombre, id_categoria_padre) 
                VALUES ('${categoria.idCategoria}', '$nombreLimpio', $idPadreStr);
            """.trimIndent()
            
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error guardando categoría remota: ${e.message}", e)
            false
        }
    }

    fun obtenerCategoriasRemotas(): Flow<List<Categoria>> = flow {
        try {
            val sql = "SELECT id_categoria, nombre, id_categoria_padre FROM categorias;"
            val response = apiService.ejecutarSql(SqliteCloudQueryRequest(sql = sql))

            if (response.isSuccessful && response.body()?.data != null) {
                val lista = response.body()!!.data!!.mapNotNull { row ->
                    val idCategoria = row["id_categoria"]?.toString() ?: return@mapNotNull null
                    val nombre = row["nombre"]?.toString() ?: "Categoría"
                    val idPadre = row["id_categoria_padre"]?.toString()

                    Categoria(
                        idCategoria = idCategoria,
                        nombre = nombre,
                        idCategoriaPadre = idPadre
                    )
                }
                emit(lista)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error consultando categorías remotas: ${e.message}", e)
            emit(emptyList())
        }
    }
}
