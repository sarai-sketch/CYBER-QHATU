package com.cyberqhatu.app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cyberqhatu.app.data.local.dao.AuthCacheDao
import com.cyberqhatu.app.data.local.dao.UserServerDao
import com.cyberqhatu.app.data.local.dao.VerificacionCacheDao
import com.cyberqhatu.app.data.local.dao.VerificacionDao
import com.cyberqhatu.app.data.local.entity.AuthCacheEntity
import com.cyberqhatu.app.data.local.entity.UserServerEntity
import com.cyberqhatu.app.data.local.entity.VerificacionCacheEntity
import com.cyberqhatu.app.data.local.entity.VerificacionEntity

/**
 * Base de datos principal de Room con soporte para Caché Local Offline-First.
 */
@Database(
    entities = [VerificacionEntity::class, VerificacionCacheEntity::class, AuthCacheEntity::class, UserServerEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class QhatuDatabase : RoomDatabase() {

    abstract fun verificacionDao(): VerificacionDao
    abstract fun verificacionCacheDao(): VerificacionCacheDao
    abstract fun authCacheDao(): AuthCacheDao
    abstract fun userServerDao(): UserServerDao

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
                    // TODO: migraciones reales antes de publicar
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
