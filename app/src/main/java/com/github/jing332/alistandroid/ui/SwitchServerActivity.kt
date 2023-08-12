package com.github.jing332.alistandroid.ui

import android.app.Activity
import android.os.Bundle

class SwitchServerActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (ForwardService.isRunning) {
//            toast(R.string.server_shutting)
//            startService(Intent(this, ForwardService::class.java).apply {
//                action = ForwardService.ACTION_SHUTDOWN
//            })
//        } else {
//            toast(R.string.server_starting)
//            startService(Intent(this, ForwardService::class.java))
//        }

        finish()
    }
}