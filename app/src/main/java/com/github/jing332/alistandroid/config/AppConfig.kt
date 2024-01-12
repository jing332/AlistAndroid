package com.github.jing332.alistandroid.config

import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.alistandroid.app
import java.io.File

object AppConfig {
    private val pref =
        DataSaverPreferences(app.getSharedPreferences("app", 0))


    val isFirstRun: Boolean
        get() = File(
            (app.filesDir.parentFile?.absolutePath ?: "") + File.separator + "databases"
        ).listFiles()?.isEmpty() ?: true

    var isAutoCheckUpdate = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "isCheckUpdate",
        initialValue = true
    )

    var enabledWakeLock = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "enabledWakeLock",
        initialValue = false
    )

    var autoOpenWebPage = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "autoOpenWebPage",
        initialValue = false
    )

    var latestDownloader = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "latestDownloader",
        initialValue = ""
    )

    var isStartAtBoot = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "isStartAtBoot",
        initialValue = false
    )

}