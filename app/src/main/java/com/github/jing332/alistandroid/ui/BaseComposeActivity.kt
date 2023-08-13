package com.github.jing332.alistandroid.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.github.jing332.alistandroid.ui.theme.AppTheme
import com.github.jing332.alistandroid.ui.widgets.TransparentSystemBars

abstract class BaseComposeActivity() : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            AppTheme {
                TransparentSystemBars()
                Content()
            }
        }
    }

    @Composable
    open fun Content() {
    }
}