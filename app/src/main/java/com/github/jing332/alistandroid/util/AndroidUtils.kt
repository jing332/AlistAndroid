package com.github.jing332.alistandroid.util

import android.os.Build

object AndroidUtils {
    const val ABI_ARM = "armeabi-v7a"
    const val ABI_ARM64 = "arm64-v8a"
    const val ABI_X86 = "x86"
    const val ABI_X86_64 = "x86_64"

    fun getABI(): String? {
        return Build.SUPPORTED_ABIS[0]
    }

}