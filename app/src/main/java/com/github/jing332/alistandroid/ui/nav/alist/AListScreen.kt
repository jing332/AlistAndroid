package com.github.jing332.alistandroid.ui.nav.alist

import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddBusiness
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jing332.alistandroid.BuildConfig
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.model.alist.AList
import com.github.jing332.alistandroid.service.AListService
import com.github.jing332.alistandroid.service.AListService.Companion.ACTION_STATUS_CHANGED
import com.github.jing332.alistandroid.ui.LocalMainViewModel
import com.github.jing332.alistandroid.ui.MyTools
import com.github.jing332.alistandroid.ui.SwitchServerActivity
import com.github.jing332.alistandroid.ui.widgets.LocalBroadcastReceiver
import com.github.jing332.alistandroid.util.ToastUtils.longToast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AListScreen() {
    val context = LocalContext.current
    val mainVM = LocalMainViewModel.current
    val view = LocalView.current
    var alistRunning by remember { mutableStateOf(AListService.isRunning) }

    LocalBroadcastReceiver(intentFilter = IntentFilter(ACTION_STATUS_CHANGED)) {
        if (it?.action == ACTION_STATUS_CHANGED)
            alistRunning = AListService.isRunning
    }

    fun switch() {
        context.startService(Intent(context, AListService::class.java).apply {
            action = if (alistRunning) AListService.ACTION_SHUTDOWN else ""
        })
        alistRunning = !alistRunning
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
                        context.longToast(
                            R.string.admin_password_set_to,
                            pwd
                        )
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
    var showMoreOptions by remember { mutableStateOf(false) }

    var showAboutDialog by remember { mutableStateOf(false) }
    if (showAboutDialog) {
        AboutDialog {
            showAboutDialog = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Text(stringResource(R.string.app_name))
                        Text(" - " + BuildConfig.ALIST_VERSION)
                    }
                },
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

                    IconButton(onClick = {
                        showMoreOptions = true
                    }) {
                        DropdownMenu(
                            expanded = showMoreOptions,
                            onDismissRequest = { showMoreOptions = false }) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.check_update)) },
                                onClick = {
                                    showMoreOptions = false
                                    mainVM.checkAppUpdate()
                                }
                            )

                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.about)) },
                                onClick = {
                                    showMoreOptions = false
                                    showAboutDialog = true
                                }
                            )
                        }
                        Icon(Icons.Default.MoreVert, stringResource(R.string.more_options))
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 8.dp)
        ) {
            ServerLogScreen(
                modifier = Modifier
                    .fillMaxSize()
            )

            SwitchFloatingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                switch = alistRunning
            ) {
                switch()
            }
        }
    }
}

@Composable
fun SwitchFloatingButton(modifier: Modifier, switch: Boolean, onSwitchChange: (Boolean) -> Unit) {
    val targetIcon =
        if (switch) Icons.Filled.Stop else Icons.AutoMirrored.Filled.Send
    val rotationAngle by animateFloatAsState(targetValue = if (switch) 360f else 0f, label = "")

    val color =
        animateColorAsState(
            targetValue = if (switch) MaterialTheme.colorScheme.inversePrimary else MaterialTheme.colorScheme.primaryContainer,
            label = "",
            animationSpec = tween(500, 0, LinearEasing)
        )

    FloatingActionButton(
        modifier = modifier,
        elevation = FloatingActionButtonDefaults.elevation(8.dp),
        shape = CircleShape,
        containerColor = color.value,
        onClick = { onSwitchChange(!switch) }) {

        Crossfade(targetState = targetIcon, label = "") {
            Icon(
                imageVector = it,
                contentDescription = stringResource(id = if (switch) R.string.shutdown else R.string.start),
                modifier = Modifier
                    .rotate(rotationAngle)
                    .graphicsLayer {
                        rotationZ = rotationAngle
                    }
                    .size(if (switch) 42.dp else 32.dp)
            )
        }

    }
}