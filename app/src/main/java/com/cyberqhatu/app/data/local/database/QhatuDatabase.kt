package com.cyberqhatu.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyberqhatu.app.data.local.dao.ProductoDao
import com.cyberqhatu.app.data.local.dao.VerificacionCacheDao
import com.cyberqhatu.app.data.local.dao.VerificacionDao
import com.cyberqhatu.app.data.local.entity.MovimientoInventarioEntity
import com.cyberqhatu.app.data.local.entity.ProductoEntity
import com.cyberqhatu.app.data.local.entity.VerificacionCacheEntity
import com.cyberqhatu.app.data.local.entity.VerificacionEntity

/**
 * Base de datos principal de Room con soporte para Caché Local e Inventario.
 */
@Database(
    entities = [
        VerificacionEntity::class,
        VerificacionCacheEntity::class,
        ProductoEntity::class,
        MovimientoInventarioEntity::class
    ],
    version = 3,
    exportSchema = false,
)
abstract class QhatuDatabase : RoomDatabase() {

    abstract fun verificacionDao(): VerificacionDao
    abstract fun verificacionCacheDao(): VerificacionCacheDao
    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCE: QhatuDatabase? = null

        fun getInstance(context: Context): QhatuDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    QhatuDatabase::class.java,
                    "qhatu_database.db",
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
