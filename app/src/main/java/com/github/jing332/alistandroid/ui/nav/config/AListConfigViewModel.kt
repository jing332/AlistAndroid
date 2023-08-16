package com.github.jing332.alistandroid.ui.nav.config

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jing332.alistandroid.model.alist.AListConfig
import com.github.jing332.alistandroid.model.alist.AListConfigManager
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

class AListConfigViewModel : ViewModel() {
    var config by mutableStateOf(AListConfig())

    fun init() {
        viewModelScope.launch {
            AListConfigManager.flowConfig().conflate().collect {
                println("collect ${it.scheme}")
                config = it
            }
        }
    }
}