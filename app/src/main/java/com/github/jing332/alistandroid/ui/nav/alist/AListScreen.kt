package com.github.jing332.alistandroid.ui.nav.alist

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
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
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.model.AList
import com.github.jing332.alistandroid.service.AlistService
import com.github.jing332.alistandroid.ui.MyTools
import com.github.jing332.alistandroid.ui.SwitchServerActivity
import com.github.jing332.alistandroid.ui.widgets.LocalBroadcastReceiver
import com.github.jing332.alistandroid.util.ToastUtils.longToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AListScreen() {
    val context = LocalContext.current
    val view = LocalView.current
    var alistRunning by remember { mutableStateOf(AList.hasRunning) }
    println(alistRunning)

    LocalBroadcastReceiver(intentFilter = IntentFilter(AList.ACTION_STATUS_CHANGED)) {
        println(it?.action)
        if (it?.action == AList.ACTION_STATUS_CHANGED) {
            alistRunning = AList.hasRunning
        }
    }

    fun switch() {
        context.startService(Intent(context, AlistService::class.java).apply {
            action = if (alistRunning) AlistService.ACTION_SHUTDOWN else ""
        })
//        alistRunning = !alistRunning
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
                        label = { Text(stringResource(id = R.string.password)) },
                        onValueChange = { pwd = it },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.titleMedium
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = pwd.isNotBlank(),
                    onClick = {
                        showPwdDialog = false
                        AList.setAdminPassword(pwd)
                        context.longToast(R.string.admin_password_set_to, pwd)
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
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = {
                        MyTools.addShortcut(
                            context,
                            context.getString(R.string.alist_server),
                            "alist_switch",
                            R.drawable.alist_switch,
                            Intent(context, SwitchServerActivity::class.java)
                        )
                    }) {
                        Icon(
                            Icons.Default.AddBusiness,
                            stringResource(R.string.add_desktop_shortcut)
                        )
                    }

                    IconButton(onClick = {
                        showPwdDialog = true
                    }) {
                        Icon(
                            Icons.Default.Password,
                            stringResource(R.string.admin_password)
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
                Switch(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    checked = alistRunning,
                    onCheckedChange = { switch() },
                )
            }
        }
    }
}