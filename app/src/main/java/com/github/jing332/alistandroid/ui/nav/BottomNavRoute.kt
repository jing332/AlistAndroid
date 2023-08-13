package com.github.jing332.alistandroid.ui.nav

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.github.jing332.alistandroid.R

sealed class BottomNavRoute(
    @StringRes val strId: Int,
    val id: String,
    val icon: @Composable (() -> Unit),
) {
    companion object {
        val routes = listOf(
            AListConfig,
            AList,
            Settings,
        )
    }

    data object AListConfig : BottomNavRoute(R.string.alist_config, "alist_config", {
        Icon(Icons.Default.Code, null)
    })

    data object AList : BottomNavRoute(R.string.app_name, "alist", {
        Icon(painterResource(id = R.drawable.alist_logo), null)
    })

    data object Settings : BottomNavRoute(R.string.settings, "settings", {
        Icon(Icons.Default.Settings, null)
    })
}