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
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import com.google.gson.Gson
import com.syc.world.ForegroundService.GlobalForForegroundService.isLogin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.util.regex.Pattern
import kotlin.coroutines.cancellation.CancellationException

// 使用前台服务以保持后台运行

class ForegroundService : Service() {
    object GlobalForForegroundService {
        var url = ""
        var username = ""
        var password = ""
        var stepCount = 0

        private val _isLogin = MutableStateFlow(false)
        val isLogin: StateFlow<Boolean>
            get() = _isLogin

        fun setIsLogin(value: Boolean) {
            _isLogin.value = value
        }

    }

    private var wakeLock: PowerManager.WakeLock? = null
    private val handler = Handler(Looper.getMainLooper())
    private val wakeLockRunnable = object : Runnable {
        override fun run() {
            Log.d("进程问题", "前台服务正在运行")
            if (!isProcessRunning(applicationContext, "com.syc.world.MainProcess")) {
                Log.d("进程问题", "主进程已掉线")
            }
            if (!isProcessRunning(applicationContext, "com.syc.world.RescueProcessService")) {
                Log.d("进程问题", "守护进程已掉线")
                restartRescueProcess()
            }
            handler.postDelayed(this, 1000) // 每 1 秒检查一次
        }
    }

    private fun readFromFileForForegroundService(context: Context, filename: String): String {
        // 获取文件路径
        val file = File(context.filesDir, filename)

        return if (file.exists()) {
            // 使用 FileInputStream 和 InputStreamReader 来读取文件内容
            FileInputStream(file).use { fis ->
                InputStreamReader(fis).use { reader ->
                    reader.readText() // 读取所有内容并返回
                }
            }
        } else {
            "" // 如果文件不存在，返回空字符串，或根据需要返回其他值
        }
    }

