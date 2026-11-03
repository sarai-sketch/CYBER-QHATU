package com.cyberqhatu.app.feature.tienda.data.repository

import android.util.Log
import com.cyberqhatu.app.core.network.SqliteCloudSql
import com.cyberqhatu.app.core.session.SesionActual
import com.cyberqhatu.app.tienda.FavoritoProducto
import com.cyberqhatu.app.tienda.FavoritoTienda
import com.cyberqhatu.app.tienda.Producto
import com.cyberqhatu.app.tienda.Tienda
import com.cyberqhatu.app.ui.screens.tienda.DomainMockData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FavProductoRow(val idProducto: String, val nombre: String = "Producto Favorito", val precio: Double = 0.0)
data class FavTiendaRow(val idTienda: String, val nombre: String = "Tienda Favorita", val descripcion: String = "Comercio local")

/** Favoritos guardados en SQLite Cloud utilizando las tablas reales `favoritos_productos` y `favoritos_tiendas` con JOINs. */
class SqliteCloudFavoritoRepository {

    private fun e(s: String?) = SqliteCloudSql.esc(s)

    suspend fun obtenerProductos(idUsuario: String): List<FavProductoRow> {
        return try {
            val sql = "SELECT f.id_producto AS id_producto, p.nombre AS nombre, p.precio AS precio FROM favoritos_productos f LEFT JOIN productos p ON p.id_producto = f.id_producto WHERE f.id_usuario = '${e(idUsuario)}' ORDER BY f.fecha_agregado DESC;"
            val filas = SqliteCloudSql.ejecutar(sql)
            filas.mapNotNull { f ->
                val id = f["id_producto"]?.toString() ?: return@mapNotNull null
                FavProductoRow(
                    idProducto = id,
                    nombre = f["nombre"]?.toString() ?: "Producto Verificado",
                    precio = f["precio"]?.toString()?.toDoubleOrNull() ?: 0.0
                )
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error obteniendo productos favoritos: ${e.message}")
            emptyList()
        }
    }

    suspend fun obtenerTiendas(idUsuario: String): List<FavTiendaRow> {
        return try {
            val sql = "SELECT f.id_tienda AS id_tienda, t.nombre AS nombre, t.descripcion AS descripcion FROM favoritos_tiendas f LEFT JOIN tiendas t ON t.id_tienda = f.id_tienda WHERE f.id_usuario = '${e(idUsuario)}' ORDER BY f.fecha_agregado DESC;"
            val filas = SqliteCloudSql.ejecutar(sql)
            filas.mapNotNull { f ->
                val id = f["id_tienda"]?.toString() ?: return@mapNotNull null
                FavTiendaRow(
                    idTienda = id,
                    nombre = f["nombre"]?.toString() ?: "Puesto Verificado",
                    descripcion = f["descripcion"]?.toString() ?: "Comercio local en feria"
                )
            }
        } catch (e: Exception) {
            Log.e("SqliteCloud", "Error obteniendo tiendas favoritas: ${e.message}")
            emptyList()
        }
    }

    suspend fun guardarProducto(idUsuario: String, idProducto: String) {
        val idFav = "${idUsuario}|$idProducto"
        SqliteCloudSql.ejecutar(
            "INSERT OR REPLACE INTO favoritos_productos (id_favorito, id_usuario, id_producto) " +
                "VALUES ('${e(idFav)}', '${e(idUsuario)}', '${e(idProducto)}');"
        )
    }

    suspend fun eliminarProducto(idUsuario: String, idProducto: String) {
        SqliteCloudSql.ejecutar(
            "DELETE FROM favoritos_productos WHERE id_usuario = '${e(idUsuario)}' AND id_producto = '${e(idProducto)}';"
        )
    }

    suspend fun guardarTienda(idUsuario: String, idTienda: String) {
        val idFav = "${idUsuario}|$idTienda"
        SqliteCloudSql.ejecutar(
            "INSERT OR REPLACE INTO favoritos_tiendas (id_favorito, id_usuario, id_tienda) " +
                "VALUES ('${e(idFav)}', '${e(idUsuario)}', '${e(idTienda)}');"
        )
    }

    suspend fun eliminarTienda(idUsuario: String, idTienda: String) {
        SqliteCloudSql.ejecutar(
            "DELETE FROM favoritos_tiendas WHERE id_usuario = '${e(idUsuario)}' AND id_tienda = '${e(idTienda)}';"
        )
    }
}

/**
 * Puente entre los corazones de la UI y la BD.
 */
object FavoritosSync {
    private const val TAG = "FavoritosSync"
    private val repo = SqliteCloudFavoritoRepository()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    fun cargarDesdeNube(idUsuario: String) {
        scope.launch {
            try {
                val (prods, tiendas) = withContext(Dispatchers.IO) {
                    repo.obtenerProductos(idUsuario) to repo.obtenerTiendas(idUsuario)
                }
                DomainMockData.favoritosProductos.clear()
                DomainMockData.favoritosProductos.addAll(prods.map {
                    FavoritoProducto(idFavorito = "$idUsuario|${it.idProducto}", idUsuario = idUsuario, idProducto = it.idProducto)
                })
                DomainMockData.favoritosTiendas.clear()
                DomainMockData.favoritosTiendas.addAll(tiendas.map {
                    FavoritoTienda(idFavorito = "$idUsuario|${it.idTienda}", idUsuario = idUsuario, idTienda = it.idTienda)
                })
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                Log.e(TAG, "No se pudieron cargar los favoritos: ${ex.message}", ex)
            }
        }
    }

    fun limpiar() {
        DomainMockData.favoritosProductos.clear()
        DomainMockData.favoritosTiendas.clear()
    }

    fun setProducto(producto: Producto, esFavorito: Boolean) {
        val idUsuario = SesionActual.idUsuario ?: return
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (esFavorito) repo.guardarProducto(idUsuario, producto.idProducto)
                    else repo.eliminarProducto(idUsuario, producto.idProducto)
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                Log.e(TAG, "Fallo guardando favorito de producto ${producto.idProducto}: ${ex.message}", ex)
                if (esFavorito) {
                    DomainMockData.favoritosProductos.removeAll { it.idProducto == producto.idProducto }
                } else if (DomainMockData.favoritosProductos.none { it.idProducto == producto.idProducto }) {
                    DomainMockData.favoritosProductos.add(
                        FavoritoProducto(idFavorito = "$idUsuario|${producto.idProducto}", idUsuario = idUsuario, idProducto = producto.idProducto)
                    )
                }
            }
        }
    }

    fun setTienda(tienda: Tienda, esFavorito: Boolean) {
        val idUsuario = SesionActual.idUsuario ?: return
        scope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (esFavorito) repo.guardarTienda(idUsuario, tienda.idTienda)
                    else repo.eliminarTienda(idUsuario, tienda.idTienda)
                }
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                Log.e(TAG, "Fallo guardando favorito de tienda ${tienda.idTienda}: ${ex.message}", ex)
                if (esFavorito) {
                    DomainMockData.favoritosTiendas.removeAll { it.idTienda == tienda.idTienda }
                } else if (DomainMockData.favoritosTiendas.none { it.idTienda == tienda.idTienda }) {
                    DomainMockData.favoritosTiendas.add(
                        FavoritoTienda(idFavorito = "$idUsuario|${tienda.idTienda}", idUsuario = idUsuario, idTienda = tienda.idTienda)
                    )
                }
            }
        }
    }
}
