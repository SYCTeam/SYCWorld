package com.syc.world

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mikepenz.markdown.coil2.Coil2ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.RoundedCornerSpanPainter
import com.mikepenz.markdown.compose.extendedspans.SquigglyUnderlineSpanPainter
import com.mikepenz.markdown.compose.extendedspans.rememberSquigglyUnderlineAnimator
import com.mikepenz.markdown.model.markdownExtendedSpans
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.concurrent.TimeUnit

@Composable
fun Dynamic(navController: NavController,postId: Int,hazeState: HazeState,hazeStyle: HazeStyle) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val qq = remember { mutableStateOf("0") }
    val time = remember { mutableLongStateOf(0L) }
    val author = remember { mutableStateOf("") }
    val ipAddress = remember { mutableStateOf("") }
    val elements = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val post = getPost("latest", postId = postId.toString(), username = Global.username, password = Global.password).second
            if (post.isNotEmpty()) {
                qq.value = post[0].qq.toString()
                time.longValue = post[0].timestamp*1000
                author.value = post[0].username
                ipAddress.value = getIpaddress(post[0].ip).second
                elements.value = post[0].content
            }
        }
    }
    Scaffold(topBar = {
        SmallTopAppBar(
            title = "",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = TopAppBarState,
            navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier.size(52.dp).padding(8.dp)
                    ) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onBackground
                        )
                    }
                    AnimatedVisibility(qq.value != "0") {
                        Row {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq.value}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 15.dp,
                                            topEnd = 15.dp,
                                            bottomStart = 15.dp,
                                            bottomEnd = 15.dp
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                val timestamp = remember { System.currentTimeMillis() }
                                val diffInMillis = timestamp - time.longValue
                                val timeAgo = remember(diffInMillis) {
                                    when {
                                        diffInMillis < TimeUnit.MINUTES.toMillis(1) -> {
                                            // 小于一分钟，显示秒数
                                            "${TimeUnit.MILLISECONDS.toSeconds(diffInMillis)}秒前"
                                        }

                                        diffInMillis < TimeUnit.HOURS.toMillis(1) -> {
                                            // 小于1小时，显示分钟数
                                            "${TimeUnit.MILLISECONDS.toMinutes(diffInMillis)}分钟前"
                                        }

                                        diffInMillis < TimeUnit.DAYS.toMillis(1) -> {
                                            // 小于1天，显示小时数
                                            "${TimeUnit.MILLISECONDS.toHours(diffInMillis)}小时前"
                                        }

                                        diffInMillis < TimeUnit.DAYS.toMillis(30) -> {
                                            // 小于30天，显示天数
                                            "${TimeUnit.MILLISECONDS.toDays(diffInMillis)}天前"
                                        }

                                        diffInMillis < TimeUnit.DAYS.toMillis(365) -> {
                                            // 小于一年，显示月份数
                                            "${TimeUnit.MILLISECONDS.toDays(diffInMillis) / 30}个月前"
                                        }

                                        else -> {
                                            // 大于一年，显示年份
                                            "${TimeUnit.MILLISECONDS.toDays(diffInMillis) / 365}年前"
                                        }
                                    }
                                }
                                Text(
                                    text = author.value,
                                    modifier = Modifier.offset(y = 3.dp),
                                    fontSize = 15.sp
                                )
                                var isTimeAgo by remember { mutableStateOf(true) }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { isTimeAgo = !isTimeAgo }
                                        .padding(top = 3.dp)
                                ) {
                                    AnimatedContent(
                                        targetState = isTimeAgo,
                                        transitionSpec = {
                                            (slideInHorizontally(initialOffsetX = { -it }) + fadeIn(tween(300))) togetherWith
                                                    (slideOutHorizontally(targetOffsetX = { it }) + fadeOut(
                                                        tween(
                                                            300
                                                        )
                                                    ))
                                        }
                                    ) { targetState ->
                                        Text(
                                            text = if (targetState) timeAgo else transToString1(time.longValue),
                                            fontSize = 12.sp,
                                            color = Color.Gray,
                                            style = TextStyle(fontStyle = FontStyle.Normal)
                                        )
                                    }
                                    AnimatedVisibility(ipAddress.value != "") {
                                        Text(
                                            text = " | IP地址: ${ipAddress.value}",
                                            fontSize = 13.sp,
                                            color = Color.Gray,
                                            style = TextStyle(fontStyle = FontStyle.Normal)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
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
                Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
                    val highlightsBuilder =
                        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
                    AnimatedVisibility(elements.value != "") {
                        Markdown(
                            elements.value,
                            modifier = Modifier.fillMaxSize(),
                            colors = markdownColor(),
                            extendedSpans = markdownExtendedSpans {
                                val animator = rememberSquigglyUnderlineAnimator()
                                remember {
                                    ExtendedSpans(
                                        RoundedCornerSpanPainter(),
                                        SquigglyUnderlineSpanPainter(animator = animator)
                                    )
                                }
                            },
                            components = markdownComponents(
                                codeBlock = {
                                    MarkdownHighlightedCodeBlock(
                                        it.content,
                                        it.node,
                                        highlightsBuilder
                                    )
                                },
                                codeFence = {
                                    MarkdownHighlightedCodeFence(
                                        it.content,
                                        it.node,
                                        highlightsBuilder
                                    )
                                },
                            ),
                            imageTransformer = Coil2ImageTransformerImpl,
                            typography = markdownTypography1()
                        )
                    }
                }
                Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()))
            }
        }
    }
}
