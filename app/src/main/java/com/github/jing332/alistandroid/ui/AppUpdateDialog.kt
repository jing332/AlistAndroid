package com.github.jing332.alistandroid.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.jing332.alistandroid.R
import dev.jeziellago.compose.markdowntext.MarkdownText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUpdateDialog(
    onDismissRequest: () -> Unit,
    version: String,
    content: String,
    downloadUrl: String
) {
    val context = LocalContext.current
    fun openDownloadUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    AlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            tonalElevation = 8.dp,
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.wrapContentHeight()
        ) {
            Column {
                Text(
                    "检查到新版本",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Text(
                    text = version,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                val scrollState = rememberScrollState()
                Column(
                    Modifier
                        .padding(8.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.Center
                ) {
                    MarkdownText(
                        markdown = content, modifier = Modifier
                            .padding(4.dp),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(Modifier.align(Alignment.CenterHorizontally)) {
                        Row {
                            TextButton(onClick = { /*TODO*/ }) {
                                Text(stringResource(id = R.string.copy_address))
                            }
                            TextButton(onClick = { openDownloadUrl(downloadUrl) }) {
                                Text("下载(Github)")
                            }
                            TextButton(onClick = { openDownloadUrl("https://ghproxy.com/${downloadUrl}") }) {
                                Text("下载(ghproxy加速)")
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        scrollState.animateScrollTo(scrollState.maxValue)
                    }
                }

            }
        }
    }
}