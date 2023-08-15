package com.github.jing332.alistandroid.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.jing332.alistandroid.app
import com.github.jing332.alistandroid.data.dao.ProviderCacheDao
import com.github.jing332.alistandroid.data.dao.ServerLogDao
import com.github.jing332.alistandroid.data.entities.ProviderCache
import com.github.jing332.alistandroid.data.entities.ServerLog

val appDb by lazy { AppDatabase.create() }

@Database(
    version = 2,
    entities = [ServerLog::class, ProviderCache::class],
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val serverLogDao: ServerLogDao
    abstract val providerCacheDao: ProviderCacheDao

    companion object {
        fun create() = Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "alistandroid.db"
        )
            .allowMainThreadQueries()
            .build()
    }
}