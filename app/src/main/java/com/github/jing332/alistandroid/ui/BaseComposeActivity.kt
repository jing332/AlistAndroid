package com.github.jing332.alistandroid.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import com.github.jing332.alistandroid.ui.theme.AppTheme
import com.github.jing332.alistandroid.ui.widgets.TransparentSystemBars

abstract class BaseComposeActivity(
    private val flagSecure: Boolean = true,
    private val hasTheme: Boolean = true
) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (flagSecure)
            window.addFlags(WindowManager.LayoutParams.FLAG_SECURE);

        setContent {
            if (hasTheme)
                AppTheme {
                    TransparentSystemBars()
                    Content()
                }
            else {
                TransparentSystemBars()
                Content()
            }
        }
    }

    @Composable
    open fun Content() {
    }
}