package com.github.jing332.alistandroid.model

import android.util.Log
import com.github.jing332.alistandroid.BuildConfig
import com.github.jing332.alistandroid.util.AndroidUtils
import com.github.jing332.alistandroid.util.StringUtils.toNumberInt

object AppUpdateChecker {
    const val TAG = "AppUpdateChecker"

    fun checkUpdate(): UpdateResult {
        val rel = Github.getLatestRelease()

        val latest = rel.tagName.toNumberInt()
        val current = BuildConfig.VERSION_NAME.toNumberInt()
        Log.i(TAG, "checkUpdate: current=$current, latest=$latest")

        if (current < latest) {
            val ass = getApkDownloadUrl(rel.assets)
            return UpdateResult(
                version = rel.tagName,
                content = rel.body,
                downloadUrl = ass.browserDownloadUrl
            ).apply {
                Log.i(TAG, "checkUpdate: hasUpdate=${this.downloadUrl}")
            }
        }

        return UpdateResult()
    }

    private fun getApkDownloadUrl(assets: List<Github.Release.Asset>): Github.Release.Asset {
        val abi = AndroidUtils.getABI()

        // 最大的为全量apk
        val apkUniversal = assets.sortedByDescending { it.size }[0]
        // 根据CPU ABI判断精简版apk
        val liteApk =
            assets.find { it.name.endsWith("${abi}.apk") }

        return liteApk ?: apkUniversal
    }
}