package com.github.jing332.alistandroid.constant

import androidx.annotation.IntDef

@IntDef(
    LogLevel.PANIC,
    LogLevel.FATAL,
    LogLevel.ERROR,
    LogLevel.WARN,
    LogLevel.INFO,
    LogLevel.DEBUG,
    LogLevel.TRACE
)
annotation class LogLevel {
    companion object {
        const val PANIC = 1
        const val FATAL = 2
        const val ERROR = 3
        const val WARN = 4
        const val INFO = 5
        const val DEBUG = 6
        const val TRACE = 7

        fun Int.toLevelString(): String {
            return when (this) {
                PANIC -> "PANIC"
                FATAL -> "FATAL"
                ERROR -> "ERROR"
                WARN -> "WARN"
                INFO -> "INFO"
                DEBUG -> "DEBUG"
                TRACE -> "TRACE"
                else -> "UNKNOWN"
            }
        }
    }

}