package com.github.jing332.alistandroid.ui.nav.config

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.constant.AppConst
import com.github.jing332.alistandroid.model.alist.AList
import com.github.jing332.alistandroid.ui.widgets.DenseOutlinedField
import com.github.jing332.alistandroid.util.AndroidUtils.performLongPress
import com.github.jing332.alistandroid.util.ClipboardUtils
import com.github.jing332.alistandroid.util.ToastUtils.longToast
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AListConfigScreen(vm: AListConfigViewModel = viewModel()) {
    val context = LocalContext.current
    fun openFileUseExtApp(path: String, title: String, mimeType: String = "text/*") {
        runCatching {
            val uriProvider =
                FileProvider.getUriForFile(
                    /* context = */ context,
                    /* authority = */ AppConst.fileProviderAuthor,
                    /* file = */ File(path)
                )
            val intent = Intent(Intent.ACTION_VIEW, uriProvider).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mimeType.isEmpty())
                    setData(uriProvider)
                else
                    setDataAndType(uriProvider, mimeType)
            }

            context.startActivity(Intent.createChooser(intent, title))
        }.onFailure {
            context.longToast(it.toString())
        }
    }

//    val scope = rememberCoroutineScope()
    LaunchedEffect(vm.hashCode()) {
        vm.init()
    }
//    val cfg = vm.config
//
//    var address by remember { mutableStateOf("0.0.0.0") }
    val view = LocalView.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(id = R.string.alist_config),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            AList.dataPath,
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 0.75f,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.extraSmall)
                                .combinedClickable(onClick = {
                                    openFileUseExtApp(
                                        AList.dataPath,
                                        context.getString(R.string.open_data_folder),
                                        mimeType = ""
                                    )
                                }, onLongClick = {
                                    view.performLongPress()
                                    ClipboardUtils.copyText(AList.dataPath)
                                    context.longToast(R.string.path_copied)
                                })
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        openFileUseExtApp(
                            AList.configPath,
                            context.getString(R.string.edit_config_json)
                        )
                    }) {
                        Icon(Icons.Default.ModeEdit, stringResource(R.string.edit_config_json))
                    }
                },
            )
        }
    )
    { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()

        ) {
            Column(Modifier.align(Alignment.CenterHorizontally)) {
                Text(stringResource(id = R.string.open_data_folder_tips))
            }
        }


//            Column(Modifier.align(Alignment.CenterHorizontally)) {
//                Text(
//                    "TODO 本界面的功能暂未实现",
//                    color = MaterialTheme.colorScheme.error,
//                    style = MaterialTheme.typography.titleLarge,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
//                )
//                ElevatedCard(Modifier.padding(8.dp)) {
//                    Column(Modifier.padding(8.dp)) {
//                        Text(
//                            "Scheme",
//                            color = MaterialTheme.colorScheme.primary,
//
//                            fontWeight = FontWeight.Bold,
//                            modifier = Modifier.align(Alignment.Start)
//                        )
//
//                        Spacer(modifier = Modifier.height(8.dp))
//                        var httpPort by remember { mutableIntStateOf(5244) }
//                        var httpsPort by remember { mutableIntStateOf(-1) }
//                        AnimatedVisibility(visible = httpPort == -1 && httpsPort == -1) {
//                            Text(
//                                stringResource(R.string.no_server_enable_warn_msg),
//                                color = MaterialTheme.colorScheme.error,
//                                fontWeight = FontWeight.Bold,
//                                modifier = Modifier
//                                    .align(Alignment.CenterHorizontally)
//                                    .padding(4.dp)
//                            )
//                        }
//
//                        DenseOutlinedField(enabled = !(httpPort == -1 && httpsPort == -1),
//                            label = { Text(stringResource(R.string.listen_address)) },
//                            modifier = Modifier.fillMaxWidth(),
//                            value = address,
//                            onValueChange = { address = it })
//                        Spacer(modifier = Modifier.height(24.dp))
//                        Row {
//                            PortEdit(
//                                Modifier
//                                    .weight(1f)
//                                    .padding(end = 8.dp),
//                                "HTTP",
//                                value = httpPort,
//                                onValueChange = {
//                                    httpPort = it
//
//                                })
//
//                            PortEdit(
//                                Modifier
//                                    .weight(1f)
//                                    .padding(start = 8.dp),
//                                "HTTPS",
//                                value = httpsPort,
//                                onValueChange = { httpsPort = it })
//                        }
//                    }
//                }
//            }
//        }
    }
}

@Composable
fun PortEdit(modifier: Modifier, label: String, value: Int, onValueChange: (Int) -> Unit) {
    Row(modifier) {
        val isEnabled = value != -1
        Checkbox(
            modifier = Modifier.align(Alignment.CenterVertically),
            checked = isEnabled,
            onCheckedChange = {
                onValueChange.invoke(
                    if (it) 5244
                    else -1
                )
            },
        )
        DenseOutlinedField(
            label = { Text(label) },
            enabled = isEnabled,
            value = value.toString(),
            onValueChange = {
                try {
                    onValueChange.invoke(it.toInt())
                } catch (_: NumberFormatException) {
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }
}