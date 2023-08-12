package com.github.jing332.alistandroid

import android.app.Application
import android.content.Intent
import android.os.Looper
import com.github.jing332.alistandroid.service.AlistService
import java.net.InetAddress
import java.net.UnknownHostException

val app by lazy { App.application }

class App : Application() {
    // android.app.Application
    override fun onCreate() {
        super.onCreate()
        application = this

        CrashHandler(this)

        startService(Intent(this, AlistService::class.java))
    }

    companion object {
        lateinit var application: Application

        fun getByName(ip: String?): InetAddress? {
            return try {
                InetAddress.getByName(ip)
            } catch (unused: UnknownHostException) {
                null
            }
        }

        val isMainThread: Boolean
            get() = Looper.getMainLooper().thread.id == Thread.currentThread().id
    }
}