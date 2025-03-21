package com.syc.world

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.syc.world.ForegroundService.GlobalForForegroundService.isInForeground
import com.syc.world.ui.theme.AppTheme
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.system.exitProcess


object Global {

    const val NAME = "酸夜沉空间"
    var url = ""
    var username = ""
    var password = ""
    var userQQ = ""

    var isGiveBodySensorsPermissions = false
    var isGiveActivityRecognitionPermissions = false
    var isGiveNotificationPermissions = false
    var isGiveManageFilePermissions = false

    private val _isShowEditSynopsis = MutableStateFlow(false)
    val isShowEditSynopsis: StateFlow<Boolean>
        get() = _isShowEditSynopsis

    fun setIsShowEditSynopsis(value: Boolean) {
        _isShowEditSynopsis.value = value
    }

    private val _isShowEditPassword = MutableStateFlow(false)
    val isShowEditPassword: StateFlow<Boolean>
        get() = _isShowEditPassword

    fun setIsShowEditPassword(value: Boolean) {
        _isShowEditPassword.value = value
    }

    private val _isShowEditQQ = MutableStateFlow(false)
    val isShowEditQQ: StateFlow<Boolean>
        get() = _isShowEditQQ

    fun setIsShowEditQQ(value: Boolean) {
        _isShowEditQQ.value = value
    }

    private val _isShowAskExit = MutableStateFlow(false)
    val isShowAskExit: StateFlow<Boolean>
        get() = _isShowAskExit

    fun setIsShowAskExit(value: Boolean) {
        _isShowAskExit.value = value
    }

    private val _isLogin = MutableStateFlow(false)
    val isLogin: StateFlow<Boolean>
        get() = _isLogin

    fun setIsLogin(value: Boolean) {
        _isLogin.value = value
    }

    // 查看他人状态

    private val _isShowState = MutableStateFlow(false)
    val isShowState: StateFlow<Boolean>
        get() = _isShowState

    fun setIsShowState(value: Boolean) {
        _isShowState.value = value
    }

    private val _personNameBeingViewed = MutableStateFlow("")
    val personNameBeingViewed: StateFlow<String>
        get() = _personNameBeingViewed

    fun setPersonNameBeingViewed(value: String) {
        _personNameBeingViewed.value = value
    }

    private val _personQQBeingViewed = MutableStateFlow("")
    val personQQBeingViewed: StateFlow<String>
        get() = _personQQBeingViewed

    fun setPersonQQBeingViewed(value: String) {
        _personQQBeingViewed.value = value
    }

    private val _personIsOnlineBeingViewed = MutableStateFlow(false)
    val personIsOnlineBeingViewed: StateFlow<Boolean>
        get() = _personIsOnlineBeingViewed

    fun setPersonIsOnlineBeingViewed(value: Boolean) {
        _personIsOnlineBeingViewed.value = value
    }

    private val _personSynopsisBeingViewed = MutableStateFlow("")
    val personSynopsisBeingViewed: StateFlow<String>
        get() = _personSynopsisBeingViewed

    fun setPersonSynopsisBeingViewed(value: String) {
        _personSynopsisBeingViewed.value = value
    }

    private val _personRegisterAddressBeingViewed = MutableStateFlow("")
    val personRegisterAddressBeingViewed: StateFlow<String>
        get() = _personRegisterAddressBeingViewed

    fun setPersonRegisterAddressBeingViewed(value: String) {
        _personRegisterAddressBeingViewed.value = value
    }

    private val _personLoginAddressBeingViewed = MutableStateFlow("")
    val personLoginAddressBeingViewed: StateFlow<String>
        get() = _personLoginAddressBeingViewed

    fun setPersonLoginAddressBeingViewed(value: String) {
        _personLoginAddressBeingViewed.value = value
    }

    private val _personLastAccessTimeBeingViewed = MutableStateFlow("")
    val personLastAccessTimeBeingViewed: StateFlow<String>
        get() = _personLastAccessTimeBeingViewed

    fun setPersonLastAccessTimeBeingViewed(value: String) {
        _personLastAccessTimeBeingViewed.value = value
    }

    private val _personStepCountBeingViewed = MutableStateFlow(-1)
    val personStepCountBeingViewed: StateFlow<Int>
        get() = _personStepCountBeingViewed

    fun setPersonStepCountBeingViewed(value: Int) {
        _personStepCountBeingViewed.value = value
    }

    private val _personLoginCountBeingViewed = MutableStateFlow(-1)
    val personLoginCountBeingViewed: StateFlow<Int>
        get() = _personLoginCountBeingViewed

    fun setPersonLoginCountBeingViewed(value: Int) {
        _personLoginCountBeingViewed.value = value
    }

    private val _personNameBeingChat = MutableStateFlow("")
    val personNameBeingChat: StateFlow<String>
        get() = _personNameBeingChat

    fun setPersonNameBeingChat(value: String) {
        _personNameBeingChat.value = value
    }

    private val _personQQBeingChat = MutableStateFlow("")
    val personQQBeingChat: StateFlow<String>
        get() = _personQQBeingChat

    fun setPersonQQBeingChat(value: String) {
        _personQQBeingChat.value = value
    }

    private val _personIsOnlineBeingChat = MutableStateFlow(false)
    val personIsOnlineBeingChat: StateFlow<Boolean>
        get() = _personIsOnlineBeingChat

    fun setPersonIsOnlineBeingChat(value: Boolean) {
        _personIsOnlineBeingChat.value = value
    }

    private val _unreadName = MutableStateFlow("")
    val unreadName: StateFlow<String>
        get() = _unreadName

    fun setUnreadName(value: String) {
        _unreadName.value = value
    }

    private val _unreadCountInChat = MutableStateFlow(0)
    val unreadCountInChat: StateFlow<Int>
        get() = _unreadCountInChat

