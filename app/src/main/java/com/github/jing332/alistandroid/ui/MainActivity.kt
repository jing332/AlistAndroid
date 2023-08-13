package com.github.jing332.alistandroid.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.github.jing332.alistandroid.ui.nav.BottomNavBar
import com.github.jing332.alistandroid.ui.nav.NavigationGraph
import splitties.systemservices.powerManager

@Suppress("DEPRECATION")
class MainActivity : BaseComposeActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        killBattery()
    }


    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = {
                BottomNavBar(navController)
            }
        ) {
            NavigationGraph(
                navController = navController,
                Modifier.padding(bottom = it.calculateBottomPadding())
            )
        }
    }


    @SuppressLint("BatteryLife")
    private fun killBattery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                kotlin.runCatching {
                    startActivity(Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    })
                }
            }
        }
    }
}