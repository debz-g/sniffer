package dev.sniffer.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import dev.sniffer.data.entity.MockEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MockDao {

    @Insert
    suspend fun insert(entity: MockEntity): Long

    @Update
    suspend fun update(entity: MockEntity)

    @Query("SELECT * FROM mocks WHERE enabled = 1")
    fun observeEnabledMocks(): Flow<List<MockEntity>>

    @Query("SELECT * FROM mocks")
    fun observeAllMocks(): Flow<List<MockEntity>>

    @Query("SELECT * FROM mocks WHERE enabled = 1")
    suspend fun getEnabledMocks(): List<MockEntity>

    @Query("UPDATE mocks SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: Long, enabled: Boolean)

    @Query("DELETE FROM mocks WHERE id = :id")
    suspend fun deleteById(id: Long)
}
