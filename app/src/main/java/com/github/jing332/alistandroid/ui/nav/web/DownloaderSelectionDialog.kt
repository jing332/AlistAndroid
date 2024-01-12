package com.github.jing332.alistandroid.ui.nav.web

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.ui.theme.AppTheme
import com.github.jing332.alistandroid.ui.widgets.AppDialog
import com.github.jing332.alistandroid.util.ClipboardUtils
import com.github.jing332.alistandroid.util.ToastUtils.toast


private data class DownloaderInfo(
    val resolveInfo: ResolveInfo,
    val name: String,
    val icon: ImageBitmap
)

@Composable
internal fun DownloaderSelectionDialog(
    onDismissRequest: () -> Unit,
    url: String,
) {
    val context = LocalContext.current
    var latestDownloader by remember { AppConfig.latestDownloader }

    val testDl = "http://localhost:5244/test.txt" // 下载器
    val testAll = "http://" // 浏览器
    var testUrl by remember { mutableStateOf(testDl) }

    fun getList(testUrl: String): List<DownloaderInfo> {
        val list = mutableListOf<DownloaderInfo>()
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setData(Uri.parse(testUrl))
        val l =
            context.packageManager.queryIntentActivities(
                intent,
                if (testUrl == testAll) PackageManager.MATCH_ALL else PackageManager.MATCH_DEFAULT_ONLY
            )
        l.forEach {
            list += DownloaderInfo(
                it,
                it.loadLabel(context.packageManager).toString(),
                it.loadIcon(context.packageManager).toBitmap().asImageBitmap()
            )
        }

        return list
    }

    val list = remember { mutableStateListOf<DownloaderInfo>() }
    var latestInfo by remember { mutableStateOf<DownloaderInfo?>(null) }
    LaunchedEffect(Unit) {
        list.clear()
        list += getList(testDl)
        list += getList(testAll)
        list.distinctBy { it.resolveInfo.activityInfo.packageName + it.resolveInfo.activityInfo.name }

        kotlin.runCatching {
            val latestPackageName = latestDownloader.split("|")[0]
            val latestActivityName = latestDownloader.split("|")[1]
            latestInfo = list.find {
                it.resolveInfo.activityInfo.packageName == latestPackageName &&
                        it.resolveInfo.activityInfo.name == latestActivityName
            }
        }.onFailure {
            latestInfo = null
            latestDownloader = ""
        }
    }

    fun startUrlApp(resolveInfo: ResolveInfo) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.data = Uri.parse(url)
        intent.setClassName(
            resolveInfo.activityInfo.packageName,
            resolveInfo.activityInfo.name
        )
        context.startActivity(intent)
    }

    AppDialog(onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.select_downloader)) },
        content = {
            Column {
                SelectionContainer {
                    Text(
                        modifier = Modifier.clickable {
                            ClipboardUtils.copyText(url)
                            context.toast(R.string.url_copied)
                        },
                        text = url,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                LazyColumn {
                    items(list) {

                        Row(modifier = Modifier
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple(),
                                onClick = {
                                    startUrlApp(it.resolveInfo)
                                    latestDownloader =
                                        it.resolveInfo.activityInfo.packageName + "|" + it.resolveInfo.activityInfo.name
                                }
                            )
                            .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                bitmap = it.icon,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .padding(vertical = 4.dp)
                            )
                            Text(
                                modifier = Modifier
                                    .weight(1f),
                                text = it.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }, buttons = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (latestInfo != null)
                    TextButton(onClick = {
                        startUrlApp(latestInfo!!.resolveInfo)
                        onDismissRequest()
                    }) {
                        Text(stringResource(id = R.string.last_used))
                        Text((": " + latestInfo?.name))
                    }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onDismissRequest) {
                    Text(stringResource(id = R.string.cancel))
                }
            }


        }
    )
}

