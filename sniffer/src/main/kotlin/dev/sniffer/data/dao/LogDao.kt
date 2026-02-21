package dev.sniffer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sniffer.data.entity.LogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogDao {

    @Insert
    suspend fun insert(entity: LogEntity): Long

    @Query("SELECT * FROM logs ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<LogEntity>>

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<LogEntity>>

    @Query("DELETE FROM logs")
    suspend fun clearAll()
}
