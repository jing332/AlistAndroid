package com.github.jing332.alistandroid.ui.nav.web

import android.annotation.SuppressLint
import android.app.Application
import android.webkit.WebView
import androidx.lifecycle.AndroidViewModel

class WebViewModel(private val application: Application ) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    var webView: WebView? = null
}