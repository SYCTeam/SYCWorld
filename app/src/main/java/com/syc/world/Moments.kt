package com.syc.world

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil3.compose.AsyncImage
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.RoundedCornerSpanPainter
import com.mikepenz.markdown.compose.extendedspans.SquigglyUnderlineSpanPainter
import com.mikepenz.markdown.compose.extendedspans.rememberSquigglyUnderlineAnimator
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.markdownExtendedSpans
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.concurrent.TimeUnit

@Composable
fun Moments(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    postId: MutableState<Int>,
    navController: NavController
) {
    Scaffold() {
        Column(modifier = Modifier.padding(PaddingValues(top = padding.calculateTopPadding()))) {
            val tabTexts = listOf("默认", "最新", "热度")
            val selectedTab = rememberSaveable { mutableIntStateOf(0) }
            TabRow(
                tabs = tabTexts,
                selectedTabIndex = selectedTab.intValue,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                selectedTab.intValue = it
            }
            val postlist = remember { mutableStateListOf(emptyList<Post>()) }
            LaunchedEffect(selectedTab.intValue) {
                withContext(Dispatchers.IO) {
                    postlist.clear()
                    val post = getPost(when (selectedTab.intValue) {
                        0 -> "random"
                        1 -> "latest"
                        2 -> "hot"
                        else -> "random"
                    }, username = Global.username, password = Global.password).second
                    postlist.add(post)
                }
            }
            if (postlist.size == 0) {
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
                            text = "加载中...",
                        )
                    }
                }
            } else {
                val context = LocalContext.current
                LazyColumn(
                    topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
                ) {
                    items(postlist.size) {
                        for (post in postlist[it]) {
                            val ipAddress = remember { mutableStateOf(post.ip) }

                            LaunchedEffect(post.ip) {
                                withContext(Dispatchers.IO) {
                                    ipAddress.value = getIpaddress(context,post.ip).second
                                }
                            }
                            MomentsItem(
                                time = (post.timestamp.toString()+"000").toLong(),
                                author = post.username,
                                ipAddress = ipAddress.value,
                                elements = post.content,
                                zan = post.likes,
                                message = post.commentsCount,
                                share = post.shares,
                                authorQQ = post.qq,
                                postId = post.postId,
                                morepostId = postId,
                                navController = navController,
                                islike = post.islike,
                                online = post.online
                            )
                        }
                    }
                    item {
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
    }
}

@Composable
fun markdownTypography1(
    h1: TextStyle = MaterialTheme.typography.displayLarge.copy(
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    h2: TextStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 23.sp,
        lineHeight = 28.sp
    ),
    h3: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 20.sp,
        lineHeight = 24.sp
    ),
    h4: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    h5: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    h6: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    text: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    code: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    inlineCode: TextStyle = text,
    quote: TextStyle = MaterialTheme.typography.bodyMedium.plus(
        SpanStyle(fontStyle = FontStyle.Italic)
    ).copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    paragraph: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    ordered: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    bullet: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    list: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    link: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp,
        fontWeight = FontWeight.Bold,
        textDecoration = TextDecoration.Underline
    ),
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1, h2 = h2, h3 = h3, h4 = h4, h5 = h5, h6 = h6,
    text = text, quote = quote, code = code, inlineCode = inlineCode, paragraph = paragraph,
    ordered = ordered, bullet = bullet, list = list, link = link,
)

