package com.syc.world

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextButtonColors
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDialogDefaults
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
                    if (islogin) {
                        val result = withContext(Dispatchers.IO) {
                            val request = Request.Builder()
                                .url("https://syc666.gdata.fun/syc/check.php?username=${name.value}")
                                .build()
                            try {
                                val response = client.newCall(request).execute()
                                if (response.isSuccessful) {
                                    val responseBody = response.body?.string() ?: ""
                                    Log.d("网络问题", responseBody)
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
                                loginqq = jsonObject.getLong("qq")
                            }
                        }
                    }
                }
                val showErrorDialog = remember { mutableStateOf(false) }
                val ErrorDialog = remember { mutableStateOf("") }
                val startlogin = remember { mutableStateOf(false) }
                val startregister = remember { mutableStateOf(false) }
                LaunchedEffect(startlogin.value) {
                    if (startlogin.value) {
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

                        println(result)
                        startlogin.value = false
                        if (result != "") {
                            val jsonObject = JSONObject(result)
                            if (jsonObject.getString("status") == "success") {
                                /*println("登录成功")
                                context.dataStore.edit { preferences ->
                                    preferences[stringPreferencesKey("name")] = name.value
                                }
                                println("登录成功")
                                context.dataStore.edit { preferences ->
                                    preferences[stringPreferencesKey("password")] = password.value
                                }
                                println("登录成功")*/

                                val appInternalDir = context.filesDir


                                // 储存账号
                                val userFile = File(appInternalDir, "username")
                                if (!userFile.exists()) {
                                    userFile.createNewFile()
                                }
                                val userpassFile = File(appInternalDir, "password")
                                if (!userpassFile.exists()) {
                                    userpassFile.createNewFile()
                                }
                                BufferedReader(FileReader(userpassFile)).use { reader ->
                                    FileWriter(userpassFile).use { writer ->
                                        writer.write(password.value)
                                    }
                                }
                                BufferedReader(FileReader(userFile)).use { reader ->
                                    FileWriter(userFile).use { writer ->
                                        writer.write(name.value)
                                    }
                                }

                                Global.username = name.value
                                Global.setIsLogin(true)

                                navController.navigate("Main") {
                                    popUpTo("Regin") {
                                        inclusive = true
                                    }
                                }
                                println("登录成功")
                            } else {
                                showErrorDialog.value = true
                                ErrorDialog.value = jsonObject.getString("message")
                            }
                        }
                    }
                }
                LaunchedEffect(startregister.value) {
                    if (startregister.value) {
                        val result = withContext(Dispatchers.IO) {
                            val url = "https://syc666.gdata.fun/syc/register.php"

                            // 创建请求体
                            val formBody = FormBody.Builder()
                                .add("username", name.value)
                                .add("password", password.value)
                                .add("qq", qq.longValue.toString())
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

                        println(result)
                        startlogin.value = false
                        if (result != "") {
                            val jsonObject = JSONObject(result)
                            if (jsonObject.getString("status") == "success") {
                                val appInternalDir = context.filesDir

                                // 储存账号
                                val userFile = File(appInternalDir, "username")
                                if (!userFile.exists()) {
                                    userFile.createNewFile()
                                }
                                BufferedReader(FileReader(userFile)).use { reader ->
                                    FileWriter(userFile).use { writer ->
                                        writer.write(name.value)
                                    }
                                }
                                val userpassFile = File(appInternalDir, "password")
                                if (!userpassFile.exists()) {
                                    userpassFile.createNewFile()
                                }
                                BufferedReader(FileReader(userpassFile)).use { reader ->
                                    FileWriter(userpassFile).use { writer ->
                                        writer.write(password.value)
                                    }
                                }

                                Global.username = name.value
                                Global.setIsLogin(true)

                                navController.navigate("Main") {
                                    popUpTo("Regin") {
                                        inclusive = true
                                    }
                                }
                            } else {
                                showErrorDialog.value = true
                                ErrorDialog.value = jsonObject.getString("message")
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
