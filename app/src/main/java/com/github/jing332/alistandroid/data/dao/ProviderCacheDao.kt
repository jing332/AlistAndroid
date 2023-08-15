package com.github.jing332.alistandroid.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jing332.alistandroid.data.entities.ProviderCache
import kotlinx.coroutines.flow.Flow

@Dao
interface ProviderCacheDao {
    @Query("SELECT * FROM provider_caches")
    fun flowAll(): Flow<List<ProviderCache>>

    @Query("SELECT * FROM provider_caches")
    fun all(): List<ProviderCache>

    @Query("DELETE FROM provider_caches")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg log: ProviderCache)
}