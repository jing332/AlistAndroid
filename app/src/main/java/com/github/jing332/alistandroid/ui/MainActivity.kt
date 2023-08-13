package com.github.jing332.alistandroid.ui

import alistlib.Alistlib
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.model.AList
import com.github.jing332.alistandroid.service.AlistService
import com.github.jing332.alistandroid.util.ToastUtils.longToast
import splitties.systemservices.powerManager

@Suppress("DEPRECATION")
class MainActivity : BaseComposeActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private var running by mutableStateOf(AList.isRunning)
    private val mReceiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(mReceiver, IntentFilter(AList.ACTION_STATUS_CHANGED))

        killBattery()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver)
    }

    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == AList.ACTION_STATUS_CHANGED) {
                running = AList.isRunning
                findViewById<View>(android.R.id.content)?.let {
                    it.isHapticFeedbackEnabled = true
                    it.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                }
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val context = LocalContext.current
        val view = LocalView.current

        fun switch() {
            context.startService(Intent(context, AlistService::class.java).apply {
                action = if (running) AlistService.ACTION_SHUTDOWN else ""
            })
            running = !running
        }

        var showPwdDialog by remember { mutableStateOf(false) }
        if (showPwdDialog) {
            var pwd by remember { mutableStateOf("") }
            AlertDialog(onDismissRequest = { showPwdDialog = false },
                title = { Text(stringResource(R.string.admin_password)) },
                text = {
                    Column(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            value = pwd,
                            onValueChange = { pwd = it }
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = pwd.isNotBlank(),
                        onClick = {
                            showPwdDialog = false
                            if (AList.setAdminPassword(pwd))
                                longToast(R.string.admin_password_set_to, pwd)
                        }) {
                        Text(stringResource(id = R.string.ok))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPwdDialog = false }) {
                        Text(stringResource(id = R.string.cancel))
                    }
                })
        }

        Scaffold(modifier = Modifier.imePadding(),
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.alist_server)) },
                    actions = {
                        IconButton(onClick = {
                            MyTools.addShortcut(
                                context,
                                getString(R.string.alist_server),
                                "server",
                                R.drawable.server,
                                Intent(context, SwitchServerActivity::class.java)
                            )
                        }) {
                            Icon(
                                Icons.Default.AddBusiness,
                                stringResource(R.string.add_desktop_shortcut)
                            )
                        }

                        IconButton(onClick = { showPwdDialog = true }) {
                            Icon(
                                Icons.Default.Password,
                                stringResource(R.string.add_desktop_shortcut)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .padding(horizontal = 8.dp)
                    .padding(bottom = 16.dp)
            ) {
                ServerLogScreen(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )

                Column(
                    Modifier
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Row(Modifier.padding(top = 8.dp)) {
                        Switch(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(Alignment.CenterVertically),
                            checked = running,
                            onCheckedChange = { switch() },
                        )
                    }
                }
            }
        }
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