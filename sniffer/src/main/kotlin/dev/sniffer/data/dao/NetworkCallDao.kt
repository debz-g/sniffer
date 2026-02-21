package dev.sniffer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import dev.sniffer.data.entity.NetworkCallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkCallDao {

    @Insert
    suspend fun insert(entity: NetworkCallEntity): Long

    @Query("SELECT * FROM network_calls ORDER BY timestamp DESC")
    fun observeAll(): Flow<List<NetworkCallEntity>>

    @Query("SELECT * FROM network_calls ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<NetworkCallEntity>>

    @Query("DELETE FROM network_calls")
    suspend fun clearAll()
}
