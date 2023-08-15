package com.github.jing332.alistandroid.ui.nav.provider

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AListProviderScreen() {
    Scaffold {
        Column(Modifier.padding(it)) {
            Text("将AList的WebAPI转为Android的内容提供器，以方便在MT管理器或文件APP中使用。")
        }
    }
}