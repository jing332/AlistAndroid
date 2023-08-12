package com.github.jing332.alistandroid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidlib.Androidlib
import androidlib.LogCallback
import com.github.jing332.alistandroid.util.ToastUtils.longToast

class AlistService : Service() {
    companion object {
        const val ACTION_SHUTDOWN =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_SHUTDOWN"

        init {
            Androidlib.init(object : LogCallback {
            })
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Androidlib.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_SHUTDOWN) {
            runCatching {
                Androidlib.shutdown(5000)
            }.onFailure {
                longToast("Shutdown failed: ${it.message}")
            }

        }

        return super.onStartCommand(intent, flags, startId)
    }
}