package com.github.jing332.alistandroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jing332.alistandroid.data.entities.ServerLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerLogDao {
    @Query("SELECT * FROM server_logs")
    fun flowAll(): Flow<List<ServerLog>>

    @Query("SELECT * FROM server_logs")
    fun all(): List<ServerLog>

    @Query("DELETE FROM server_logs")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg log: ServerLog)
}