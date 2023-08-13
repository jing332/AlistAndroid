package com.github.jing332.alistandroid.ui.nav.config

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ModeEdit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.constant.AppConst
import com.github.jing332.alistandroid.model.AList
import com.github.jing332.alistandroid.ui.widgets.DenseOutlinedField
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AListConfigScreen() {
    val context = LocalContext.current
    fun openConfigJson() {
        val uri =
            FileProvider.getUriForFile(
                /* context = */ context,
                /* authority = */ AppConst.fileProviderAuthor,
                /* file = */ File(AList.configPath)
            )
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            setDataAndType(uri, "text/*")
        }

        context.startActivity(intent)
    }

    var address by remember { mutableStateOf("0.0.0.0") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.alist_config)) },
                actions = {
                    IconButton(onClick = {
                        openConfigJson()
                    }) {
                        Icon(Icons.Default.ModeEdit, stringResource(R.string.edit_config_json))
                    }
                }
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
                ElevatedCard(Modifier.padding(8.dp)) {
                    Column(Modifier.padding(8.dp)) {
                        Text(
                            "Scheme",
                            color = MaterialTheme.colorScheme.primary,

                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        var httpPort by remember { mutableIntStateOf(5244) }
                        var httpsPort by remember { mutableIntStateOf(-1) }
                        AnimatedVisibility(visible = httpPort == -1 && httpsPort == -1) {
                            Text(
                                stringResource(R.string.no_server_enable_warn_msg),
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(4.dp)
                            )
                        }

                        DenseOutlinedField(enabled = !(httpPort == -1 && httpsPort == -1),
                            label = { Text(stringResource(R.string.listen_address)) },
                            modifier = Modifier.fillMaxWidth(),
                            value = address,
                            onValueChange = { address = it })
                        Spacer(modifier = Modifier.height(24.dp))
                        Row {
                            PortEdit(
                                Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp),
                                "HTTP",
                                value = httpPort,
                                onValueChange = { httpPort = it })

                            PortEdit(
                                Modifier
                                    .weight(1f)
                                    .padding(start = 8.dp),
                                "HTTPS",
                                value = httpsPort,
                                onValueChange = { httpsPort = it })
                        }
                    }
                }
            }
        }
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