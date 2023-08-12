package com.github.jing332.alistandroid.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.github.jing332.alistandroid.app
import com.github.jing332.alistandroid.data.dao.ServerLogDao
import com.github.jing332.alistandroid.data.entities.ServerLog

val appDb by lazy { AppDatabase.create() }

@Database(
    version = 1,
    entities = [ServerLog::class],
    autoMigrations = [

    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract val serverLogDao: ServerLogDao

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