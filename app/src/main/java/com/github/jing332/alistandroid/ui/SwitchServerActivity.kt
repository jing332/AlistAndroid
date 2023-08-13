package com.github.jing332.alistandroid.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.model.AList
import com.github.jing332.alistandroid.service.AlistService
import com.github.jing332.alistandroid.util.ToastUtils.toast

class SwitchServerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AList.hasRunning) {
            toast(R.string.alist_shut_downing)
            startService(Intent(this, AlistService::class.java).apply {
                action = AlistService.ACTION_SHUTDOWN
            })
        } else {
            toast(R.string.alist_starting)
            startService(Intent(this, AlistService::class.java))
        }

        finish()
    }
}