package com.github.jing332.alistandroid.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.drake.net.utils.withMain
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.config.AppConfig
import com.github.jing332.alistandroid.constant.AppConst
import com.github.jing332.alistandroid.model.alist.AList
import com.github.jing332.alistandroid.model.alist.AListConfigManager
import com.github.jing332.alistandroid.ui.MainActivity
import com.github.jing332.alistandroid.ui.theme.androidColor
import com.github.jing332.alistandroid.util.ClipboardUtils
import com.github.jing332.alistandroid.util.ToastUtils.toast
import com.github.jing332.utils.NativeLib
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import splitties.systemservices.powerManager

class AListService : Service() {
    companion object {
        const val TAG = "AlistService"
        const val ACTION_SHUTDOWN =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_SHUTDOWN"

        const val ACTION_COPY_ADDRESS =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_COPY_ADDRESS"

        const val ACTION_STATUS_CHANGED =
            "com.github.jing332.alistandroid.service.AlistService.ACTION_STATUS_CHANGED"

        const val NOTIFICATION_CHAN_ID = "alist_server"
        const val FOREGROUND_ID = 5224

        var isRunning: Boolean = false
    }

    private val mScope = CoroutineScope(Job())
    private val mNotificationReceiver = NotificationActionReceiver()
    private val mReceiver = MyReceiver()
    private var mWakeLock: PowerManager.WakeLock? = null

    override fun onBind(p0: Intent?): IBinder? = null

    @Suppress("DEPRECATION")
    private fun notifyStatusChanged() {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(Intent(ACTION_STATUS_CHANGED))
    }

    @SuppressLint("WakelockTimeout")
    override fun onCreate() {
        super.onCreate()

        if (AppConfig.enabledWakeLock.value) {
            mWakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "alist::service"
            )
            mWakeLock?.acquire()
        }

        AppConst.localBroadcast.registerReceiver(mReceiver, IntentFilter(ACTION_STATUS_CHANGED))
        ContextCompat.registerReceiver(
            this,
            mNotificationReceiver,
            IntentFilter(ACTION_SHUTDOWN).apply {
                addAction(ACTION_COPY_ADDRESS)
            },
            ContextCompat.RECEIVER_EXPORTED
        )
        initNotification()
    }


    @Suppress("DEPRECATION")
    override fun onDestroy() {
        super.onDestroy()

        mWakeLock?.release()
        mWakeLock = null

        stopForeground(true)

        AppConst.localBroadcast.unregisterReceiver(mReceiver)
        unregisterReceiver(mNotificationReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) {
            AList.shutdown()
        } else {
            toast(getString(R.string.starting))
            isRunning = true
            notifyStatusChanged()
            mScope.launch(Dispatchers.IO) {
                val ret = AList.startup()
                isRunning = false
                withMain {
                    toast(getString(R.string.shutdowned, ret.toString()))
                    notifyStatusChanged()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @Suppress("DEPRECATION")
    inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STATUS_CHANGED) {
                if (!isRunning) {
                    stopForeground(true)
                    stopSelf()
                }
            }
        }
    }

    private fun httpAddress(): String {
        val cfg = AListConfigManager.config()
        val ip = NativeLib.getLocalIp()
        return "http://${ip}:${cfg.scheme.httpPort}"
    }

    @Suppress("DEPRECATION")
    private fun initNotification() {
        // Android 12(S)+ 必须指定PendingIntent.FLAG_
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            PendingIntent.FLAG_IMMUTABLE
        else
            0

        /*点击通知跳转*/
        val pendingIntent =
            PendingIntent.getActivity(
                this, 0, Intent(
                    this,
                    MainActivity::class.java
                ),
                pendingIntentFlags
            )
        /*当点击退出按钮时发送广播*/
        val shutdownAction: PendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                Intent(ACTION_SHUTDOWN),
                pendingIntentFlags
            )
        val copyAddressPendingIntent =
            PendingIntent.getBroadcast(
                this,
                0,
                Intent(ACTION_COPY_ADDRESS),
                pendingIntentFlags
            )

        val color = com.github.jing332.alistandroid.ui.theme.seed.androidColor
        val smallIconRes: Int
        val builder = Notification.Builder(applicationContext)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {/*Android 8.0+ 要求必须设置通知信道*/
            val chan = NotificationChannel(
                NOTIFICATION_CHAN_ID,
                getString(R.string.alist_server),
                NotificationManager.IMPORTANCE_NONE
            )
            chan.lightColor = color
            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)
            smallIconRes = when ((0..1).random()) {
                0 -> R.drawable.server
                1 -> R.drawable.server2
                else -> R.drawable.server2
            }

            builder.setChannelId(NOTIFICATION_CHAN_ID)
        } else {
            smallIconRes = R.mipmap.ic_launcher_round
        }
        val notification = builder
            .setColor(color)
            .setContentTitle(getString(R.string.alist_server_running))
            .setContentText(httpAddress())
            .setSmallIcon(smallIconRes)
            .setContentIntent(pendingIntent)
            .addAction(0, getString(R.string.shutdown), shutdownAction)
            .addAction(0, getString(R.string.copy_address), copyAddressPendingIntent)
            .build()

        // 前台服务
        startForeground(FOREGROUND_ID, notification)
    }

    inner class NotificationActionReceiver : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                ACTION_SHUTDOWN -> AList.shutdown()

                ACTION_COPY_ADDRESS -> {
                    ClipboardUtils.copyText("AList", httpAddress())
                    toast(R.string.address_copied)
                }
            }
        }
    }

}