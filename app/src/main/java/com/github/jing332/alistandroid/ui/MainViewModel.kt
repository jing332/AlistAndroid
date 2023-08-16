package com.github.jing332.alistandroid.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jing332.alistandroid.model.AppUpdateChecker
import com.github.jing332.alistandroid.model.UpdateResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    var showUpdateDialog by mutableStateOf<UpdateResult?>(null)

    fun checkAppUpdate() {
        viewModelScope.launch(Dispatchers.IO) {
            val ret = AppUpdateChecker.checkUpdate()
            if (ret.hasUpdate()) showUpdateDialog = ret
        }
    }
}