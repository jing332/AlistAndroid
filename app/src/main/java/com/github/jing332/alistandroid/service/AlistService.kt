package com.github.jing332.alistandroid.service

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AlistService() : Service() {
    companion object {
        const val ACTION_SHUTDOWN =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_SHUTDOWN"
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if (intent.action == ACTION_SHUTDOWN){

        }

        return super.onStartCommand(intent, flags, startId)
    }
}