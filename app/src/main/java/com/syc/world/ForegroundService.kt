package com.syc.world

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader

// 使用前台服务以保持后台运行

class ForegroundService : Service() {

    private fun createNotificationChannel() {
        val channelId = "SYC"
        val channelName = "酸夜沉空间"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val notificationChannel = NotificationChannel(channelId, channelName, importance)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }


    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildForegroundNotification()
        var lastExecutionTime: Long = 0

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        CoroutineScope(Dispatchers.IO).launch {
            monitorStepCount(applicationContext)
        }

        // 启动后台任务进行步数监测
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastExecutionTime >= 5 * 60 * 1000) { // 5 分钟
                    // 每5分钟提交一次步数
                    withContext(Dispatchers.IO) {
                        if (Global.username.trim().isNotEmpty() && Global.password.trim()
                                .isNotEmpty() && Global.stepCount.toString().trim().isNotEmpty()
                        ) {
                            modifyStepCount(
                                Global.username,
                                Global.password,
                                Global.stepCount.toString()
                            )
                        }
                    }
                    lastExecutionTime = currentTime
                }
                delay(1000) // 每秒检查一次
            }
        }

        // 启动后台任务进行心跳检测
        CoroutineScope(Dispatchers.IO).launch {
            while (Global.url == "") {
                withContext(Dispatchers.IO) {
                    Global.url = getUrl()
                }
                if (Global.url != "" && Global.url.contains("http")) {
                    break
                }
                delay(2000)
            }
            while (true) {
                if (Global.isLogin.value && Global.url != "" && Global.url.contains("http")) {
                    if (Global.username.trim().isNotEmpty()) {
                        val response = checkUserOnline(username = Global.username)
                        withContext(Dispatchers.Main) {
                            if (response.contains("success")) {
                                Toast.makeText(
                                    applicationContext,
                                    "后台：心跳成功！\n步数：${Global.stepCount}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "后台：心跳失败！",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
                delay(10000)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    @SuppressLint("SdCardPath")
    private fun buildForegroundNotification(): Notification {
        val channelId = "SYC"

        val emptyIntent = Intent().apply {
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            emptyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val name = readFile("/data/data/com.syc.world/files/username")

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle(if (name != "error") name else "酸夜沉空间正在运行中")
            .setContentText("We are unstoppable.")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }


    companion object
}

fun readFile(filePath: String): String {
    val file = File(filePath)
    return if (file.exists()) {
        try {
            val inputStream = FileInputStream(file)
            val reader = InputStreamReader(inputStream)
            val content = reader.readText()
            reader.close()
            content.trim()  // 去除多余的空格和换行符
        } catch (e: IOException) {
            e.printStackTrace()
            "error"
        }
    } else {
        "error"
    }
}
