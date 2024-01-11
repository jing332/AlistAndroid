package com.github.jing332.alistandroid.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.service.AListService
import com.github.jing332.alistandroid.util.ToastUtils.toast

class SwitchServerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (AListService.isRunning) {
            toast(R.string.alist_shut_downing)
            startService(Intent(this, AListService::class.java).apply {
                action = AListService.ACTION_SHUTDOWN
            })
        } else {
            toast(R.string.alist_starting)
            startService(Intent(this, AListService::class.java))
        }

        finish()
    }
}