    private suspend fun getUrlForForegroundService(): String {
        val url = "https://sharechain.qq.com/93bd306d9c78bc6c4bc469c43c086cb6"

        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()

            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() ?: "【URL】http://xyc.okc.today【URL】"
                    val pattern = Pattern.compile("【URL】(.*?)【URL】", Pattern.DOTALL)
                    val matcher = pattern.matcher(responseBody)

                    if (matcher.find()) {
                        matcher.group(1) ?: "http://xyc.okc.today"
                    } else {
                        "http://xyc.okc.today"
                    }
                } else {
                    "http://xyc.okc.today"
                }
            } catch (e: IOException) {
                e.printStackTrace()
                "http://xyc.okc.today"
            } catch (e: CancellationException) {
                "http://xyc.okc.today"
            }
        }
    }


    private fun loginForForegroundService(username: String, password: String): String {
        val url = "${GlobalForForegroundService.url}/syc/login.php".toHttpUrlOrNull()
            ?: return "Error: Invalid URL"

        val client = OkHttpClient()

        // 创建请求体，包含用户名和密码
        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        // 构建请求
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d("登录问题", responseBody)
                responseBody
            } else {
                "Error: ${response.message}"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }


    private fun checkUserOnlineForForegroundService(username: String): String {
        val url = "${GlobalForForegroundService.url}/syc/keepAlive.php"

        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", username)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d("在线状态", responseBody)
                responseBody
            } else {
                "Error"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "Error"
        }
    }

    fun checkChatMessage(
        username: String,
        password: String,
        localMessageCount: Int,
    ): Pair<String, String> {
        val url = "${Global.url}/syc/receiveChatMessage.php".toHttpUrlOrNull() ?: return Pair(
            "Error",
            "Invalid URL"
        )

        val client = OkHttpClient()

        // 创建请求体，包含用户名和密码
        val formBodyBuilder =
            FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add("localMessageCount", localMessageCount.toString())

        val formBody = formBodyBuilder.build()

        Log.d("聊天信息获取", url.toString())

        // 构建请求
        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                Log.d("聊天信息获取", responseBody)
                try {
                    val pinUserInfo: WebCommonInfo =
                        Gson().fromJson(responseBody, WebCommonInfo::class.java)
                    Pair(pinUserInfo.status, pinUserInfo.message)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Pair("error", "Parsing error")
                }
            } else {
                Pair("error", response.message)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Pair("error", e.message ?: "Unknown error")
        }
    }



    private fun chatMessageNotification() {
        val chatMessage = { mutableStateListOf<ChatMessage>() }

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {

                delay(3000)
            }
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
            wakeLock?.acquire(1 * 60 * 1000L) // 申请 1 分钟
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
        // 启动 wakeLockRunnable
        handler.post(wakeLockRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
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

    private fun restartRescueProcess() {
        // 重启主进程
        val intent = Intent(this, RescueProcessService::class.java)
        startService(intent)
        Log.d("进程问题", "已尝试启动MainProcessService")
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
            while (true) {
                if (readFromFileForForegroundService(applicationContext, "isLogin") == "true") {
                    GlobalForForegroundService.setIsLogin(
                        readFromFileForForegroundService(
                            applicationContext,
                            "isLogin"
                        ).toBooleanStrict()
                    )
                } else if (readFromFileForForegroundService(
                        applicationContext,
                        "isLogin"
                    ) == "false"
                ) {
                    GlobalForForegroundService.setIsLogin(
                        readFromFileForForegroundService(
                            applicationContext,
                            "isLogin"
                        ).toBooleanStrict()
                    )
                }
                val readUsernameResult =
                    readFromFileForForegroundService(applicationContext, "username")
                if (readUsernameResult.trim().isNotEmpty()) {
                    GlobalForForegroundService.username = readUsernameResult
                }
                val readPasswordResult =
                    readFromFileForForegroundService(applicationContext, "password")
                if (readPasswordResult.trim().isNotEmpty()) {
                    GlobalForForegroundService.password = readPasswordResult
                }
                delay(500)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            stepCount = readFromFileForForegroundService(applicationContext, "stepCount")
            Log.d("步数问题", stepCount)
            if (stepCount.trim().isNotEmpty() && stepCount.toIntOrNull() != null) {
                GlobalForForegroundService.stepCount = stepCount.toInt()
                Log.d(
                    "步数问题",
                    "已经将文件中的步数更新，GlobalForForegroundService.stepCount: ${GlobalForForegroundService.stepCount}"
                )
            }
            monitorStepCount(applicationContext)
            Log.d("步数问题", "已启用步数监控")
        }

        CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                if (isLogin.value) {
                    delay(1 * 60 * 1000)
                    nextExecutionTime -= 1 * 60 * 1000
                    createStepCountNotification(
                        applicationContext,
                        "步数: ${GlobalForForegroundService.stepCount}",
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
                if (isLogin.value) {
                    currentTime = System.currentTimeMillis()
                    if (currentTime - lastExecutionTime >= 5 * 60 * 1000) { // 5 分钟
                        Log.d(
                            "提交问题",
                            "已符合步数提交时间\n用户名: ${GlobalForForegroundService.username}, 用户密码: ${GlobalForForegroundService.password}, 步数: ${GlobalForForegroundService.stepCount}"
                        )
                        // 每5分钟提交一次步数
                        withContext(Dispatchers.IO) {
                            if (GlobalForForegroundService.username.trim()
                                    .isNotEmpty() && GlobalForForegroundService.password.trim()
                                    .isNotEmpty() && GlobalForForegroundService.stepCount.toString()
                                    .trim().isNotEmpty()
                            ) {
                                Log.d("提交问题", "开始提交步数...")
                                result = modifyStepCount(
                                    GlobalForForegroundService.username,
                                    GlobalForForegroundService.password,
                                    GlobalForForegroundService.stepCount.toString()
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
            while (GlobalForForegroundService.url == "") {
                withContext(Dispatchers.IO) {
                    GlobalForForegroundService.url = getUrlForForegroundService()
                }
                if (GlobalForForegroundService.url != "" && GlobalForForegroundService.url.contains(
                        "http"
                    )
                ) {
                    break
                }
                delay(2000)
            }
            while (true) {
                createStepCountNotification(
                    applicationContext,
                    "步数：${GlobalForForegroundService.stepCount}",
                    "距离下次提交步数还有:${nextExecutionTime / 60 / 1000}分钟。"
                )
                if (isLogin.value && GlobalForForegroundService.url != "" && GlobalForForegroundService.url.contains(
                        "http"
                    )
                ) {
                    if (GlobalForForegroundService.username.trim().isNotEmpty()) {
                        withContext(Dispatchers.IO) {
                            if (checkUserOnlineForForegroundService(username = GlobalForForegroundService.username).contains(
                                    "success"
                                )
                            ) {
                                /*withContext(Dispatchers.Main) {
                                    Toast.makeText(
                                        applicationContext,
                                        "后台：心跳成功！\n步数：${GlobalForForegroundService.stepCount}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }*/
                            } else {
                                withContext(Dispatchers.IO) {
                                    acquireWakeLock()
                                    val loginResponse = loginForForegroundService(
                                        GlobalForForegroundService.username,
                                        GlobalForForegroundService.password
                                    )
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
                                                "登录失败，请让我再试N次！",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    releaseWakeLock()
                                }
                            }
                        }
                    }
                } else {
                    Log.d(
                        "在线状态",
                        "未登录或域名不完整, isLogin.value: ${isLogin.value}, GlobalForForegroundService.url: ${GlobalForForegroundService.url}"
                    )
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

class RescueProcessService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val startRun = object : Runnable {
        override fun run() {
            Log.d("进程问题", "守护进程正在运行")
            if (!isProcessRunning(applicationContext, "com.syc.world.ForegroundService")) {
                Log.d("进程问题", "前台服务已掉线")
                restartForegroundServiceProcess()
            }
            handler.postDelayed(this, 1000) // 每 1 秒检查一次
        }
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

    private fun restartForegroundServiceProcess() {
        val intent = Intent(this, ForegroundService::class.java)
        startService(intent)
        Log.d("进程问题", "已尝试启动前台服务")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("进程问题", "RescueProcessService started.")

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.post(startRun)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(startRun)
        Log.d("进程问题", "守护进程被摧毁")
    }
}
