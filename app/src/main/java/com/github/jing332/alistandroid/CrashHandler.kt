package com.github.jing332.alistandroid

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.drake.net.utils.runMain
import com.github.jing332.alistandroid.util.ClipboardUtils
import com.github.jing332.alistandroid.util.FileUtil
import com.github.jing332.alistandroid.util.ToastUtils.longToast
import java.text.DateFormat
import java.util.Date

/* loaded from: classes2.dex */
class CrashHandler(val context: Context) : Thread.UncaughtExceptionHandler {
    companion object {
        const val TAG = "CrashHandler"
    }

    private var defaultHandler: Thread.UncaughtExceptionHandler? = null

    init {
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    // java.lang.Thread.UncaughtExceptionHandler
    override fun uncaughtException(t: Thread, e: Throwable) {
        handleException(e)
        val uncaughtExceptionHandler = defaultHandler
        uncaughtExceptionHandler?.uncaughtException(t, e)
    }

    @Suppress("DEPRECATION")
    private fun handleException(e: Throwable) {
        runMain {
            app.longToast("程序出现异常，已将日志复制到剪贴板")
        }
        if (App.isMainThread)
            Thread.sleep(2000)
        val packageName = this.context.packageName
        try {
            val packageInfo = this.context.packageManager.getPackageInfo(packageName, 1)
            val str = """
                ${DateFormat.getInstance().format(Date())}
                Version：${packageInfo.versionCode} (${packageInfo.versionName})
                Brand：${Build.BRAND}，Model：${Build.MODEL}，Android：${Build.VERSION.RELEASE}
                StackTrace：
                ${Log.getStackTraceString(e)}
                """.trimIndent()

            ClipboardUtils.copyText("AlistAndroid", str)
            FileUtil.writeFile(context.getExternalFilesDir("log").toString() + "/crash.log", str)
        } catch (e2: PackageManager.NameNotFoundException) {
            Log.e(TAG, "handleException: ", e2)
        }
    }
}