@Preview
@Composable
private fun PreviewDownloadSelectionDialog() {
    val url =
        """https://cn-beijing-data.aliyundrive.net/5efad89a3b84299fd8324fd18c23e71adda192d6%2F5efad89a168e3acc7eda4b5396411f175936be71?callback=eyJjYWxsYmFja1VybCI6Imh0dHA6Ly9iajI5LmFwaS1ocC5hbGl5dW5wZHMuY29tL3YyL2ZpbGUvZG93bmxvYWRfY2FsbGJhY2siLCJjYWxsYmFja0JvZHkiOiJodHRwSGVhZGVyLnJhbmdlPSR7aHR0cEhlYWRlci5yYW5nZX1cdTAwMjZidWNrZXQ9JHtidWNrZXR9XHUwMDI2b2JqZWN0PSR7b2JqZWN0fVx1MDAyNmRvbWFpbl9pZD0ke3g6ZG9tYWluX2lkfVx1MDAyNnVzZXJfaWQ9JHt4OnVzZXJfaWR9XHUwMDI2ZHJpdmVfaWQ9JHt4OmRyaXZlX2lkfVx1MDAyNmZpbGVfaWQ9JHt4OmZpbGVfaWR9IiwiY2FsbGJhY2tCb2R5VHlwZSI6ImFwcGxpY2F0aW9uL3gtd3d3LWZvcm0tdXJsZW5jb2RlZCIsImNhbGxiYWNrU3RhZ2UiOiJiZWZvcmUtZXhlY3V0ZSIsImNhbGxiYWNrRmFpbHVyZUFjdGlvbiI6Imlnbm9yZSJ9&callback-var=eyJ4OmRvbWFpbl9pZCI6ImJqMjkiLCJ4OnVzZXJfaWQiOiI2ZGExNTNjNzJjMzE0OTdhODI5OWFmOWM2MjhkNDVhYyIsIng6ZHJpdmVfaWQiOiI0OTQ3MDAwMiIsIng6ZmlsZV9pZCI6IjY0ZGEyMTY3MGQ4NTEwOTYzMDUyNGZkMjhlMTI1Nzk0ZGVjZTJkNzAifQ%3D%3D&di=bj29&dr=49470002&f=64da21670d85109630524fd28e125794dece2d70&response-content-disposition=attachment%3B%20filename%2A%3DUTF-8%27%272.txt&security-token=CAIS%2BgF1q6Ft5B2yfSjIr5eHEunxp5RzhofYVHHWp1A0RrcftpbA1Dz2IHFPeHJrBeAYt%2FoxmW1X5vwSlq5rR4QAXlDfNRXNMVnvqFHPWZHInuDox55m4cTXNAr%2BIhr%2F29CoEIedZdjBe%2FCrRknZnytou9XTfimjWFrXWv%2Fgy%2BQQDLItUxK%2FcCBNCfpPOwJms7V6D3bKMuu3OROY6Qi5TmgQ41Uh1jgjtPzkkpfFtkGF1GeXkLFF%2B97DRbG%2FdNRpMZtFVNO44fd7bKKp0lQLukMWr%2Fwq3PIdp2ma447NWQlLnzyCMvvJ9OVDFyN0aKEnH7J%2Bq%2FzxhTPrMnpkSlacGoABUoPE2jOEORiebfwJHV0jeVjVVlBUObg5O0Ew8mWMyVasFzVjHbpM9zGgbTtdP11pi64wmre5GggdrGP%2BbL%2FpCf5RspIkfzNsEkAu9WuParbLLoOclEFDL89M1mFs7jTj3m3bhxxW%2FbZuj%2B3sOUw%2BMaA8ZCkEF613RKS3GfUi%2FncgAA%3D%3D&u=6da153c72c31497a8299af9c628d45ac&x-oss-access-key-id=STS.NT2YSEJKR1E3VWgCPaJ83YSk6&x-oss-expires=1705055101&x-oss-signature=AD%2B627ARMqV2ipzXmtk3p1guWrQxxFOGmOlQvubucmo%3D&x-oss-signature-version=OSS2"""
    AppTheme {
        var show by remember { mutableStateOf(true) }
        if (show)
            DownloaderSelectionDialog(
                onDismissRequest = { show = false },
                url = url
            )
    }
}