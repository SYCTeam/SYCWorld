package com.syc.world

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.gson.Gson
import com.syc.world.ForegroundService.MomentsMessage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notification(navController: NavController,hazeState: HazeState,hazeStyle: HazeStyle,postId: MutableState<Int>,isReply: MutableState<Boolean>) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val notList = remember { mutableStateListOf<MomentsMessage>() }

    Scaffold(topBar = {
        SmallTopAppBar(
            title = "通知",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = TopAppBarState,
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
        val isRefreshing = remember { mutableStateOf(false) }
        LaunchedEffect(isRefreshing.value) {
            notList.clear()
            val readResult = readFromFile(context, "Moments/list.json")
            val gson = Gson()
            if (readResult.isNotEmpty()) {
                notList.addAll(gson.fromJson(readResult, Array<MomentsMessage>::class.java).toList().sortedByDescending { it.time })
            }
            isRefreshing.value = false
        }
        PullToRefreshBox(
            isRefreshing = isRefreshing.value,
            onRefresh = {
                isRefreshing.value = true
            }
        ) {
            LazyColumn(
                contentPadding = PaddingValues(top = padding.calculateTopPadding()),
                topAppBarScrollBehavior = TopAppBarState,
                modifier = Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState),
            ) {
                itemsIndexed(notList) { _, content ->
                    notList(
                        qq = content.qq,
                        content = content.content.replace(Regex("!\\[Image]\\(([^)]+)"),"[图片]"),
                        timestamp = content.time,
                        name = content.name,
                        type = content.type,
                        navController = navController,
                        postId = postId,
                        listPostId = content.postId,
                        isReply = isReply
                    )
                }
            }
        }
    }
}

@Composable
fun notList(qq: Long, content: String, timestamp: Long, name: String, type: String, navController: NavController, postId: MutableState<Int>, listPostId: Int,isReply: MutableState<Boolean>) {
    if (type == "like") {
        Column(modifier = Modifier.background(CardDefaults.DefaultColor())
            .clickable {
                isReply.value = false
                postId.value = listPostId
                navController.navigate("Dynamic")
            }) {
            Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                AsyncImage(
                    model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq}&spec=640&img_type=jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(25.dp))
                )
                Spacer(modifier = Modifier.size(14.dp))
                Column {
                    Row {
                        Text(name,fontSize = 17.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(calculateTimeAgo(timestamp),fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("点赞了你的动态："+content,fontSize = 14.sp,maxLines = 1)
                }
            }
        }
    } else if (type == "comment") {
        Column(modifier = Modifier.background(CardDefaults.DefaultColor())
            .clickable {
                isReply.value = true
            postId.value = listPostId
            navController.navigate("Dynamic")
        }) {
            Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)) {
                AsyncImage(
                    model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq}&spec=640&img_type=jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(25.dp))
                )
                Spacer(modifier = Modifier.size(14.dp))
                Column {
                    Row {
                        Text(name,fontSize = 17.sp)
                        Spacer(modifier = Modifier.weight(1f))
                        Text(calculateTimeAgo(timestamp),fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("回复了你的动态："+content,fontSize = 14.sp,maxLines = 1)
                }
            }
        }
    }
}
