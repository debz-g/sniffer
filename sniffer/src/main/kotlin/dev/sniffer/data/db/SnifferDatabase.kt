package dev.sniffer.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dev.sniffer.data.dao.LogDao
import dev.sniffer.data.dao.MockDao
import dev.sniffer.data.dao.NetworkCallDao
import dev.sniffer.data.entity.LogEntity
import dev.sniffer.data.entity.MockEntity
import dev.sniffer.data.entity.NetworkCallEntity

@Database(
    entities = [
        NetworkCallEntity::class,
        LogEntity::class,
        MockEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SnifferDatabase : RoomDatabase() {
    abstract fun networkCallDao(): NetworkCallDao
    abstract fun logDao(): LogDao
    abstract fun mockDao(): MockDao

    companion object {
        @Volatile
        private var INSTANCE: SnifferDatabase? = null

        fun get(context: Context): SnifferDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SnifferDatabase::class.java,
                    "sniffer_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
