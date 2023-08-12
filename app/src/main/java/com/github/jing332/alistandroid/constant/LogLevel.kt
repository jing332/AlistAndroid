package com.github.jing332.alistandroid.constant

import androidx.annotation.IntDef

@IntDef(LogLevel.DEBUG, LogLevel.INFO, LogLevel.WARN, LogLevel.ERROR)
annotation class LogLevel {
    companion object {
        const val DEBUG = 0
        const val INFO = 1
        const val WARN = 2
        const val ERROR = 3
    }
}