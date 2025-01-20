package com.syc.world

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
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
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.syc.world.ui.theme.AppTheme
import dev.chrisbanes.haze.HazeEffectScope
import dev.chrisbanes.haze.HazeProgressive
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
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
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.system.exitProcess

object Global {

    var username = ""

    var isGiveNotificationPermissions = false
    var isGiveManageFilePermissions = false

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

}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        window.isNavigationBarContrastEnforced = false
        setContent {

            // 前台服务，启动！！！
            val serviceIntent = Intent(this, ForegroundService::class.java)
            startForegroundService(serviceIntent)

            var showNotificationPermissionDialog by remember { mutableStateOf(false) }
            var showManageFilePermissionDialog by remember { mutableStateOf(false) }

            val context = LocalContext.current

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

            // 申请电池白名单
            requestIgnoreBatteryOptimizations(context)

            val isLogin = Global.isLogin.collectAsState()

            // 10s刷新一次在线状态
            LaunchedEffect(isLogin.value) {
                Log.d("在线状态", "isLogin.value发生变化")
                if (isLogin.value) {
                    Log.d("在线状态", "isLogin.value为true")
                    while (true) {
                        withContext(Dispatchers.IO) {
                            Log.d("在线状态", "开始循环,username: ${Global.username}")
                            if (Global.username.trim().isNotEmpty()) {
                                if (checkUserOnline(username = Global.username).contains("success")) {
                                    Toast.makeText(
                                        context,
                                        "心跳成功！",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "心跳失败！",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                Log.d("在线状态", "已访问")
                            }
                            delay(10000)
                        }
                    }
                }
            }


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
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AllHome(
                        colorMode = colorMode,
                        modifier = Modifier.padding()
                    )
                }
            }
        }
    }
}

fun checkUserOnline(username: String): String {
    val url = "https://syc666.gdata.fun/syc/keepAlive.php"

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

//电池优化
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
            Toast.makeText(context, "此设备不支持忽略电池优化设置", Toast.LENGTH_SHORT).show()
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
    colorMode: MutableState<Int> = remember { mutableIntStateOf(0) }
) {
    val context = LocalContext.current
    // 读取账号
    val appInternalDir = context.filesDir
    val name = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
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
    LaunchedEffect(password.value) {
        if (password.value != "" && name.value != "") {
            val result = withContext(Dispatchers.IO) {
                val url = "https://syc666.gdata.fun/syc/login.php"

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
                val jsonObject = JSONObject(result)
                if (jsonObject.getString("status") == "success") {
                    Global.username = name.value
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
    }

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
            composable("loading") { loading() }
            composable("Main") {
                Main(
                    modifier = modifier,
                    navController,
                    colorMode = colorMode,
                    hazeState,
                    hazeStyle
                )
            }
            composable("Regin") { Regin(hazeStyle, hazeState, navController) }
        }
    }
}

@Composable
fun loading() {
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
    hazeStyle: HazeStyle
) {
    val topAppBarScrollBehavior0 =
        MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val topAppBarScrollBehavior1 =
        MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val topAppBarScrollBehavior2 =
        MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())
    val topAppBarScrollBehavior3 =
        MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0,
        topAppBarScrollBehavior1,
        topAppBarScrollBehavior2,
        topAppBarScrollBehavior3
    )
    val pagerState = rememberPagerState(pageCount = { 4 }, initialPage = 0)
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
        NavigationItem("动态", ImageVector.vectorResource(id = R.drawable.zone)),
        NavigationItem("我的", ImageVector.vectorResource(id = R.drawable.my))
    )
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.debounce(150).collectLatest {
            targetPage = pagerState.currentPage
        }
    }
    Scaffold(modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                items = items,
                color = Color.Transparent,
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle
                ),
                selected = targetPage,
                onClick = { index ->
                    targetPage = index
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }, topBar = {
            TopAppBar(scrollBehavior = currentScrollBehavior,
                color = Color.Transparent,
                title = when (pagerState.currentPage) {
                    0 -> "首页"
                    1 -> "消息"
                    2 -> "动态"
                    else -> "我的"
                },
                modifier = Modifier.hazeEffect(
                    state = hazeState,
                    style = hazeStyle, block = fun HazeEffectScope.() {
                        progressive =
                            HazeProgressive.verticalGradient(startIntensity = 1f, endIntensity = 0f)
                    }),
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                })
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
                colorMode = colorMode
            )
        }
    }
}

@Composable
fun AppHorizontalPager(
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    navController: NavController,
    colorMode: MutableState<Int>
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
                    navController = navController
                )

                1 -> Chat(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                    padding = padding,
                    navController = navController
                )

                2 -> Moments(
                    topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                    padding = padding,
                    navController = navController
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

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    name = "settings"
)