    fun setUnreadCountInChat(value: Int) {
        _unreadCountInChat.value = value
    }

    private val _processedUsers = MutableStateFlow<List<String>>(emptyList())
    val processedUsers: StateFlow<List<String>> get() = _processedUsers

    fun addProcessedUser(username: String) {
        _processedUsers.value += username
    }

    fun removeProcessedUser(username: String) {
        _processedUsers.value = _processedUsers.value.filter { it != username }
    }

    private val _chatSelection1 = MutableStateFlow(false)
    val chatSelection1: StateFlow<Boolean>
        get() = _chatSelection1

    fun setChatSelection1(value: Boolean) {
        _chatSelection1.value = value
    }

    private val _chatSelection2 = MutableStateFlow(false)
    val chatSelection2: StateFlow<Boolean>
        get() = _chatSelection2

    fun setChatSelection2(value: Boolean) {
        _chatSelection2.value = value
    }

    private val _chatSelection3 = MutableStateFlow(false)
    val chatSelection3: StateFlow<Boolean>
        get() = _chatSelection3

    fun setChatSelection3(value: Boolean) {
        _chatSelection3.value = value
    }

    private val _chatIsChatMessageAnimation = MutableStateFlow(false)
    val chatIsChatMessageAnimation: StateFlow<Boolean>
        get() = _chatIsChatMessageAnimation

    fun setChatIsChatMessageAnimation(value: Boolean) {
        _chatIsChatMessageAnimation.value = value
    }

    private val _isDeleteChatMessageOpen = MutableStateFlow(false)
    val isDeleteChatMessageOpen: StateFlow<Boolean>
        get() = _isDeleteChatMessageOpen

    fun setIsDeleteChatMessageOpen(value: Boolean) {
        _isDeleteChatMessageOpen.value = value
    }

    private val _isUpdateChatList = MutableStateFlow(true)
    val isUpdateChatList: StateFlow<Boolean>
        get() = _isUpdateChatList

    fun setIsUpdateChatList(value: Boolean) {
        _isUpdateChatList.value = value
    }

    private val _isOpenMoreFunction = MutableStateFlow(false)
    val isOpenMoreFunction: StateFlow<Boolean>
        get() = _isOpenMoreFunction

    fun setIsOpenMoreFunction(value: Boolean) {
        _isOpenMoreFunction.value = value
    }

    private val _isSelectImageSuccessfully = MutableStateFlow(false)
    val isSelectImageSuccessfully: StateFlow<Boolean>
        get() = _isSelectImageSuccessfully

    fun setIsSelectImageSuccessfully(value: Boolean) {
        _isSelectImageSuccessfully.value = value
    }

    private val _isInChat = MutableStateFlow(false)
    val isInChat: StateFlow<Boolean>
        get() = _isInChat

    fun setIsInChat(value: Boolean) {
        _isInChat.value = value
    }

}

class AppLifecycleObserver : LifecycleObserver {

    private var isAppInForeground = true

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isAppInForeground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isAppInForeground = false
    }

    fun isAppInForeground(): Boolean {
        return isAppInForeground
    }
}

