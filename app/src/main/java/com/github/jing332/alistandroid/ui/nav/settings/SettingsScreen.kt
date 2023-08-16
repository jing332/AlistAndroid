package com.github.jing332.alistandroid.ui.nav.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.github.jing332.alistandroid.config.AppConfig

@Composable
fun SettingsScreen() {
    Column(Modifier.statusBarsPadding()) {
        var checkUpdate by remember { AppConfig.isAutoCheckUpdate }
        PreferenceSwitch(
            title = { Text("自动检查更新") },
            subTitle = { Text("打开程序主界面时从Github检查更新") },
            checked = checkUpdate,
            onCheckedChange = { checkUpdate = it },
            icon = {
                Icon(Icons.Default.ArrowCircleUp, contentDescription = null)
            }
        )

    }
}