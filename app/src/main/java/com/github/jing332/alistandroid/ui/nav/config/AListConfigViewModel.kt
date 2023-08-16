package com.github.jing332.alistandroid.ui.nav.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.jing332.alistandroid.model.alist.AListConfigManager
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch

class AListConfigViewModel : ViewModel() {
    fun init() {
        viewModelScope.launch {
            AListConfigManager.flowConfig().conflate().collect {
                println("collect ${it.scheme}")
//                context.toast(it.scheme.httpPort.toString())
            }
        }
    }
}