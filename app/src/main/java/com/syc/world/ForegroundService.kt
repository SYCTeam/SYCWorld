package com.syc.world

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.system.exitProcess

// 使用前台服务以保持后台运行

class ForegroundService : Service() {
    private var wakeLock: PowerManager.WakeLock? = null
    private val handler = Handler(Looper.getMainLooper())
    private val wakeLockRunnable = object : Runnable {
        override fun run() {
            if (!isProcessRunning(applicationContext, "com.syc.world.MainProcess")) {
                restartMainProcess()
            }
            handler.postDelayed(this, 5000) // 每 5 秒检查一次
            acquireWakeLock() // 重新获取 WakeLock
            handler.postDelayed(this, 3 * 60 * 1000L) // 每 3 分钟重新获取一次，避免超时
        }
    }

    private fun createNotificationChannel() {
        val channelId = "SYC"
        val channelName = "酸夜沉空间前台服务"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val notificationChannel = NotificationChannel(channelId, channelName, importance)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
    }

    private fun acquireWakeLock() {
        if (wakeLock == null) {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SYC:WakeLock")
        }
        if (wakeLock?.isHeld != true) {
            wakeLock?.acquire(3 * 60 * 1000L) // 申请 3 分钟
        }
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        wakeLock = null
    }


    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireWakeLock()
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseWakeLock()
        handler.removeCallbacks(wakeLockRunnable) // 移除任务，避免内存泄漏
    }

    @SuppressLint("ServiceCast")
    fun isProcessRunning(context: Context, processName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses

        for (process in runningAppProcesses) {
            if (process.processName == processName) {
                return true
            }
        }
        return false
    }

    private fun restartMainProcess() {
        // 重启主进程
        val intent = Intent(this, MainProcessService::class.java)
        startService(intent)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification =
            buildForegroundNotification()

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        var stepCount: String
        var currentTime: Long
        var lastExecutionTime: Long = 0
        var nextExecutionTime = 5 * 60 * 1000

        CoroutineScope(Dispatchers.IO).launch {
            stepCount = readFromFile(applicationContext, "stepCount")
            Log.d("步数问题", stepCount)
            if (stepCount.trim().isNotEmpty() && stepCount.toIntOrNull() != null) {
                Global.stepCount = stepCount.toInt()
                Log.d("步数问题", "已经将文件中的步数更新，Global.stepCount: ${Global.stepCount}")
            }
            monitorStepCount(applicationContext)
            Log.d("步数问题", "已启用步数监控")
        }

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (Global.isLogin.value) {
                    delay(1 * 60 * 1000)
                    nextExecutionTime -= 1 * 60 * 1000
                    createStepCountNotification(
                        applicationContext,
                        "步数：${Global.stepCount}",
                        "距离下次提交步数还有:${nextExecutionTime / 60 / 1000}分钟。"
                    )
                    if (nextExecutionTime <= 0) {
                        nextExecutionTime = 5 * 60 * 1000
                    }
                }
                delay(1000)
            }
        }

        var result: String

        // 启动后台任务进行步数监测
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("提交问题", "已启动步数提交线程")
            while (true) {
                if (Global.isLogin.value) {
                    currentTime = System.currentTimeMillis()
                    if (currentTime - lastExecutionTime >= 5 * 60 * 1000) { // 5 分钟
                        Log.d(
                            "提交问题",
                            "已符合步数提交时间\n用户名: ${Global.username}, 用户密码: ${Global.password}, 步数: ${Global.stepCount}"
                        )
                        // 每5分钟提交一次步数
                        withContext(Dispatchers.IO) {
                            if (Global.username.trim().isNotEmpty() && Global.password.trim()
                                    .isNotEmpty() && Global.stepCount.toString().trim().isNotEmpty()
                            ) {
                                Log.d("提交问题", "开始提交步数...")
                                result = modifyStepCount(
                                    Global.username,
                                    Global.password,
                                    Global.stepCount.toString()
                                )
                                nextExecutionTime = 5 * 60 * 1000
                                Log.d("提交问题", result)
                            }
                        }
                        Log.d("提交问题", "已更新上一次执行的时间")
                        lastExecutionTime = currentTime
                    }
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
                createStepCountNotification(
                    applicationContext,
                    "步数：${Global.stepCount}",
                    "距离下次提交步数还有:${nextExecutionTime / 60 / 1000}分钟。"
                )
                if (Global.isLogin.value && Global.url != "" && Global.url.contains("http")) {
                    if (Global.username.trim().isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            if (checkUserOnline(username = Global.username).contains("success")) {
                                /*withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        applicationContext,
                                        "后台：心跳成功！\n步数：${Global.stepCount}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }*/
                            } else {
                                withContext(Dispatchers.IO) {
                                    val loginResponse = login(Global.username, Global.password)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            applicationContext,
                                            "后台：心跳失败！\n正在尝试重新登录...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    if (loginResponse.contains("success")) {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                applicationContext,
                                                "登录成功！",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                applicationContext,
                                                "登录失败，请重启软件！",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        delay(1000)
                                        exitProcess(0)
                                    }
                                }
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
    private fun buildForegroundNotification(text: String = "We are unstoppable."): Notification {
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
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }


    companion object
}

class MainProcessService : Service() {

    @SuppressLint("ServiceCast")
    fun isProcessRunning(context: Context, processName: String): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppProcesses = activityManager.runningAppProcesses

        for (process in runningAppProcesses) {
            if (process.processName == processName) {
                return true
            }
        }
        return false
    }

    override fun onCreate() {
        super.onCreate()
        // 这里做一些初始化工作
        Log.d("MainProcess", "Main process started.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // 启动后台任务进行心跳检测
        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                // 检测进程是否存在
                if (!isProcessRunning(applicationContext, "com.syc.world.MainProcess")) {
                    // 启动 MainActivity
                    Intent(
                        applicationContext,
                        MainActivity::class.java
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                delay(5000)
            }
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainProcess", "Main process stopped.")
    }
}
