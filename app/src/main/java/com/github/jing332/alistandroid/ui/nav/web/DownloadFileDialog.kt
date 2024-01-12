package com.github.jing332.alistandroid.ui.nav.web

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.ui.widgets.AppDialog
import com.github.jing332.alistandroid.util.ClipboardUtils
import com.github.jing332.alistandroid.util.StringUtils.parseToMap
import com.github.jing332.alistandroid.util.ToastUtils.toast
import java.io.File

@Composable
internal fun DownloadFileDialog(
    onDismissRequest: () -> Unit,
    url: String,
    userAgent: String,
    contentDisposition: String,
) {
    val context = LocalContext.current
    val fileName = remember {
        contentDisposition.parseToMap().getOrElse("filename") { "" }
            .trim('"')
    }

    fun addSystemDownload() {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "AList" + File.separator + fileName
        )
        request.setMimeType("")
        request.addRequestHeader("User-Agent", userAgent)
        request.setDescription(fileName)
        downloadManager.enqueue(request)

        context.toast("已添加到下载管理器")
    }

    fun openUrl() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.resolveActivity(context.packageManager)

        context.startActivity(
            Intent.createChooser(
                intent,
                fileName.ifBlank { "Download file" })
        )
    }

    AppDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(stringResource(id = R.string.download_file))
        }, content = {
            SelectionContainer {

                Text(
                    modifier = Modifier.clickable {
                        ClipboardUtils.copyText(url)
                        context.toast(R.string.url_copied)
                    },
                    text = url,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }, buttons = {
            Row(Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.weight(1f)) {
                    TextButton(onClick = {
                        onDismissRequest()
                        addSystemDownload()
                    }) {
                        Text(stringResource(id = R.string.system_downloader))
                    }

                    TextButton(onClick = {
                        onDismissRequest()
                        openUrl()
                    }) {
                        Text(stringResource(id = R.string.open_url))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }

            }
        }
    )
}