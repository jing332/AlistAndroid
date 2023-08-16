package com.github.jing332.alistandroid.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.model.AppUpdateChecker
import com.github.jing332.alistandroid.model.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    companion object {
        const val TAG = "MainViewModel"
    }

    var showUpdateDialog by mutableStateOf<UpdateResult?>(null)

    fun checkAppUpdate() {
        if (AppConfig.isAutoCheckUpdate.value)
            viewModelScope.launch(Dispatchers.IO) {
                runCatching {
                    val ret = AppUpdateChecker.checkUpdate()
                    if (ret.hasUpdate()) showUpdateDialog = ret
                }.onFailure {
                    Log.e(TAG, "checkAppUpdate: ", it)
                }
            }
    }
}