package com.syc.world

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperArrow
import top.yukonga.miuix.kmp.extra.SuperDialogDefaults.insideMargin
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun PersonInfo(
    navController: NavController
) {
    val hazeState = remember { HazeState() }
    val hazeStyle = HazeStyle(
        backgroundColor = MiuixTheme.colorScheme.background,
        tint = HazeTint(MiuixTheme.colorScheme.background.copy(0.75f)),
        blurRadius = 25.dp,
        noiseFactor = 0f
    )
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val qq = remember { mutableStateOf("2223289765") }
    val appInternalDir = context.filesDir
    val name = remember { mutableStateOf("") }
    val client = OkHttpClient()
    if (File(appInternalDir, "username").exists() && File(appInternalDir, "username").length() > 0) {
        BufferedReader(FileReader(File(appInternalDir, "username"))).use { reader ->
            val savedUserInput = reader.readLine()
            if (savedUserInput.isNotEmpty()) {
                name.value = savedUserInput
            }
        }
    }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (Global.url == "") {
                withContext(Dispatchers.IO) {
                    Global.url = getUrl()
                }
                if (Global.url != "" && Global.url.contains("http")) {
                    break
                }
                delay(2000)
            }
            if (Global.url != "" && Global.url.contains("http")) {
                val result = withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url("${Global.url}/syc/check.php?username=${name.value}")
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
                        qq.value = jsonObject.getLong("qq").toString()
                    }
                }
            }
        }
    }
    val loginCounts = remember { mutableIntStateOf(0) }
    val registerTime = remember { mutableLongStateOf(0L) }
    val registerAddress = remember { mutableStateOf("") }
    val location = remember { mutableStateOf("") }
    val synopsis = remember { mutableStateOf("这个人很懒，暂时没有简介~") }
    LaunchedEffect(Unit) {
        if (Global.username.trim().isNotEmpty()) {
            withContext(Dispatchers.IO) {
                val userInfoFirst = getUserInformation(name.value)
                if (isJson(userInfoFirst)) {
                    val jsonObject = JSONObject(userInfoFirst)
                    if (jsonObject.getString("status") == "success") {
                        loginCounts.intValue = jsonObject.getInt("loginCount")

                        registerTime.longValue = jsonObject.getLong("registerTime")
                        synopsis.value = jsonObject.getString("synopsis")
                        // 获取 registerIp 的地理位置
                        val result = withContext(Dispatchers.IO) {
                            try {
                                // 获取 loginIp 的地理位置
                                client.newCall(Request.Builder().url("https://api.ip77.net/ip2/v4/")
                                    .post(FormBody.Builder().add("ip", jsonObject.getString("loginIp")).build())
                                    .build()).execute().use { response ->
                                    if (response.isSuccessful) response.body?.string().orEmpty() else ""
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                ""
                            }
                        }

// 获取 registerIp 的地理位置
                        val registerLocationResult = withContext(Dispatchers.IO) {
                            try {
                                // 获取 registerIp 的地理位置
                                client.newCall(Request.Builder().url("https://api.ip77.net/ip2/v4/")
                                    .post(FormBody.Builder().add("ip", jsonObject.getString("registerIp")).build())
                                    .build()).execute().use { response ->
                                    if (response.isSuccessful) response.body?.string().orEmpty() else ""
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                ""
                            }
                        }

// 处理 loginIp 的地理位置
                        if (result.isNotEmpty()) {
                            JSONObject(result).takeIf { it.getInt("code") == 0 }?.let {
                                val data = it.getJSONObject("data")
                                location.value = buildString {
                                    append(data.getString("country"))
                                    append(" ")
                                    append(data.getString("province"))
                                    append("省 ")
                                    if (data.getString("city") != data.getString("province") || data.getString("city") != "") {
                                        append(data.getString("city"))
                                        append("市 ")
                                        if (data.getString("district") != "") {
                                            append(data.getString("district"))
                                            append("区/县 ")
                                        }
                                        if (data.getString("street") != "") append(data.getString("street"))
                                    }
                                }
                            }
                        }

// 处理 registerIp 的地理位置
                        if (registerLocationResult.isNotEmpty()) {
                            JSONObject(registerLocationResult).takeIf { it.getInt("code") == 0 }?.let {
                                val data = it.getJSONObject("data")
                                registerAddress.value = buildString {
                                    append(data.getString("country"))
                                    append(" ")
                                    append(data.getString("province"))
                                    append("省 ")
                                    if (data.getString("city") != data.getString("province") || data.getString("city") != "") {
                                        append(data.getString("city"))
                                        append("市 ")
                                        if (data.getString("district") != "") {
                                            append(data.getString("district"))
                                            append("区/县 ")
                                        }
                                        if (data.getString("street") != "") append(data.getString("street"))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    Scaffold(topBar = {
        TopAppBar(
            title = "个人信息",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = topAppBarScrollBehavior,
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState)
        ) {
            item {
                SmallTitle("编辑资料")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    BasicComponent(
                        title = "头像",
                        summary = "头像为您QQ使用的头像",
                        rightActions = {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq.value}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                            )
                        }
                    )
                    SuperArrow(
                        title = "个人简介",
                        summary = synopsis.value
                    )
                }
                SmallTitle("个人资料")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 6.dp)
                ) {
                    BasicComponent(
                        title = "IP属地",
                        summary = if (location.value == "") null else location.value
                    )
                    BasicComponent(
                        title = "注册IP属地",
                        summary = if (registerAddress.value == "") null else registerAddress.value
                    )
                    BasicComponent(
                        title = "注册时间",
                        summary = transToString((registerTime.longValue.toString()+"000").toLong())
                    )
                    BasicComponent(
                        title = "登录次数",
                        summary = loginCounts.intValue.toString()+ "次"
                    )
                }
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 65.dp
                    )
                )
            }
        }
    }
}
