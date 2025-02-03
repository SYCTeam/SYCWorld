package com.syc.world

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mikepenz.markdown.coil2.Coil2ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.CurrentComponentsBridge.text
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
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.concurrent.TimeUnit

@Composable
fun Moments(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController
) {
    Scaffold() {
        Column(modifier = Modifier.padding(PaddingValues(top = padding.calculateTopPadding()))) {
            val tabTexts = listOf("默认", "最新", "热度")
            val selectedTab = remember { mutableIntStateOf(0) }
            TabRow(
                tabs = tabTexts,
                selectedTabIndex = selectedTab.intValue,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                selectedTab.intValue = it
            }
            LazyColumn(
                topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
            ) {
                item {
                    LaunchedEffect(Unit) {
                        withContext(Dispatchers.IO) {
                            val postlist = getPost()
                            println(postlist.first+"aaa"+postlist.second)
                        }
                    }
                    LaunchedEffect(selectedTab.intValue) {
                        withContext(Dispatchers.IO) {
                            val postlist = if (selectedTab.intValue == 0) {
                                getPost()
                            } else if (selectedTab.intValue == 0) {
                                getPost("latest")
                            } else {
                                getPost("hot")
                            }
                            println(postlist.second+"aaa")
                        }
                    }

                    MomentsItem(
                        1737276065842,
                        "小夜",
                        "印度",
                        "111",
                        0,
                        0,
                        0,
                        1640432
                    )
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

@OptIn(ExperimentalAnimationApi::class)
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
    authorQQ: Long
) {
    val timestamp = remember { System.currentTimeMillis() }
    val diffInMillis = timestamp - time
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
    Card(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 6.dp)
            .fillMaxWidth()
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
            )
            Image(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-5).dp, y = 10.dp),
                painter = painterResource(id = R.drawable.point_green),
                contentDescription = null
            )
            Column(modifier = Modifier.padding(start = 5.dp)) {
                Text(
                    text = author,
                    modifier = Modifier.offset(y = 3.dp),
                    fontSize = 15.sp,
                    style = TextStyle(fontStyle = FontStyle.Italic)
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
                            text = if (targetState) timeAgo else transToString(time),
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
                elements.replace(pattern, "").take(90)+if (elements.length > 90) "..." else "",
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
                        .fillMaxWidth()
                        .height(165.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
                        .fillMaxWidth()
                        .height(165.dp)) {
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
                    Spacer(modifier = Modifier.width(4.dp))
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .height(165.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
                        .fillMaxWidth()
                        .height(105.dp)) {
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
            val zanok = remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { zanok.value = !zanok.value }) {
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
                val zansave = remember { mutableIntStateOf(zan) }
                Text(
                    (zansave.intValue + (if (zanok.value) +1 else +0)).toString(),
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
