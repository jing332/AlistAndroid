package com.github.jing332.alistandroid.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.model.ShortCuts
import com.github.jing332.alistandroid.ui.MyTools.killBattery
import com.github.jing332.alistandroid.ui.nav.BottomNavBar
import com.github.jing332.alistandroid.ui.nav.NavigationGraph
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

val LocalMainViewModel = staticCompositionLocalOf<MainViewModel> {
    error("No MainViewModel provided")
}

class MainActivity : BaseComposeActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            ShortCuts.buildShortCuts(this@MainActivity)
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // A13
            val notificationPermission =
                rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            if (!notificationPermission.status.isGranted)
                LaunchedEffect(key1 = notificationPermission) {
                    notificationPermission.launchPermissionRequest()
                }
        }

        if (vm.showUpdateDialog != null) {
            val data = vm.showUpdateDialog ?: return
            AppUpdateDialog(
                onDismissRequest = { vm.showUpdateDialog = null },
                content = data.content,
                version = data.version,
                downloadUrl = data.downloadUrl,
            )
        }

        LaunchedEffect(vm.hashCode()) {
            vm.checkAppUpdate()
        }
        CompositionLocalProvider(
            LocalMainViewModel provides vm
        ) {

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
    }


}