class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        setContent {

            ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleObserver())

            requestPermissions(this)

            // 初始化生命周期观察者
            val appLifecycleObserver = AppLifecycleObserver()

            // 注册生命周期观察者
            lifecycle.addObserver(appLifecycleObserver)

            val isLogin = Global.isLogin.collectAsState()
            val isInChat = Global.isInChat.collectAsState()

            var showBodySensorsPermissionDialog by remember { mutableStateOf(false) }
            var showActivityRecognitionPermissionDialog by remember { mutableStateOf(false) }
            var showNotificationPermissionDialog by remember { mutableStateOf(false) }
            var showManageFilePermissionDialog by remember { mutableStateOf(false) }

            val context = LocalContext.current

            val isDeleteChatMessageOpen = Global.isDeleteChatMessageOpen.collectAsState()

            // 循环获取后端配置
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    while (true) {
                        Global.setIsDeleteChatMessageOpen(getDeleteMessageListSwitch().toBooleanStrictOrNull() == true) // 获取是否删除聊天记录

                        if (isDeleteChatMessageOpen.value) {
                            deleteFile(
                                context, "ChatMessage/Message/"
                            )
                            deleteFile(
                                context, "ChatMessage/Count/"
                            )
                        }

                        delay(3000)
                    }
                }
            }


            //在 Composable 中根据需要显示弹窗
            if (showBodySensorsPermissionDialog) {
                CheckBodySensorsPermission(
                    "申请身体传感器权限",
                    "应用需要'运动健康'权限和'身体传感器'权限才能正常工作，请点击下方“设置”按钮进行设置。",
                    onSettingsClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    },
                    onCancelClick = {
                        exitProcess(0)
                    }
                )
            }

            //在 Composable 中根据需要显示弹窗
            if (showActivityRecognitionPermissionDialog) {
                CheckBodySensorsPermission(
                    "申请运动健康权限",
                    "应用需要'运动健康'权限和'身体传感器'权限才能正常工作，请点击下方“设置”按钮进行设置。",
                    onSettingsClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    },
                    onCancelClick = {
                        exitProcess(0)
                    }
                )
            }

            //在 Composable 中根据需要显示弹窗
            if (showNotificationPermissionDialog) {
                NotificationPermissionDialog(
                    onSettingsClick = {
                        //用户点击“设置”按钮时，跳转到通知权限设置页面
                        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                        context.startActivity(intent)
                    },
                    onCancelClick = {
                        exitProcess(0)
                    }
                )
            }


            //在 Composable 中根据需要显示弹窗
            if (showManageFilePermissionDialog) {
                RequestAllFilesAccessPermission(
                    onSettingsClick = {
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    },
                    onCancelClick = {
                        exitProcess(0)
                    }
                )
            }


            LaunchedEffect(Unit) {
                while (!Global.isGiveNotificationPermissions) {
                    val notificationsEnabled = NotificationManagerCompat.from(context)
                        .areNotificationsEnabled()
                    showNotificationPermissionDialog = !notificationsEnabled
                    Global.isGiveNotificationPermissions = notificationsEnabled
                    delay(500)
                    if (showNotificationPermissionDialog != !notificationsEnabled) break
                }
            }


            LaunchedEffect(Unit) {
                while (!Global.isGiveManageFilePermissions) {
                    if (!Environment.isExternalStorageManager()) {
                        showManageFilePermissionDialog = true
                    } else {
                        Global.isGiveManageFilePermissions = false
                        showManageFilePermissionDialog = false
                    }
                    delay(500)
                }
            }

            LaunchedEffect(Unit) {
                while (!Global.isGiveBodySensorsPermissions) {
                    val bodySensorsPermissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BODY_SENSORS
                    ) == PackageManager.PERMISSION_GRANTED

                    showBodySensorsPermissionDialog = !bodySensorsPermissionGranted
                    Global.isGiveBodySensorsPermissions = bodySensorsPermissionGranted

                    delay(500)

                    // 如果权限状态没有变化，则跳出循环
                    if (showBodySensorsPermissionDialog != !bodySensorsPermissionGranted) break
                }
            }

            LaunchedEffect(Unit) {
                while (!Global.isGiveActivityRecognitionPermissions) {
                    val activityRecognitionPermissionGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACTIVITY_RECOGNITION
                    ) == PackageManager.PERMISSION_GRANTED

                    showActivityRecognitionPermissionDialog = !activityRecognitionPermissionGranted
                    Global.isGiveActivityRecognitionPermissions =
                        activityRecognitionPermissionGranted

                    delay(500)

                    // 如果权限状态没有变化，则跳出循环
                    if (showActivityRecognitionPermissionDialog != !activityRecognitionPermissionGranted) break
                }
            }

            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    // 调用 isAppInForeground 方法
                    val isInForeground = appLifecycleObserver.isAppInForeground()

                    writeToFile(applicationContext, "", "isInForeground", isInForeground.toString())

                    writeToFile(context, "", "isLogin", isLogin.value.toString())

                    writeToFile(context, "", "isInChat", isInChat.value.toString())

                    if (!isProcessRunning(context, "com.syc.world.RescueProcessService")) {
                        restartRescueProcess(context)
                    }
                    if (!isProcessRunning(context, "com.syc.world.ForegroundService")) {
                        restartForegroundServiceProcess(context)
                    }

                    delay(100)
                }
            }

            // 申请电池白名单
            requestIgnoreBatteryOptimizations(context)

            val colorMode = remember { mutableIntStateOf(0) }
            val darkMode =
                colorMode.intValue == 2 || (isSystemInDarkTheme() && colorMode.intValue == 0)
            LaunchedEffect(Unit) {
                getColorMode(context).collect { savedIndex ->
                    colorMode.intValue = savedIndex
                }
            }
            DisposableEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(
                        android.graphics.Color.TRANSPARENT,
                        android.graphics.Color.TRANSPARENT
                    ) { darkMode },
                )

                window.isNavigationBarContrastEnforced =
                    false  // Xiaomi moment, this code must be here

                onDispose {}
            }
            AppTheme(colorMode = colorMode.intValue) {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    AllHome(
                        colorMode = colorMode,
                        modifier = Modifier.padding(),
                        intent = intent
                    )
                }
            }
        }
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

fun restartRescueProcess(context: Context) {
    val intent = Intent(context, RescueProcessService::class.java)
    context.startService(intent)
}

private fun restartForegroundServiceProcess(context: Context) {
    val intent = Intent(context, ForegroundService::class.java)
    context.startService(intent)
}


fun writeToFile(context: Context, child: String, filename: String, content: String) {
    val dir = File(context.filesDir, child)

    if (!dir.exists()) {
        dir.mkdirs()
    }

    val file = File(dir, filename)

    FileOutputStream(file).use { fos ->
        OutputStreamWriter(fos).use { writer ->
            writer.write(content)
        }
    }
}

fun deleteFile(context: Context, filename: String): Boolean {
    // 获取文件或目录的路径
    val file = File(context.filesDir, filename)

    return if (file.exists()) {
        // 如果是文件，直接删除
        if (file.isFile) {
            file.delete()
        } else {
            // 如果是目录，递归删除目录及其中的所有文件
            file.deleteRecursively()
        }
    } else {
        false // 文件或目录不存在，返回 false
    }
}

fun readFromFile(context: Context, filename: String): String {
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

fun readFile(filename: String): String {
    val file = File(filename)
    return if (file.exists()) {
        try {
            val inputStream = FileInputStream(filename)
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

fun requestPermissions(context: Context) {
    val activityContext = context as? Activity  // 尝试将 context 转换为 Activity

    activityContext?.let {
        // 检查是否具有 "ACTIVITY_RECOGNITION" 权限
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                it,  // 使用 Activity 上下文
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                10001
            )
        }

        // 检查是否具有 "BODY_SENSORS" 权限
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.BODY_SENSORS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                it,  // 使用 Activity 上下文
                arrayOf(Manifest.permission.BODY_SENSORS),
                10002
            )
        }

    } ?: run {
        // 如果 context 不是 Activity 类型，则给出错误提示
        Log.e("权限问题", "Context is not an instance of Activity")
    }
}

