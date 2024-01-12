package com.github.jing332.alistandroid.config

import com.github.jing332.alistandroid.App
import com.funny.data_saver.core.DataSaverPreferences
import com.funny.data_saver.core.mutableDataSaverStateOf
import com.github.jing332.alistandroid.app

object ServerConfig {
    private val pref =
        DataSaverPreferences(app.getSharedPreferences("server", 0))



}