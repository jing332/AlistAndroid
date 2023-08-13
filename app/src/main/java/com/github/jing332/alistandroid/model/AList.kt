package com.github.jing332.alistandroid.model

import alistlib.Alistlib
import alistlib.Event
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.app
import com.github.jing332.alistandroid.constant.AppConst
import com.github.jing332.alistandroid.constant.LogLevel
import com.github.jing332.alistandroid.data.appDb
import com.github.jing332.alistandroid.data.entities.ServerLog
import com.github.jing332.alistandroid.service.AlistService
import com.github.jing332.alistandroid.util.ToastUtils.longToast
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import java.io.File

object AList {
    const val ACTION_STATUS_CHANGED =
        "com.github.jing332.alistandroid.AList.ACTION_STATUS_CHANGED"

    const val TYPE_HTTP = "http"
    const val TYPE_HTTPS = "https"
    const val TYPE_UNIX = "unix"

    val context = app

    val dataPath: String
        get() = context.getExternalFilesDir("data")?.absolutePath!!

    val configPath: String
        get() = "$dataPath${File.separator}config.json"

    /**
     * 是否有服务正在运行
     */
    val hasRunning: Boolean
        get() = when {
            Alistlib.isRunning(TYPE_HTTP) -> true
            Alistlib.isRunning(TYPE_HTTPS) -> true
            Alistlib.isRunning(TYPE_UNIX) -> true
            else -> false
        }

    fun init() {
        Alistlib.setConfigData(dataPath)
//            Alistlib.setConfigDebug(BuildConfig.DEBUG)
        Alistlib.setConfigLogStd(true)

        Alistlib.init(object : Event {
            override fun onShutdown(type: String) {
                notifyStatusChanged()
            }

            override fun onStartError(type: String, msg: String) {
                appDb.serverLogDao.insert(
                    ServerLog(
                        level = LogLevel.ERROR,
                        message = "${type}: $msg"
                    )
                )
                notifyStatusChanged()
            }

        }) { level, msg ->
            Log.i(AlistService.TAG, "level=${level}, msg=$msg")
            appDb.serverLogDao.insert(ServerLog(level = level.toInt(), message = msg))
        }
    }

    fun setAdminPassword(pwd: String) {
        if (!hasRunning) {
            init()
        }

        Alistlib.setAdminPassword(pwd)
    }

    @Suppress("DEPRECATION")
    private fun notifyStatusChanged() {
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(ACTION_STATUS_CHANGED))
    }

    fun shutdown(timeout: Long = 5000L) {
        runCatching {
            Alistlib.shutdown(timeout)
        }.onFailure {
            context.longToast(R.string.server_shutdown_failed, it.toString())
        }
    }


    fun startup() {
        if (Alistlib.isRunning("")) {
            context.longToast("服务已在运行中")
            return
        }
        appDb.serverLogDao.deleteAll()

        init()
        Alistlib.start()
        notifyStatusChanged()
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun config(): AListConfig {
        try {
            File(configPath).inputStream().use {
                return AppConst.json.decodeFromStream<AListConfig>(it)
            }
        } catch (e: Exception) {
            context.longToast("读取config.json失败：$e")
            return AListConfig()
        }
    }
}