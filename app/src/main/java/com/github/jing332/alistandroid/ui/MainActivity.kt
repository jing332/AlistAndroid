package com.github.jing332.alistandroid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.compose.runtime.Composable
import splitties.systemservices.powerManager

class MainActivity : BaseComposeActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        killBattery()
    }

    @Composable
    override fun Content() {

    }


    @SuppressLint("BatteryLife")
    private fun killBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
//                toast(R.string.added_background_whitelist)
            } else {
                kotlin.runCatching {
                    startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    })
                }.onFailure {
//                    toast(R.string.system_not_support_please_manual_set)
                }
            }
        }
    }
}