package com.github.jing332.alistandroid.service

import alistlib.Alistlib
import alistlib.LogCallback
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.github.jing332.alistandroid.util.ToastUtils.longToast

class AlistService : Service() {
    companion object {
        const val TAG = "AlistService"
        const val ACTION_SHUTDOWN =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_SHUTDOWN"

        init {
//            Alistlib.init { level, msg -> Log.i(TAG, "level=${level}, msg={$msg}") }
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Alistlib.start()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_SHUTDOWN) {
            runCatching {
                Alistlib.shutdown(5000)
            }.onFailure {
                longToast("Shutdown failed: ${it.message}")
            }

        }

        return super.onStartCommand(intent, flags, startId)
    }
}