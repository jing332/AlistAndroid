package com.github.jing332.alistandroid.model

import alistlib.Alistlib
import alistlib.Event
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.app
import com.github.jing332.alistandroid.constant.LogLevel
import com.github.jing332.alistandroid.data.appDb
import com.github.jing332.alistandroid.data.entities.ServerLog
import com.github.jing332.alistandroid.service.AlistService
import com.github.jing332.alistandroid.util.ToastUtils.longToast

object AList {
    const val ACTION_STATUS_CHANGED =
        "com.github.jing332.alistandroid.AList.ACTION_STATUS_CHANGED"

    val context = app
    var isRunning = false

    fun init() {
        Alistlib.init(object : Event {
            override fun onShutdown(type: String) {
                updateStatus()
            }

            override fun onStartError(type: String, msg: String) {
                appDb.serverLogDao.insert(
                    ServerLog(
                        level = LogLevel.ERROR,
                        message = "${type}: $msg"
                    )
                )
                updateStatus()
            }

        }) { level, msg ->
            Log.i(AlistService.TAG, "level=${level}, msg=$msg")
            appDb.serverLogDao.insert(ServerLog(level = level.toInt(), message = msg))
        }
    }

    fun setAdminPassword(pwd: String): Boolean {
        return if (isRunning) {
            Alistlib.setAdminPassword(pwd)
            true
        } else {
            context.longToast(R.string.set_admin_pwd_for_not_running)
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun updateStatus() {
        isRunning = Alistlib.isRunning("")
        LocalBroadcastManager.getInstance(context)
            .sendBroadcast(Intent(ACTION_STATUS_CHANGED))
//        if (!isRunning) stopSelf()
    }

    fun shutdown(timeout: Long = 5000L) {
        runCatching {
            Alistlib.shutdown(timeout)
        }.onFailure {
            context.longToast(R.string.server_shutdown_failed, it.toString())
        }
    }

    private val mDataPath by lazy {
        context.getExternalFilesDir("data")!!.absolutePath
    }

    fun startup() {
        if (Alistlib.isRunning("")){
            context.longToast("服务已在运行中")
            return
        }
        appDb.serverLogDao.deleteAll()

        Alistlib.setConfigData(mDataPath)
//            Alistlib.setConfigDebug(BuildConfig.DEBUG)
        Alistlib.setConfigLogStd(true)

        init()
        Alistlib.start()
    }
}