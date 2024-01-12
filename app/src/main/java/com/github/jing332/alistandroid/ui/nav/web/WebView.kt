package com.github.jing332.alistandroid.ui.nav.web

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.ViewGroup.LayoutParams
import android.webkit.JsResult
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.service.AListService
import com.github.jing332.alistandroid.ui.widgets.LocalBroadcastReceiver
import com.github.jing332.alistandroid.util.StringUtils.parseToMap
import com.github.jing332.alistandroid.util.ToastUtils.longToast
import com.github.jing332.alistandroid.util.ToastUtils.toast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebView(modifier: Modifier = Modifier, url: String) {
    var progress by remember { mutableIntStateOf(0) }

    var showAlertDialog by remember { mutableStateOf<Triple<String, String, JsResult>?>(null) }
    if (showAlertDialog != null) {
        val webUrl = showAlertDialog!!.first
        val msg = showAlertDialog!!.second
        val result = showAlertDialog!!.third
        AlertDialog(onDismissRequest = {
            result.cancel()
            showAlertDialog = null
        },
            title = { Text(webUrl) },
            text = { Text(msg) },
            confirmButton = {
                TextButton(
                    onClick = {
                        result.confirm()
                        showAlertDialog = null
                    }) {
                    Text(stringResource(id = android.R.string.ok))
                }
            }, dismissButton = {
                result.cancel()
                showAlertDialog = null
            })
    }
    Box {
        var webView by remember { mutableStateOf<WebView?>(null) }
        BackHandler(webView?.canGoBack() == true) {
            webView?.goBack()
        }

        val context = LocalContext.current
        val scope = rememberCoroutineScope()
        LocalBroadcastReceiver(intentFilter = IntentFilter(AListService.ACTION_STATUS_CHANGED)) {
            if (AListService.isRunning)
                scope.launch {
                    delay(2000)
                    context.toast("reload")

                }
        }

        LaunchedEffect(key1 = Unit) {
            if (!AListService.isRunning) {
                context.startService(Intent(context, AListService::class.java))
                webView?.reload()
            }
        }


        var showDownloadDialog by remember { mutableStateOf<Triple<String, String, String>?>(null) }
        if (showDownloadDialog != null) {
            val dlUrl = showDownloadDialog!!.first
            val userAgent = showDownloadDialog!!.second
            val contentDisposition = showDownloadDialog!!.third
            DownloadFileDialog(
                onDismissRequest = { showDownloadDialog = null },
                url = dlUrl,
                userAgent = userAgent,
                contentDisposition = contentDisposition,
            )
        }

        var filePathCallback by remember { mutableStateOf<ValueCallback<Array<Uri>>?>(null) }
        val filePicker =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
                val uri = it.data?.data
                if (uri != null) {
                    filePathCallback?.onReceiveValue(arrayOf(uri))
                } else
                    filePathCallback?.onReceiveValue(null)

                filePathCallback = null
            }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val refreshLayout = SwipeRefreshLayout(it)
                webView ?: run {
                    webView = WebView(it).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onReceivedError(
                                view: WebView,
                                request: WebResourceRequest,
                                error: WebResourceError
                            ) {
                                super.onReceivedError(view, request, error)
                                Thread.sleep(500)
                                if (request.isForMainFrame) view.reload()
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                kotlin.runCatching {
                                    if (request?.url?.scheme?.startsWith("http") == false) {
                                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                                        context.startActivity(
                                            Intent.createChooser(
                                                intent,
                                                request.url.toString()
                                            )
                                        )
                                        return true
                                    }
                                }.onFailure {
                                    context.longToast(R.string.togo_app_failed, request?.url ?: "")
                                }

                                return super.shouldOverrideUrlLoading(view, request)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onShowFileChooser(
                                webView: WebView?,
                                callback: ValueCallback<Array<Uri>>,
                                fileChooserParams: FileChooserParams
                            ): Boolean {
                                filePathCallback = callback

                                context.toast(fileChooserParams.mode.toString() + fileChooserParams.acceptTypes.contentToString())
                                filePicker.launch(
                                    Intent.createChooser(
                                        fileChooserParams.createIntent(),
                                        "Open files"
                                    )
                                )

                                return true
                            }

                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                super.onProgressChanged(view, newProgress)
                                progress = newProgress
                                if (newProgress == 100)
                                    refreshLayout.isRefreshing = false
                                else if (!refreshLayout.isRefreshing)
                                    refreshLayout.isRefreshing = true
                            }

                            override fun onJsConfirm(
                                view: WebView?,
                                url: String?,
                                message: String?,
                                result: JsResult?
                            ): Boolean {
                                if (result == null) return false
                                showAlertDialog = Triple(url ?: "", message ?: "", result)
                                return true
                            }

                            override fun onJsAlert(
                                view: WebView?,
                                url: String?,
                                message: String?,
                                result: JsResult?
                            ): Boolean {
                                if (result == null) return false
                                showAlertDialog = Triple(url ?: "", message ?: "", result)

                                return true
                            }
                        }

                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.databaseEnabled = true
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true

                        setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                            Log.d(
                                "WebView",
                                "url: $url, userAgent: $userAgent, contentDisposition: $contentDisposition, mimetype: $mimetype, contentLength: $contentLength"
                            )

                            showDownloadDialog = Triple(url, userAgent, contentDisposition)
                        }


                        loadUrl(url)
                    }
                }
                refreshLayout.apply {
                    setOnRefreshListener {
                        webView?.reload()
                    }

                    addView(
                        webView,
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    )
                }
            }
        )
        if (progress in 1..99)
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
    }
}