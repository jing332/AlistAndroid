package com.github.jing332.alistandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.service.AListService

class BootStartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && AppConfig.isStartAtBoot.value) {
            context.startService(Intent(context, AListService::class.java))
        }
    }
}