package com.github.jing332.alistandroid.config

import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.alistandroid.app

object AppConfig {
    private val pref =
        DataSaverPreferences(app.getSharedPreferences("app", 0))


    var isAutoCheckUpdate = mutableDataSaverStateOf(
        dataSaverInterface = pref,
        key = "isCheckUpdate",
        initialValue = true
    )

}