@SuppressLint("ServiceCast")
fun createStepCountNotification(context: Context, title: String, content: String) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 创建通知渠道，设置为低重要性，避免打扰用户
    val channelId = "SYC_StepCount"
    val channelName = "酸夜沉空间计步服务"
    val importance = NotificationManager.IMPORTANCE_LOW // 低重要性，避免打扰
    val notificationChannel = NotificationChannel(channelId, channelName, importance).apply {
        setSound(null, null) // 禁用声音
        enableVibration(false) // 禁用震动
        vibrationPattern = longArrayOf(0) // 震动模式为空
        setShowBadge(false) // 不在桌面图标上显示通知角标
        lockscreenVisibility = Notification.VISIBILITY_SECRET // 锁屏不显示
    }
    notificationManager.createNotificationChannel(notificationChannel)

    // 创建点击通知后跳转的 Intent，跳转到应用首页
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    // 创建 PendingIntent
    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 创建通知
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.step_count) // 设置通知图标
        .setContentTitle(title) // 设置通知标题
        .setContentText(content) // 设置通知内容
        .setOngoing(true) // 设置通知为常驻通知
        .setAutoCancel(false) // 禁止自动消失
        .setPriority(NotificationCompat.PRIORITY_LOW) // 低优先级，避免打扰
        .setSilent(true) // Android 11+ 进一步确保静默
        .setDefaults(Notification.DEFAULT_LIGHTS) // 仅使用 LED 提示（如果有的话）
        .setContentIntent(pendingIntent) // 设置点击通知时打开应用首页

    // 发送通知
    notificationManager.notify(10001, notificationBuilder.build())
}

@SuppressLint("ServiceCast")
suspend fun createChatMessageNotification(
    imageUrl: String,
    context: Context,
    title: String,
    content: String,
    timestamp: Long = System.currentTimeMillis(),
    link: String? = null
) {
    // 如果在前台，播放声音并退出
    if (isInForeground.value) {
        //playSound(context)
        Log.d("通知问题", "在前台，只播放铃声")
        return
    }

    // 后台逻辑：创建通知
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // 创建通知渠道
    val channelId = "SYC_ChatMessage"
    val channelName = "酸夜沉空间消息通知服务"
    val importance = NotificationManager.IMPORTANCE_HIGH

    NotificationChannel(channelId, channelName, importance).apply {
        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/ring")
        setSound(
            soundUri,
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 500, 1000)
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }.also { notificationManager.createNotificationChannel(it) }

    // 创建PendingIntent
    val intent = if (!link.isNullOrEmpty()) {
        Intent(Intent.ACTION_VIEW, Uri.parse(link))
    } else {
        Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }

    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // 加载头像
    val avatarBitmap = withContext(Dispatchers.IO) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .circleCrop()
            .submit()
            .get()
    }

    // 构建通知（移除冗余声音和震动设置）
    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.new_message)
        .setLargeIcon(avatarBitmap)
        .setContentTitle(title)
        .setContentText(content)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
        .setWhen(timestamp)
        .setShowWhen(true)

    // 发送通知
    notificationManager.notify(10004, notificationBuilder.build())
}

@SuppressLint("ServiceCast")
suspend fun createMomentsMessageNotification(
    imageUrl: String,
    context: Context,
    title: String,
    content: String,
    timestamp: Long = System.currentTimeMillis()
) {
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val channelId = "SYC_MomentsMessage"
    val channelName = "酸夜沉空间动态通知服务"
    val importance = NotificationManager.IMPORTANCE_HIGH

    val channel = NotificationChannel(channelId, channelName, importance).apply {
        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/ring")
        setSound(
            soundUri,
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 500, 1000)
        setShowBadge(false)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }
    notificationManager.createNotificationChannel(channel)

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val avatarBitmap = withContext(Dispatchers.IO) {
        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .circleCrop()
            .submit()
            .get()
    }

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.new_message)
        .setLargeIcon(avatarBitmap)
        .setContentTitle(title)
        .setContentText(content)
        .setContentIntent(pendingIntent)
        .setOngoing(false)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setVibrate(longArrayOf(0, 500, 1000))
        .setSound(Uri.parse("android.resource://${context.packageName}/raw/ring"))
        .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
        .setWhen(timestamp) // 设置消息时间
        .setShowWhen(true) // 确保时间显示

    notificationManager.notify(10003, notificationBuilder.build())
}

// 电池优化
@SuppressLint("BatteryLife")
fun requestIgnoreBatteryOptimizations(context: Context) {
    val packageName = context.packageName
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    //检查应用是否已经在电池优化白名单中
    if (powerManager.isIgnoringBatteryOptimizations(packageName)) {
        //无需询问
    } else {
        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
            data = Uri.parse("package:$packageName")
        }

        //检查设备是否支持忽略电池优化设置
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "此设备不支持忽略电池优化设置", Toast.LENGTH_LONG).show()
        }
    }
}


