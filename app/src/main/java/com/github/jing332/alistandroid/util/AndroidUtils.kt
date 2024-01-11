package com.github.jing332.alistandroid.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.HapticFeedbackConstantsCompat
import com.github.jing332.alistandroid.constant.AppConst

object AndroidUtils {
    const val ABI_ARM = "armeabi-v7a"
    const val ABI_ARM64 = "arm64-v8a"
    const val ABI_X86 = "x86"
    const val ABI_X86_64 = "x86_64"

    fun getABI(): String? {
        return Build.SUPPORTED_ABIS[0]
    }

    fun View.performLongPress() {
        isHapticFeedbackEnabled = true
        performHapticFeedback(HapticFeedbackConstantsCompat.LONG_PRESS)
    }

}