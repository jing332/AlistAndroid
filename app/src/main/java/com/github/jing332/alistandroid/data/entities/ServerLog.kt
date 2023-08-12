package com.github.jing332.alistandroid.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.jing332.alistandroid.constant.LogLevel

@Entity("server_logs")
data class ServerLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @LogLevel val level: Int,
    val message: String,
    val description: String? = null,
)