package com.github.jing332.alistandroid.ui.nav.web

import androidx.compose.runtime.Composable

@Composable
internal fun DownloadFileDialog(
    onDismissRequest: () -> Unit,
    url: String,
    userAgent: String,
    contentDisposition: String,
) {
    DownloaderSelectionDialog(onDismissRequest = onDismissRequest, url = url)


    /*val context = LocalContext.current
    val fileName = remember {
        URLUtil.guessFileName(url, contentDisposition, null)
//        contentDisposition.parseToMap().getOrElse("filename") { "" }
//            .trim('"')
    }

    fun addSystemDownload() {
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(Uri.parse(url))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val subPath = "AList" + File.separator + fileName
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath)
        request.setMimeType("")
        request.addRequestHeader("User-Agent", userAgent)
        request.setDescription(fileName)
        request.setTitle(fileName)

        Log.d("DownloadFileDialog", "addSystemDownload: $subPath")
        downloadManager.enqueue(request)

        context.toast("已添加到下载管理器\n/内部存储/Download/AList")
    }

    var showDownloadSelectDialog by remember { mutableStateOf(true) }
    if (showDownloadSelectDialog)
        DownloaderSelectionDialog(
            onDismissRequest = { onDismissRequest() },
            url = url,
        )

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
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
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
                        showDownloadSelectDialog = true
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
    )*/
}