@Composable
fun markdownColor(
    text: Color = MiuixTheme.colorScheme.onBackground,
    codeText: Color = MiuixTheme.colorScheme.onBackground,
    inlineCodeText: Color = codeText,
    linkText: Color = text,
    codeBackground: Color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.1f),
    inlineCodeBackground: Color = codeBackground,
    dividerColor: Color = MaterialTheme.colorScheme.outlineVariant,
    tableText: Color = text,
    tableBackground: Color = MiuixTheme.colorScheme.onBackground.copy(alpha = 0.02f),
): MarkdownColors = DefaultMarkdownColors(
    text = text,
    codeText = codeText,
    inlineCodeText = inlineCodeText,
    linkText = linkText,
    codeBackground = codeBackground,
    inlineCodeBackground = inlineCodeBackground,
    dividerColor = dividerColor,
    tableText = tableText,
    tableBackground = tableBackground,
)

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun MomentsItem(
    time: Long,
    author: String,
    ipAddress: String,
    elements: String,
    zan: Int,
    message: Int,
    share: Int,
    authorQQ: Long,
    postId: Int,
    morepostId: MutableState<Int>,
    navController: NavController,
    islike: Boolean,
    online: Boolean
) {
    val timeAgo = remember {
        calculateTimeAgo(time)
    }
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .fillMaxWidth()
            .clickable {
                morepostId.value = postId
                navController.navigate("Dynamic")
            }
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 0.dp)
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${authorQQ}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 5.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 15.dp,
                            topEnd = 15.dp,
                            bottomStart = 15.dp,
                            bottomEnd = 15.dp
                        )
                    )
            )
            Image(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-5).dp, y = 10.dp),
                painter = painterResource(id = if (online) R.drawable.point_green else R.drawable.point_gray),
                contentDescription = null
            )
            Column(modifier = Modifier.padding(start = 5.dp)) {
                Text(
                    text = author,
                    modifier = Modifier.offset(y = 3.dp),
                    fontSize = 15.sp
                )
                var isTimeAgo by remember { mutableStateOf(true) }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        //.clickable { isTimeAgo = !isTimeAgo }
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
                            text = if (targetState) timeAgo else transToString1(time),
                            fontSize = 12.sp,
                            color = Color.Gray,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                    Text(
                        text = " | IP地址: $ipAddress",
                        fontSize = 13.sp,
                        color = Color.Gray,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
            }
        }
        Column(modifier = Modifier
            .padding(horizontal = 12.dp)
            .offset(y = (0).dp)) {
            /*Text(
                text = buildAnnotatedString {
                    // 添加基础文本
                    append(elements.take(90))
                    if (elements.length > 90) {
                        withStyle(
                            style = SpanStyle(
                                color = MiuixTheme.colorScheme.primaryVariant,
                            )
                        ) {
                            append("...查看更多")
                        }
                    }
                },
                fontSize = 16.sp,
            )*/
            val highlightsBuilder =
                Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
            val pattern = Regex("!\\[Image]\\(([^)]+)\\)")
            Markdown(
                elements.replace(pattern, "").take(90)+if (elements.replace(pattern, "").length > 90) "..." else "",
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
                imageTransformer = Coil3ImageTransformerImpl,
                typography = markdownTypography1()
            )
            Spacer(modifier = Modifier.height(10.dp))
            val pic = pattern.findAll(elements)
                .mapNotNull { it.groups[1]?.value }
                .toList()
            if (pic.size != 0) {
                if (pic.size == 1) {
                    AsyncImage(
                        model = pic[0],
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize(0.4f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 5.dp,
                                    topEnd = 5.dp,
                                    bottomStart = 5.dp,
                                    bottomEnd = 5.dp
                                )
                            )
                    )
                }
                if (pic.size == 2) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[0],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // 第二张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomEnd = 5.dp, topEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[1],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                }
                if (pic.size == 3) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topStart = 5.dp, bottomStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[0],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[1],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topEnd = 5.dp, bottomEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[2],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                }
                if (pic.size == 4 || pic.size == 5) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[0],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // 第二张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[1],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                            if (pic.size == 5) {
                                Column(modifier = Modifier
                                    .background(Color.Gray.copy(alpha = 0.5f))
                                    .align(Alignment.TopEnd)) {
                                    Text("${pic.size}图", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[2],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        // 第二张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[3],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                }
                if (pic.size == 6 || pic.size == 7 || pic.size == 8) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[0],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[1],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[2],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                            if (pic.size != 6) {
                                Column(modifier = Modifier
                                    .background(Color.Gray.copy(alpha = 0.5f))
                                    .align(Alignment.TopEnd)) {
                                    Text("${pic.size}图", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[3],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[4],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[5],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                }
                if (pic.size >= 9) {
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[0],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[1],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(topEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[2],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                            if (pic.size != 9) {
                                Column(modifier = Modifier
                                    .background(Color.Gray.copy(alpha = 0.5f))
                                    .align(Alignment.TopEnd)) {
                                    Text("${pic.size}图", fontSize = 10.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[3],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[4],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[5],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()) {
                        // 第一张图片
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomStart = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[6],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[7],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f)  // 平均分配空间
                                .fillMaxHeight()  // 高度填满父布局
                                .clip(RoundedCornerShape(bottomEnd = 5.dp))  // 圆角
                                .aspectRatio(1f)  // 保证宽高比为 1:1，即正方形
                        ) {
                            AsyncImage(
                                model = pic[8],
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize(),
                                contentScale = ContentScale.Crop  // 裁剪并填充正方形区域
                            )
                        }
                    }
                }
            }
        }
        Row(modifier = Modifier.height(52.dp)) {
            val zanok = remember { mutableStateOf(islike) }
            val context = LocalContext.current
            val zansave = remember { mutableStateOf(zan.toString()) }
            val zan = remember { mutableStateOf(false) }
            LaunchedEffect(zanok.value) {
                if (zanok.value && zan.value) {
                    withContext(Dispatchers.IO) {
                        val post = addlike(
                            username = Global.username,
                            password = Global.password,
                            postId = postId.toString(),
                        )
                        if (post.first != "success") {
                            Log.d("点赞问题", post.second)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "点赞失败，原因：${post.second}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            zansave.value = (zansave.value.toInt()+1).toString()
                        }
                        zan.value = false
                    }
                }
            }
            val zanno = remember { mutableStateOf(false) }
            LaunchedEffect(zanno.value) {
                if (zanno.value && zan.value) {
                    withContext(Dispatchers.IO) {
                        val post = cancellike(
                            username = Global.username,
                            password = Global.password,
                            postId = postId.toString(),
                        )
                        if (post.first != "success") {
                            Log.d("点赞问题", post.second)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "取消点赞失败，原因：${post.second}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            zanok.value = true
                        } else {
                            zansave.value = (zansave.value.toInt()-1).toString()
                        }
                        zanno.value = false
                        zan.value = false
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable {
                        if (zanok.value) zanno.value = true
                        zanok.value = !zanok.value
                    zan.value = !zan.value}) {
                val animatedColor = animateColorAsState(
                    targetValue = if (zanok.value) MiuixTheme.colorScheme.primaryVariant else Color.Gray,
                    animationSpec = tween(durationMillis = 300) // 动画时长为300ms
                )

                Image(
                    painter = painterResource(id = R.drawable.zan0),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(animatedColor.value)
                )
                Text(
                    zansave.value,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .offset(y = 1.dp),
                    color = animatedColor.value
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Image(
                    painterResource(R.drawable.message),
                    null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
                Text(
                    message.toString(),
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .offset(y = 1.dp),
                    color = Color.Gray
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Image(
                    painterResource(R.drawable.shares),
                    null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(Color.Gray)
                )
                val sharesave = remember { mutableIntStateOf(share) }
                Text(
                    sharesave.intValue.toString(),
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .offset(y = 1.dp),
                    color = Color.Gray
                )
            }
        }
    }
}
