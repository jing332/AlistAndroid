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
) {
    companion object {

        @Suppress("RegExpRedundantEscape")
        fun String.evalLog(): ServerLog? {
            val logPattern = """(\w+)\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2})\] (.+)""".toRegex()
            val result = logPattern.find(this)
            if (result != null) {
                val (level, time, msg) = result.destructured
                val l = when (level[0].toString()) {
                    "D" -> LogLevel.DEBUG
                    "I" -> LogLevel.INFO
                    "W" -> LogLevel.WARN
                    "E" -> LogLevel.ERROR
                    else -> LogLevel.INFO
                }
                return ServerLog(level = l, message = msg, description = time)
            }
            return null
        }
    }
}
