package com.github.jing332.alistandroid.ui.nav.settings

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import android.webkit.WebStorage
import android.webkit.WebView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.HdrAuto
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.SystemSecurityUpdateGood
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.ui.MyTools.isIgnoringBatteryOptimizations
import com.github.jing332.alistandroid.ui.MyTools.killBattery
import com.github.jing332.alistandroid.util.ToastUtils.toast
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@Suppress("DEPRECATION")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    Column(Modifier.statusBarsPadding()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DividerPreference {
                Text(stringResource(id = R.string.importent_settings))
            }

            AnimatedVisibility(visible = !context.isIgnoringBatteryOptimizations()) {
                BasePreferenceWidget(
                    onClick = { context.killBattery() },
                    title = {
                        Text(
                            stringResource(R.string.grant_battery_whiltelist),
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    subTitle = { Text(stringResource(R.string.grant_battery_whiltelist_desc)) }) {
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) { // A11
                var isGranted by remember { mutableStateOf(Environment.isExternalStorageManager()) }
                val permissionCheckerObserver = remember {
                    LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_RESUME) {
                            isGranted = Environment.isExternalStorageManager()
                        }
                    }
                }
                val lifecycle = LocalLifecycleOwner.current.lifecycle
                DisposableEffect(lifecycle, permissionCheckerObserver) {
                    lifecycle.addObserver(permissionCheckerObserver)
                    onDispose { lifecycle.removeObserver(permissionCheckerObserver) }
                }

                BasePreferenceWidget(
                    onClick = {
                        context.startActivity(Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                            setData(Uri.parse("package:${context.packageName}"))
                        })
                    },
                    title = {
                        Text(
                            stringResource(id = R.string.all_files_manage_permission),
                            color = if (isGranted) Color.Companion.Unspecified else MaterialTheme.colorScheme.error,
                        )
                    },
                    subTitle = { Text(stringResource(id = R.string.files_permission_desc)) },
                    content = {
                        Checkbox(enabled = false, checked = isGranted, onCheckedChange = {})
                    })

            } else { // < A11
                val readPermission =
                    rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
                AnimatedVisibility(visible = !readPermission.status.isGranted) {
                    BasePreferenceWidget(
                        onClick = { readPermission.launchPermissionRequest() },
                        title = {
                            Text(
                                stringResource(id = R.string.read_external_storage_permission),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        subTitle = { Text(stringResource(id = R.string.files_permission_desc)) }
                    )
                }

                val writePermission =
                    rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
                AnimatedVisibility(visible = !writePermission.status.isGranted) {
                    BasePreferenceWidget(
                        onClick = { writePermission.launchPermissionRequest() },
                        title = {
                            Text(
                                stringResource(id = R.string.write_external_storage_permission),
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        subTitle = { Text(stringResource(id = R.string.files_permission_desc)) }
                    )
                }
            }

        }

        DividerPreference { Text(text = stringResource(id = R.string.app_switch)) }

        var checkUpdate by remember { AppConfig.isAutoCheckUpdate }
        SwitchPreference(
            title = { Text(stringResource(R.string.auto_check_updates)) },
            subTitle = { Text(stringResource(R.string.auto_check_updates_desc)) },
            checked = checkUpdate,
            onCheckedChange = { checkUpdate = it },
            icon = { Icon(Icons.Default.ArrowCircleUp, contentDescription = null) }
        )

        var enabledWakeLock by remember { AppConfig.enabledWakeLock }
        SwitchPreference(
            title = { Text(stringResource(R.string.wake_lock)) },
            subTitle = { Text(stringResource(R.string.wake_lock_desc)) },
            checked = enabledWakeLock,
            onCheckedChange = { enabledWakeLock = it },
            icon = { Icon(Icons.Default.ScreenLockPortrait, contentDescription = null) }
        )

        var startAtBoot by remember { AppConfig.isStartAtBoot }
        SwitchPreference(
            title = { Text(stringResource(R.string.start_at_boot)) },
            subTitle = { Text(stringResource(R.string.start_at_boot_desc)) },
            checked = startAtBoot,
            onCheckedChange = { startAtBoot = it },
            icon = { Icon(Icons.Default.SystemSecurityUpdateGood, contentDescription = null) }
        )

        DividerPreference {
            Text(stringResource(id = R.string.web))
        }

        var autoOpenPage by remember { AppConfig.autoOpenWebPage }
        SwitchPreference(
            title = { Text(stringResource(R.string.auto_open_web)) },
            subTitle = { Text(stringResource(R.string.auto_open_web_desc)) },
            icon = { Icon(Icons.Default.HdrAuto, null) },
            checked = autoOpenPage
        ) { autoOpenPage = it }

        BasePreferenceWidget(
            onClick = {
                WebView(context).clearCache(true)
                context.toast(R.string.cleared)
            },
            icon = { Icon(Icons.Default.FilePresent, null) },
            title = { Text(stringResource(id = R.string.clear_web_cache)) },
            subTitle = { Text(stringResource(id = R.string.clear_web_cache_desc)) }
        )

        var showClearDataMenu by remember { mutableStateOf(false) }
        BasePreferenceWidget(
            onClick = { showClearDataMenu = true },
            icon = { Icon(Icons.Default.SupervisorAccount, null) },
            title = { Text(stringResource(id = R.string.clear_web_data)) }, subTitle = {
                Text(stringResource(id = R.string.clear_web_data_desc))
            }, content = {
                DropdownMenu(
                    expanded = showClearDataMenu,
                    onDismissRequest = { showClearDataMenu = false }) {
                    DropdownMenuItem(text = {
                        Text(
                            stringResource(R.string.confirm),
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }, onClick = {
                        showClearDataMenu = false

                        context.deleteDatabase("webview.db")
                        context.deleteDatabase("webviewCache.db")

                        CookieSyncManager.createInstance(context)
                        val cookieManager = CookieManager.getInstance()
                        cookieManager.removeSessionCookies(null)
                        cookieManager.removeAllCookie()
                        cookieManager.flush()

                        WebStorage.getInstance().deleteAllData()

                        context.toast(R.string.cleared)
                    })
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.cancel)) },
                        onClick = { showClearDataMenu = false })
                }
            }
        )
    }
}