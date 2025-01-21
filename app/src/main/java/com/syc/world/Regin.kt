package com.syc.world

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.system.exitProcess

@Composable
fun Regin(hazeStyle: HazeStyle, hazeState: HazeState, navController: NavController) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    var islogin by remember { mutableStateOf(true) }
    var loginqq by remember { mutableLongStateOf(0L) }
    val client = OkHttpClient()
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = "注册/登录",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = TopAppBarState,
            navigationIcon = {
                IconButton(
                    onClick = {
                        exitProcess(0)
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }, actions = {
                TextButton(
                    onClick = {
                        islogin = !islogin
                    },
                    modifier = Modifier.padding(start = 18.dp),
                    text = if (islogin) "注册" else "登录"
                )
            }
        )
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = TopAppBarState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            item {
                val name = remember { mutableStateOf("") }
                val qq = remember { mutableLongStateOf(10001L) }
                val qqcache = remember { mutableStateOf("") }
                val password = remember { mutableStateOf("") }
                val compositionResult =
                    rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))
                val progress =
                    animateLottieCompositionAsState(
                        composition = compositionResult.value
                    )
                LaunchedEffect(name.value) {
                    while (Global.url.isEmpty()) {
                        withContext(Dispatchers.IO) { Global.url = getUrl() }
                        if (Global.url.contains("http")) break
                        delay(2000)
                    }

                    if (islogin && Global.url.contains("http")) {
                        val result = withContext(Dispatchers.IO) {
                            try {
                                val response = client.newCall(
                                    Request.Builder()
                                        .url("${Global.url}/syc/check.php?username=${name.value}")
                                        .build()
                                ).execute()
                                response.takeIf { it.isSuccessful }?.body?.string().orEmpty()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ""
                            }
                        }

                        result.takeIf { it.isNotEmpty() }?.let {
                            val jsonObject = JSONObject(it)
                            if (jsonObject.optString("status") == "success") {
                                loginqq = jsonObject.optLong("qq")
                            }
                        }
                    }
                }
                val showErrorDialog = remember { mutableStateOf(false) }
                val ErrorDialog = remember { mutableStateOf("") }
                val startlogin = remember { mutableStateOf(false) }
                val startregister = remember { mutableStateOf(false) }
                LaunchedEffect(startlogin.value) {
                    while (Global.url.isEmpty()) {
                        withContext(Dispatchers.IO) { Global.url = getUrl() }
                        if (Global.url.contains("http")) break
                        delay(2000)
                    }

                    if (startlogin.value && Global.url.contains("http")) {
                        val result = withContext(Dispatchers.IO) {
                            val url = "${Global.url}/syc/login.php"

                            // 创建 POST 请求
                            val request = Request.Builder()
                                .url(url)
                                .post(
                                    FormBody.Builder()
                                        .add("username", name.value)
                                        .add("password", password.value)
                                        .build()
                                ).build()

                            try {
                                client.newCall(request).execute().use { response ->
                                    if (response.isSuccessful) response.body?.string().orEmpty() else ""
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ""
                            }
                        }
                        startlogin.value = false
                        if (result.isNotEmpty()) {
                            JSONObject(result).takeIf { it.getString("status") == "success" }?.let { jsonObject ->
                                // 储存账号信息
                                val appInternalDir = context.filesDir
                                val userFile = File(appInternalDir, "username")
                                val userpassFile = File(appInternalDir, "password")

                                listOf(userFile to name.value, userpassFile to password.value).forEach { (file, value) ->
                                    file.writeText(value)
                                }

                                Global.username = name.value
                                Global.setIsLogin(true)

                                navController.navigate("Main") {
                                    popUpTo("Regin") { inclusive = true }
                                }
                            } ?: run {
                                showErrorDialog.value = true
                                ErrorDialog.value = JSONObject(result).optString("message", "登录失败")
                            }
                        }
                    }
                }

                LaunchedEffect(startregister.value) {
                    while (Global.url.isEmpty()) {
                        withContext(Dispatchers.IO) { Global.url = getUrl() }
                        if (Global.url.contains("http")) break
                        delay(2000)
                    }

                    if (startregister.value && Global.url.contains("http")) {
                        val result = withContext(Dispatchers.IO) {
                            val url = "${Global.url}/syc/register.php"
                            val formBody = FormBody.Builder()
                                .add("username", name.value)
                                .add("password", password.value)
                                .add("qq", qq.longValue.toString())
                                .build()

                            val request = Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build()

                            try {
                                client.newCall(request).execute().use { response ->
                                    if (response.isSuccessful) response.body?.string().orEmpty() else ""
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                ""
                            }
                        }
                        startregister.value = false

                        if (result.isNotEmpty()) {
                            JSONObject(result).takeIf { it.getString("status") == "success" }?.let {
                                val appInternalDir = context.filesDir

                                // 储存账号信息
                                listOf("username" to name.value, "password" to password.value).forEach { (fileName, value) ->
                                    val file = File(appInternalDir, fileName)
                                    file.writeText(value)
                                }
                                if (Global.url.contains("http")) {
                                    val result1 = withContext(Dispatchers.IO) {
                                        val url = "${Global.url}/syc/login.php"

                                        // 创建 POST 请求
                                        val request = Request.Builder()
                                            .url(url)
                                            .post(
                                                FormBody.Builder()
                                                    .add("username", name.value)
                                                    .add("password", password.value)
                                                    .build()
                                            ).build()

                                        try {
                                            client.newCall(request).execute().use { response ->
                                                if (response.isSuccessful) response.body?.string().orEmpty() else ""
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            ""
                                        }
                                    }
                                    if (result1.isNotEmpty()) {
                                        JSONObject(result1).takeIf { it.getString("status") == "success" }?.let { jsonObject ->
                                            navController.navigate("Main") {
                                                popUpTo("Regin") { inclusive = true }
                                            }
                                        } ?: run {
                                            showErrorDialog.value = true
                                            ErrorDialog.value = JSONObject(result1).optString("message", "登录失败")
                                        }
                                    }
                                }

                                Global.username = name.value
                                Global.setIsLogin(true)

                            } ?: run {
                                showErrorDialog.value = true
                                ErrorDialog.value = JSONObject(result).optString("message", "注册失败")
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp), // 填充边距
                    verticalArrangement = Arrangement.Center, // 垂直居中
                    horizontalAlignment = Alignment.CenterHorizontally // 水平居中
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                            .height(250.dp)
                    ) {
                        if (!islogin && qq.longValue != 10001L && qq.longValue > 10001L) {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq.longValue}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        } else if (islogin && loginqq != 0L) {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${loginqq}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        } else {
                            LottieAnimation(
                                composition = compositionResult.value,
                                progress = { progress.progress },
                                modifier = Modifier
                                    .fillMaxSize()
                            )
                        }
                    }
                    // 用户名输入框
                    TextField(
                        value = name.value,
                        label = "用户名",
                        onValueChange = { name.value = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()  // 宽度填充父布局
                            .padding(bottom = 10.dp) // 每个输入框之间的间距
                    )

                    // 动态显示的QQ输入框
                    AnimatedVisibility(visible = !islogin) {
                        TextField(
                            value = if (qqcache.value == "10001") "" else qqcache.value,
                            label = "QQ",
                            onValueChange = {
                                if (it.toLongOrNull() != null) {
                                    qq.longValue = it.toLong()
                                    qqcache.value = it
                                } else {
                                    qqcache.value = it
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()  // 宽度填充父布局
                                .padding(bottom = 10.dp) // 每个输入框之间的间距
                        )
                    }

                    // 密码输入框
                    TextField(
                        value = password.value,
                        label = "密码",
                        onValueChange = { password.value = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    )

                    TextButton(
                        text = if (islogin) "登录" else "注册",
                        onClick = {
                            if (islogin) {
                                if (name.value.trim().isNotEmpty() && password.value.trim()
                                        .isNotEmpty()
                                ) {
                                    startlogin.value = true
                                } else {
                                    showErrorDialog.value = true
                                    ErrorDialog.value = "用户名或密码不能为空！"
                                }
                            } else {
                                if (name.value.trim().isNotEmpty() && password.value.trim()
                                        .isNotEmpty() && qq.longValue > 10001L
                                ) {
                                    startregister.value = true
                                } else {
                                    showErrorDialog.value = true
                                    ErrorDialog.value = "用户名或密码不能为空，或QQ格式不正确！"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                    SuperDialog(
                        show = showErrorDialog,
                        title = if (islogin) "登录失败" else "注册失败",
                        summary = ErrorDialog.value,
                        summaryColor = Color.Red,
                        onDismissRequest = { dismissDialog(showErrorDialog) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TextButton(
                                text = "我知道了",
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColorsPrimary(),
                                onClick = {
                                    dismissDialog(showErrorDialog)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
