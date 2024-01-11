package com.github.jing332.alistandroid.ui.nav.web

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.jing332.alistandroid.model.alist.AListConfigManager
import com.github.jing332.alistandroid.service.AListService

@Composable
fun WebScreen(modifier: Modifier = Modifier) {
    Scaffold {

        Column(modifier = modifier.padding(it)) {
            val url = remember { "http://localhost:${AListConfigManager.config().scheme.httpPort}" }
            WebView(url = url)

        }
    }
}