@Composable
fun NotificationPermissionDialog(
    onSettingsClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelClick,
        title = {
            androidx.compose.material3.Text(
                text = "申请通知权限",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            androidx.compose.material3.Text(
                text = "应用需要通知权限才能正常工作，请点击下方“设置”按钮进行设置。",
                style = MaterialTheme.typography.bodyLarge
            )
        },
        confirmButton = {
            Button(
                onClick = onSettingsClick
            ) {
                androidx.compose.material3.Text(
                    text = "设置",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        dismissButton = {
            Button(
                onClick = onCancelClick
            ) {
                androidx.compose.material3.Text(
                    text = "取消",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    )
}


@Composable
fun CheckBodySensorsPermission(
    title: String,
    text: String,
    onSettingsClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AlertDialog(
            onDismissRequest = onSettingsClick,
            title = {
                androidx.compose.material3.Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                androidx.compose.material3.Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onSettingsClick()
                    }
                ) {
                    androidx.compose.material3.Text(
                        text = "设置",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onCancelClick
                ) {
                    androidx.compose.material3.Text(
                        text = "取消",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Composable
fun RequestAllFilesAccessPermission(
    onSettingsClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AlertDialog(
            onDismissRequest = onSettingsClick,
            title = {
                androidx.compose.material3.Text(
                    text = "申请访问所有目录权限",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                androidx.compose.material3.Text(
                    text = "应用需要目录权限才能正常工作，请点击下方“设置”按钮进行设置。",
                    style = MaterialTheme.typography.bodyLarge
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                        intent.data = Uri.parse("package:${context.packageName}")
                        context.startActivity(intent)
                    }
                ) {
                    androidx.compose.material3.Text(
                        text = "设置",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            dismissButton = {
                Button(
                    onClick = onCancelClick
                ) {
                    androidx.compose.material3.Text(
                        text = "取消",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        )
    }
}

@Immutable
class SpringEasing @JvmOverloads constructor(
    private val damping: Float = 0.85f,
    private val response: Float = 0.3f,
    private val mass: Float = 1.0f,
    private val acceleration: Float = 0.0f
) : Easing {
    var duration: Long = 1000L
        private set
    private var g = 0.0
    private var inputScale = 1.0f
    private var omega = 0.0
    private var p = 0.0
    private var q = 0.0
    private lateinit var solution: SpringSolution
    private var velocity = 0.0f
    private var xStar = 0.0
    private var zeta = 0.0

    init {
        updateParameters()
    }

    override fun transform(fraction: Float): Float {
        if (fraction == 1.0f) {
            return 1.0f
        }
        val f2 = fraction * this.inputScale
        val x = solution.x(f2).toFloat()
        this.velocity = solution.dX(f2).toFloat()
        return x
    }

    private fun updateParameters() {
        val d = damping.toDouble()
        this.zeta = d
        val d2 = 6.283185307179586 / this.response
        this.omega = d2
        val f = this.mass
        val d3 = (((d * 2.0) * d2) * f) / f
        this.p = d3
        val d4 = ((d2 * d2) * f) / f
        this.q = d4
        val d5 = acceleration.toDouble()
        this.g = d5
        val d6 = ((-d5) / d4) + 1.0
        this.xStar = d6
        val d7 = (d3 * d3) - (d4 * 4.0)
        val d8 = 0.0 - d6
        this.solution = if (d7 > 0.0) {
            OverDampingSolution(d7, d8, d3, velocity.toDouble(), d6)
        } else if (d7 == 0.0) {
            CriticalDampingSolution(d8, d3, velocity.toDouble(), d6)
        } else {
            UnderDampingSolution(d7, d8, d3, velocity.toDouble(), d6)
        }
        val solveDuration = (solveDuration(d7) * 1000.0).toLong()
        this.duration = solveDuration
        this.inputScale = (solveDuration.toFloat()) / 1000.0f
    }

    private fun solveDuration(d: Double): Double {
        var d2: Double
        var d3 = 0.0
        val d4 = if (d >= 0.0) 0.001 else 1.0E-4
        val d5 = this.g
        var d6 = 1.0
        if (d5 == 0.0) {
            var f = 0.0f
            while (abs(d3 - 1.0) > d4) {
                f += 0.001f
                d3 = solution.x(f)
                val dX = solution.dX(f)
                if (abs(d3 - 1.0) <= d4 && dX <= 5.0E-4) {
                    break
                }
            }
            return f.toDouble()
        }
        val solve = solution.solve(0.0, this.q, d5, this.xStar)
        val d7 = this.q
        val d8 = this.xStar
        val d9 = d7 * d8 * d8
        val d10 = (solve - d9) * d4
        var d11 = 1.0
        var solve2 = solution.solve(1.0, d7, this.g, d8)
        var d12 = 0.0
        while (true) {
            d2 = d9 + d10
            if (solve2 <= d2) {
                break
            }
            val d13 = d11 + d6
            d12 = d11
            d6 = 1.0
            d11 = d13
            solve2 = solution.solve(d13, this.q, this.g, this.xStar)
        }
        do {
            val d14 = (d12 + d11) / 2.0
            if (solution.solve(d14, this.q, this.g, this.xStar) > d2) {
                d12 = d14
            } else {
                d11 = d14
            }
        } while (d11 - d12 >= d4)
        return d11
    }

    internal abstract inner class SpringSolution {
        abstract fun dX(f: Float): Double

        abstract fun x(f: Float): Double

        fun solve(d: Double, d2: Double, d3: Double, d4: Double): Double {
            val f = d.toFloat()
            val x = x(f)
            val dX = dX(f)
            return (((d2 * x) * x) + (dX * dX)) - ((d3 * 2.0) * (x - d4))
        }
    }

    internal inner class CriticalDampingSolution(
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val c1: Double
        private val c2: Double
        private val r: Double
        private val xStar: Double

        init {
            val d6 = (-d3) / 2.0
            this.r = d6
            this.c1 = d2
            this.c2 = d4 - (d2 * d6)
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return ((this.c1 + (this.c2 * d)) * exp(this.r * d)) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = this.c1
            val d2 = this.r
            val d3 = this.c2
            val d4 = f.toDouble()
            return ((d * d2) + (d3 * ((d2 * d4) + 1.0))) * exp(d2 * d4)
        }
    }

    internal inner class OverDampingSolution(
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val c1: Double
        private val c2: Double
        private val r1: Double
        private val r2: Double
        private val xStar: Double

        init {
            val sqrt = sqrt(d)
            val d6 = (sqrt - d3) / 2.0
            this.r1 = d6
            val d7 = ((-sqrt) - d3) / 2.0
            this.r2 = d7
            this.c1 = (d4 - (d2 * d7)) / sqrt
            this.c2 = (-(d4 - (d6 * d2))) / sqrt
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return (this.c1 * exp(this.r1 * d)) + (this.c2 * exp(this.r2 * d)) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = this.c1
            val d2 = this.r1
            val d3 = f.toDouble()
            val exp = d * d2 * exp(d2 * d3)
            val d4 = this.c2
            val d5 = this.r2
            return exp + (d4 * d5 * exp(d5 * d3))
        }
    }

    internal inner class UnderDampingSolution(
        d: Double,
        d2: Double,
        d3: Double,
        d4: Double,
        d5: Double
    ) :
        SpringSolution() {
        private val alpha: Double
        private val beta: Double
        private val c1: Double
        private val c2: Double
        private val xStar: Double

        init {
            val d6 = (-d3) / 2.0
            this.alpha = d6
            val sqrt = sqrt(-d) / 2.0
            this.beta = sqrt
            this.c1 = d2
            this.c2 = (d4 - (d2 * d6)) / sqrt
            this.xStar = d5
        }

        override fun x(f: Float): Double {
            val d = f.toDouble()
            return (exp(this.alpha * d) * ((this.c1 * cos(this.beta * d)) + (this.c2 * sin(
                this.beta * d
            )))) + this.xStar
        }

        override fun dX(f: Float): Double {
            val d = f.toDouble()
            val exp = exp(this.alpha * d)
            val d2 = this.c1 * this.alpha
            val d3 = this.c2
            val d4 = this.beta
            val cos = (d2 + (d3 * d4)) * cos(d4 * d)
            val d5 = this.c2 * this.alpha
            val d6 = this.c1
            val d7 = this.beta
            return exp * (cos + ((d5 - (d6 * d7)) * sin(d7 * d)))
        }
    }
}

@Composable
fun AllHome(
    modifier: Modifier = Modifier,
    colorMode: MutableState<Int> = remember { mutableIntStateOf(0) },
    intent: Intent? = null
) {
    val context = LocalContext.current
    // 读取账号
    val appInternalDir = context.filesDir
    val name = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 0)

    var unReadMessageCount: Int

    if (File(appInternalDir, "username").exists() && File(
            appInternalDir,
            "username"
        ).length() > 0
    ) {
        BufferedReader(
            FileReader(
                File(
                    appInternalDir,
                    "username"
                )
            )
        ).use { reader ->
            val savedUserInput = reader.readLine()
            if (savedUserInput.isNotEmpty()) {
                name.value = savedUserInput
            }
        }
    }
    if (File(appInternalDir, "password").exists() && File(
            appInternalDir,
            "password"
        ).length() > 0
    ) {
        BufferedReader(
            FileReader(
                File(
                    appInternalDir,
                    "password"
                )
            )
        ).use { reader ->
            val savedUserInput = reader.readLine()
            if (savedUserInput.isNotEmpty()) {
                password.value = savedUserInput
            }
        }
    }
    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.background,
        tint = HazeTint(MiuixTheme.colorScheme.background.copy(0.75f)),
        blurRadius = 25.dp,
        noiseFactor = 0f
    )
    val navController = rememberNavController()
    val windowWidth = getWindowSize().width
    val easing = SpringEasing(0.95f, 0.4f)//CubicBezierEasing(0.4f, 0.95f, 0.2f, 1f)
    val duration = easing.duration.toInt()
    val client = OkHttpClient()
    val postId = remember { mutableIntStateOf(0) }
    val isReply = remember { mutableStateOf(false) }
    val postlist = remember { mutableStateListOf(emptyList<Post>()) }
    postlist.clear()
    LaunchedEffect(password.value) {
        while (Global.url == "") {
            withContext(Dispatchers.IO) {
                Global.url = getUrl()
            }
            if (Global.url != "" && Global.url.contains("http")) {
                break
            }
            delay(2000)
        }
        if (password.value != "" && name.value != "" && Global.url != "" && Global.url.contains("http")) {
            val result = withContext(Dispatchers.IO) {
                val url = "${Global.url}/syc/login.php"

                // 创建请求体
                val formBody = FormBody.Builder()
                    .add("username", name.value)
                    .add("password", password.value)
                    .build()

                // 构建 POST 请求
                val request = Request.Builder()
                    .url(url)
                    .post(formBody)  // 使用 .post() 方法
                    .build()

                try {
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val responseBody = response.body?.string() ?: ""
                        responseBody
                    } else {
                        ""
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    ""
                } catch (e: CancellationException) {
                    e.printStackTrace()
                    ""
                }
            }
            if (result != "") {
                Log.d("stzy", result)
                val jsonObject = JSONObject(result)
                if (jsonObject.getString("status") == "success") {
                    Global.username = name.value
                    Global.password = password.value
                    Global.setIsLogin(true)
                    navController.navigate("Main") {
                        popUpTo("loading") {
                            inclusive = true
                        }
                    }
                } else {
                    navController.navigate("Regin") {
                        popUpTo("loading") {
                            inclusive = true
                        }
                    }
                }
            }
        } else {
            navController.navigate("Regin") {
                popUpTo("loading") {
                    inclusive = true
                }
            }
        }
        intent?.data?.let {
            Log.d(
                "导航",
                "host:${it.host} path:${it.path} scheme:${it.scheme} query:${it.query} fragment:${it.fragment} authority:${it.authority} port:${it.port} schemeSpecificPart:${it.schemeSpecificPart} toString:${it.toString()}"
            )
            if (it.host == "moment") {
                val QPpostId = it.getQueryParameter("postId")
                if (QPpostId != null) {
                    postId.intValue = QPpostId.toInt()
                }
                val QPisReply = it.getQueryParameter("isReply")
                if (QPisReply == "true") {
                    isReply.value = true
                }
                navController.navigate("Dynamic")
            }
            if (it.host == "chat") {
                withContext(Dispatchers.IO) {
                    while (true) {
                        if (Global.username.trim().isNotEmpty() && Global.password.trim()
                                .isNotEmpty()
                        ) {
                            Log.d("跳转问题", "成功接收到跳转请求")
                            if (it.getQueryParameter("name") != null && it.getQueryParameter("qq") != null) {
                                it.getQueryParameter("name")
                                    ?.let { it1 -> Global.setPersonNameBeingChat(it1) }
                                it.getQueryParameter("qq")
                                    ?.let { it1 -> Global.setPersonQQBeingChat(it1) }
                                val informationResult =
                                    getUserInformation(it.getQueryParameter("name") ?: "")
                                if (informationResult.isNotEmpty() && isJson(informationResult)) {
                                    val userInfo = parseUserInfo(informationResult)
                                    if (userInfo != null) {
                                        if (userInfo.online.isNotEmpty()) {
                                            Log.d("跳转问题", "online: ${userInfo.online}")
                                            Global.setPersonIsOnlineBeingChat(userInfo.online == "在线")
                                        }
                                    }
                                }
                                val chatList = getChatList(Global.username, Global.password)
                                val unreadName = Global.unreadName.value
                                val unreadCountInChat = Global.unreadCountInChat.value
                                val processedUsers = Global.processedUsers.value

                                if (chatList.first == "success") {
                                    withContext(Dispatchers.Main) {
                                        chatList.second.map { chatItem ->
                                            if (chatItem.username == it.getQueryParameter("name") && chatItem.qq == it.getQueryParameter(
                                                    "qq"
                                                )
                                            ) {

                                                val readResult =
                                                    readFromFile(
                                                        context,
                                                        "/ChatMessage/NewMessage/${chatItem.username}"
                                                    )

                                                if (readResult != "404" && readResult.toIntOrNull() != null) {
                                                    Global.setUnreadName(chatItem.username)
                                                    unReadMessageCount = readResult.toInt()
                                                } else {
                                                    unReadMessageCount = 0
                                                }

                                                // 如果有新的消息且不为空，则更新未读消息数
                                                if (chatItem.username == unreadName) {
                                                    if (chatItem.username in processedUsers) {
                                                        Global.setUnreadCountInChat(unreadCountInChat - unReadMessageCount)
                                                        deleteFile(
                                                            context,
                                                            "ChatMessage/NewMessage/${chatItem.username}"
                                                        )
                                                        Global.removeProcessedUser(chatItem.username)
                                                    }
                                                }
                                                Global.setIsUpdateChatList(true)
                                                navController.navigate("ChatUi")
                                            }
                                        }
                                    }
                                } else {
                                    Log.d("跳转问题", "聊天列表获取失败")
                                }
                            }
                            break
                        }
                    }
                }
            }
        }
    }
    val notidifference = remember { mutableStateOf(0) }

    Column {
        NavHost(navController = navController, startDestination = "loading", enterTransition = {
            slideInHorizontally(
                initialOffsetX = { windowWidth },
                animationSpec = tween(duration, 0, easing = easing)
            )
        },
            exitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            popEnterTransition = {
                slideInHorizontally(
                    initialOffsetX = { -windowWidth / 5 },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            popExitTransition = {
                slideOutHorizontally(
                    targetOffsetX = { windowWidth },
                    animationSpec = tween(duration, 0, easing = easing)
                )
            },
            sizeTransform = {
                SizeTransform(clip = true)  // 允许页面在过渡时进行缩放，但不裁剪内容
            }
        ) {
            composable("loading") { Loading() }
            composable("Main") {
                Main(
                    modifier = modifier,
                    navController,
                    colorMode = colorMode,
                    hazeState,
                    hazeStyle,
                    postId = postId,
                    isReply = isReply,
                    postList = postlist,
                    notidifference,
                    pagerState = pagerState
                )
            }
            composable("Regin") { Regin(hazeStyle, hazeState, navController) }
            composable("PersonInfo") { PersonInfo(navController) }
            composable("Publish_Dynamic") { Publist_Dynamic(navController, hazeStyle, hazeState) }
            composable("ChatUi") { ChatUi(navController, pagerState) }
            composable("ChatSettings") { ChatSettings(navController) }
            composable("Dynamic") {
                Dynamic(
                    navController,
                    postId.intValue,
                    hazeState,
                    hazeStyle,
                    isReply
                )
            }
            composable("Notification") {
                Notification(
                    navController,
                    hazeState,
                    hazeStyle,
                    postId,
                    isReply,
                    notidifference.value
                )
            }
        }
    }
}

@Composable
fun Loading() {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center) // 内容居中
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally // 水平居中
            ) {
                // 圆形进度条
                CircularProgressIndicator(
                    modifier = Modifier.size(50.dp), // 设置进度条的大小
                    color = MiuixTheme.colorScheme.primary, // 进度条颜色
                    strokeWidth = 6.dp // 进度条宽度
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "自动登录中...",
                )
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
fun Main(
    modifier: Modifier = Modifier,
    navController: NavController,
    colorMode: MutableState<Int>,
    hazeState: HazeState,
    hazeStyle: HazeStyle,
    postId: MutableState<Int>,
    isReply: MutableState<Boolean>,
    postList: SnapshotStateList<List<Post>>,
    notidifference: MutableState<Int>,
    pagerState: PagerState
) {
    val topAppBarScrollBehavior0 =
        MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 =
        MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 =
        MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior3 =
        MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0,
        topAppBarScrollBehavior1,
        topAppBarScrollBehavior2,
        topAppBarScrollBehavior3
    )
    var targetPage by remember { mutableIntStateOf(pagerState.currentPage) }
    val coroutineScope = rememberCoroutineScope()
    val currentScrollBehavior = when (pagerState.currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        2 -> topAppBarScrollBehaviorList[2]
        else -> topAppBarScrollBehaviorList[3]
    }
    val items = listOf(
        NavigationItem("首页", ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem("消息", ImageVector.vectorResource(id = R.drawable.chat)),
        if (pagerState.currentPage == 2) NavigationItem(
            "刷新",
            ImageVector.vectorResource(id = R.drawable.refresh)
        ) else NavigationItem("动态", ImageVector.vectorResource(id = R.drawable.zone)),
        NavigationItem("我的", ImageVector.vectorResource(id = R.drawable.my))
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }
    val tabTexts = listOf("默认", "最新", "热度")
    val selectedTab = rememberSaveable { mutableIntStateOf(0) }
    val isTab = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.ring) }
    LaunchedEffect(Unit) {
        while (true) {
            val gson = Gson()
            val listFile = File(context.filesDir, "Moments/list.json")
            val listSize = if (listFile.exists()) {
                val listJson = listFile.readText()
                if (listJson.isNotEmpty()) {
                    gson.fromJson(
                        listJson,
                        Array<ForegroundService.MomentsMessage>::class.java
                    ).size
                } else {
                    0
                }
            } else {
                0
            }

            // 读取 quantity.json 文件
            val quantityFile = File(context.filesDir, "Moments/quantity.json")
            val quantityJson = if (quantityFile.exists()) {
                quantityFile.readText()
            } else {
                "[]"
            }
            val quantity = if (quantityJson.isNotEmpty()) {
                gson.fromJson(
                    quantityJson,
                    Array<ForegroundService.MomentsMessage>::class.java
                ).size
            } else {
                0
            }
            if (notidifference.value != listSize - quantity) {
                if (listSize - quantity != 0) {
                    mediaPlayer.start()
                }
                //mediaPlayer.release()
                notidifference.value = listSize - quantity
            }
            delay(3000)
        }
    }
    Scaffold(modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            if (pagerState.currentPage == 2) {
                TextButton(
                    "发布",
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        navController.navigate("Publish_Dynamic")
                    }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                items = items,
                color = Color.Transparent,
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle
                ),
                showDivider = false,
                selected = targetPage,
                onClick = { index ->
                    if (index == 2) {
                        if (pagerState.currentPage == 2) {
                            isTab.value = true
                        } else {
                            targetPage = index
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                    } else {
                        targetPage = index
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                }
            )
        }, topBar = {
            Column(modifier = if (pagerState.currentPage == 2) {
                Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle, block =
                    fun HazeEffectScope.() {
                        progressive =
                            HazeProgressive.verticalGradient(
                                startIntensity = 1f,
                                endIntensity = 0f
                            )
                    }
                )
            } else Modifier) {
                TopAppBar(scrollBehavior = currentScrollBehavior,
                    color = Color.Transparent,
                    title = when (pagerState.currentPage) {
                        0 -> "首页"
                        1 -> "消息"
                        2 -> "动态"
                        else -> "我的"
                    },
                    modifier = if (pagerState.currentPage != 2) {
                        Modifier.hazeEffect(
                            state = hazeState,
                            style = hazeStyle, block =
                            fun HazeEffectScope.() {
                                progressive =
                                    HazeProgressive.verticalGradient(
                                        startIntensity = 1f,
                                        endIntensity = 0f
                                    )
                            }
                        )
                    } else Modifier,
                    navigationIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                    }, actions = {
                        Box(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    navController.navigate("Notification")
                                    val listFile = File(context.filesDir, "Moments/list.json")
                                    if (listFile.exists()) {
                                        writeToFile(
                                            context,
                                            "Moments",
                                            "quantity.json",
                                            listFile.readText()
                                        )
                                    }
                                },
                                modifier = Modifier,
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.noti),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(30.dp),
                                    colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onBackground)
                                )
                            }
                            if (notidifference.value != 0) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd) // 将数字放在右上角
                                        .background(
                                            color = Color.Red, // 设置角标背景色
                                            shape = CircleShape
                                        )
                                ) {
                                    Text(
                                        text = notidifference.value.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(2.dp)
                                    )
                                }
                            }
                        }
                    })
                AnimatedVisibility(pagerState.currentPage == 2) {
                    TabRow(
                        tabs = tabTexts,
                        backgroundColor = Color.Transparent,
                        selectedTabIndex = selectedTab.intValue,
                        selectedBackgroundColor = MiuixTheme.colorScheme.surface.copy(alpha = 0.8f),
                        cornerRadius = 20.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        selectedTab.intValue = it
                        isTab.value = true
                    }
                }
            }

        }) { padding ->
        Box(
            modifier = Modifier.hazeSource(state = hazeState)
        ) {
            AppHorizontalPager(
                modifier = Modifier.imePadding(),
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                navController = navController,
                colorMode = colorMode,
                postId = postId,
                isReply = isReply,
                selectedTab = selectedTab,
                postList = postList,
                isTab = isTab
            )
        }
    }
    ViewOthersPopup()
    ViewPersonPopup(navController)
}

@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController,
    colorMode: MutableState<Int>,
    postId: MutableState<Int>,
    isReply: MutableState<Boolean>,
    selectedTab: MutableState<Int>,
    postList: SnapshotStateList<List<Post>>,
    isTab: MutableState<Boolean>
) {
    HorizontalPager(
        modifier = modifier,
        pagerState = pagerState,
        userScrollEnabled = true,
        pageContent = { page ->
            when (page) {
                0 -> Home(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                    padding = padding,
                    navController = navController,
                    postId = postId
                )

                1 -> Chat(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                    padding = padding,
                    navController = navController
                )

                2 -> Moments(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    navController = navController,
                    postId = postId,
                    isReply = isReply,
                    selectedTab = selectedTab,
                    postlist = postList,
                    isTab = isTab
                )

                else -> Person(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[3],
                    padding = padding,
                    colorMode = colorMode,
                    navController = navController
                )
            }
        }
    )
}

suspend fun saveColorMode(context: Context, selectedIndex: Int) {
    val colorModeKey = intPreferencesKey("color_mode")
    context.dataStore.edit { preferences ->
        preferences[colorModeKey] = selectedIndex
    }
}

fun getColorMode(context: Context): Flow<Int> {
    val colorModeKey = intPreferencesKey("color_mode")
    return context.dataStore.data.map { preferences ->
        preferences[colorModeKey] ?: 0 // 默认值为 0（Auto_Mode）
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings"
)
