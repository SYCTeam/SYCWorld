package com.syc.world

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.mikepenz.markdown.model.DefaultMarkdownColors
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownColors
import com.mikepenz.markdown.model.MarkdownTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.min

@SuppressLint("Range")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Moments(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    postId: MutableState<Int>,
    navController: NavController,
    isReply: MutableState<Boolean>,
    selectedTab: MutableState<Int>,
    postlist: SnapshotStateList<List<Post>>,
    isTab: MutableState<Boolean>
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val nestedScrollConnection = remember { topAppBarScrollBehavior.nestedScrollConnection }
    val coroutineScope = rememberCoroutineScope()

    var currentPage by rememberSaveable { mutableStateOf(1) }
    var isLoading by remember { mutableStateOf(false) }

    // **📌 监听 Tab 变化，重新加载第一页**
    LaunchedEffect(isTab.value) {
        if (isTab.value) {
            currentPage = 1
            isLoading = true
            withContext(Dispatchers.IO) {
                postlist.clear()
                val newPosts = getPost(
                    when (selectedTab.value) {
                        0 -> "random"
                        1 -> "latest"
                        2 -> "hot"
                        else -> "random"
                    },
                    username = Global.username,
                    password = Global.password,
                    page = 1
                ).second
                withContext(Dispatchers.Main) {
                    if (newPosts.isNotEmpty()) {
                        postlist.add(newPosts)
                    }
                    isLoading = false
                    isTab.value = false
                }
            }
        }
    }

    // **📌 监听列表滚动，快滑到底部时加载更多**
    LaunchedEffect(listState) {
        if (!isTab.value) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
                .collect { lastVisibleItemIndex ->
                    val totalItems = postlist.flatten().size
                    if (!isLoading && lastVisibleItemIndex >= totalItems - 5) { // **快到底部**
                        isLoading = true
                        coroutineScope.launch(Dispatchers.IO) {
                            val newPosts = getPost(
                                when (selectedTab.value) {
                                    0 -> "random"
                                    1 -> "latest"
                                    2 -> "hot"
                                    else -> "random"
                                },
                                username = Global.username,
                                password = Global.password,
                                page = currentPage + 1
                            ).second
                            withContext(Dispatchers.Main) {
                                if (newPosts.isNotEmpty()) {
                                    postlist.add(newPosts)
                                    currentPage++
                                }
                                isLoading = false
                            }
                        }
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.nestedScroll(nestedScrollConnection)
    ) {
        item { Spacer(modifier = Modifier.height(padding.calculateTopPadding())) }

        items(postlist.flatten()) { post ->
            var visible by rememberSaveable { mutableStateOf(false) }
            val ipAddress = remember { mutableStateOf(post.ip) }

            LaunchedEffect(post.ip) {
                withContext(Dispatchers.IO) {
                    val resolvedIp = getIpaddress(context, post.ip).second
                    withContext(Dispatchers.Main) {
                        ipAddress.value = resolvedIp
                    }
                }
            }

            LaunchedEffect(post.postId) {
                if (!visible) visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = slideInVertically(
                    initialOffsetY = { it / 10 },
                    animationSpec = tween(durationMillis = 200)
                ) + fadeIn(animationSpec = tween(durationMillis = 200))
            ) {
                MomentsItem(
                    time = (post.timestamp.toString() + "000").toLong(),
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
                    online = post.online,
                    isReply = isReply
                )
            }
        }

        if (!postlist.isNotEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("加载中...")
                }
            }
        }

        item {
            Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 65.dp))
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
    textLink: TextLinkStyles = TextLinkStyles(style = link.toSpanStyle())
): MarkdownTypography = DefaultMarkdownTypography(
    h1 = h1, h2 = h2, h3 = h3, h4 = h4, h5 = h5, h6 = h6,
    text = text, quote = quote, code = code, inlineCode = inlineCode, paragraph = paragraph,
    ordered = ordered, bullet = bullet, list = list, link = link,
    textLink = textLink,
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
    online: Boolean,
    isReply: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    val timeAgo = remember {
        calculateTimeAgo(time)
    }
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp, horizontal = 10.dp)
            .fillMaxWidth()
            .clickable {
                isReply.value = false
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
            val pattern = Regex("!\\[(.*?)]\\((.*?)(?:\\s+[\"'](.*?)[\"'])?\\)")
            Markdown(elements.replace(pattern, "").take(90)+if (elements.replace(pattern, "").length > 90) "..." else "")
            Spacer(modifier = Modifier.height(10.dp))
            val pic = pattern.findAll(elements)
                .mapNotNull { it.groups[2]?.value }
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
                    .clickable {
                        morepostId.value = postId
                        navController.navigate("Dynamic")
                        isReply.value = true
                    }
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
