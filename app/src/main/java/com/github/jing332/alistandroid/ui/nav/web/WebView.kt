package com.github.jing332.alistandroid.ui.nav.web

import android.annotation.SuppressLint
import android.content.Intent
import android.view.ViewGroup.LayoutParams
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.util.ToastUtils.longToast

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebView(modifier: Modifier = Modifier, url: String, vm: WebViewModel = viewModel()) {
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
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                val refreshLayout = SwipeRefreshLayout(it)
                webView ?: run {
                    webView = WebView(it).apply {
                        webViewClient = object : WebViewClient() {
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