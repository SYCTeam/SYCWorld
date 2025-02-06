package com.syc.world

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
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
import org.w3c.dom.Comment
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider

@Composable
fun Dynamic(navController: NavController,postId: Int,hazeState: HazeState,hazeStyle: HazeStyle) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val qq = remember { mutableStateOf("0") }
    val time = remember { mutableLongStateOf(0L) }
    val author = remember { mutableStateOf("") }
    val ipAddress = remember { mutableStateOf("") }
    val elements = remember { mutableStateOf("") }
    val view = remember { mutableStateOf("") }
    val message = remember { mutableStateOf(0) }
    val like = remember { mutableStateOf(0) }
    val share = remember { mutableStateOf(0) }
    val comment = remember { mutableStateListOf<Comments>() }
    val zanok = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            comment.clear()
            val post = getPost("latest", postId = postId.toString(), username = Global.username, password = Global.password).second
            if (post.isNotEmpty()) {
                qq.value = post[0].qq.toString()
                time.longValue = post[0].timestamp*1000
                author.value = post[0].username
                ipAddress.value = getIpaddress(post[0].ip).second
                elements.value = post[0].content
                view.value = post[0].views.toString()
                message.value = post[0].commentsCount
                like.value = post[0].likes
                share.value = post[0].shares
                Global.setChatSelection3(false)
                comment.addAll(post[0].comments)
                zanok.value = post[0].islike
            }
        }
    }
    Scaffold(topBar = {
        SmallTopAppBar(
            title = "",
            color = Color.Transparent,
            modifier = Modifier
                .hazeEffect(
                    state = hazeState,
                    style = hazeStyle
                )
                .background(CardDefaults.DefaultColor())
                .fillMaxWidth(),
            scrollBehavior = TopAppBarState,
            navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .padding(8.dp)
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
                                            20.dp
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
                                    modifier = Modifier.offset(y = 0.dp),
                                    fontSize = 16.sp
                                )
                                var isTimeAgo by remember { mutableStateOf(true) }
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clickable { isTimeAgo = !isTimeAgo }
                                        .padding(top = 1.5.dp)
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
                                }
                            }
                        }
                    }
                }
            }
        )
    }, bottomBar = {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    48.dp + WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
                .shadow(elevation = 4.dp, shape = RectangleShape, spotColor = Color.Gray)
                .background(CardDefaults.DefaultColor())
        ) {
            Column(modifier = Modifier
                .fillMaxHeight()
                .weight(1.2f)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )) {
                Card(modifier = Modifier
                    .padding(start = 12.dp)
                    .padding(vertical = 7.dp)
                    .fillMaxSize(), color = MiuixTheme.colorScheme.background.copy(alpha = 0.5f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(painter = painterResource(R.drawable.write), contentDescription = null, modifier = Modifier
                            .padding(start = 9.dp)
                            .size(16.dp))
                        Text(
                            text = "说说你的看法",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }

            val context = LocalContext.current
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
                            like.value = like.value+1
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
                            like.value = like.value-1
                        }
                        zanno.value = false
                        zan.value = false
                    }
                }
            }
            Row(modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )) {
                Row(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.message), contentDescription = null, modifier = Modifier
                        .size(18.dp)
                        .offset(x = 1.5.dp),colorFilter = ColorFilter.tint(Color.Gray))
                    Text(text = message.value.toString(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.offset(x = (-1).dp, y = (-7).dp))
                }
                val animatedColor = animateColorAsState(
                    targetValue = if (zanok.value) MiuixTheme.colorScheme.primaryVariant else Color.Gray,
                    animationSpec = tween(durationMillis = 300) // 动画时长为300ms
                )
                Row(modifier = Modifier
                    .weight(1f)
                    .clickable {
                        if (zanok.value) zanno.value = true
                        zanok.value = !zanok.value
                        zan.value = !zan.value
                    }
                    .fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.zan0), contentDescription = null, modifier = Modifier
                        .size(18.dp)
                        .offset(x = 1.5.dp),colorFilter = ColorFilter.tint(animatedColor.value))
                    Text(text = like.value.toString(), fontSize = 9.sp, color = animatedColor.value, modifier = Modifier.offset(x = (-1).dp, y = (-7).dp))
                }
                Row(modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Image(painter = painterResource(R.drawable.shares), contentDescription = null, modifier = Modifier
                        .size(18.dp)
                        .offset(x = 1.5.dp),colorFilter = ColorFilter.tint(Color.Gray))
                    Text(text = share.value.toString(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.offset(x = (-1).dp, y = (-7).dp))
                }
            }
        }
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = TopAppBarState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            item {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(CardDefaults.DefaultColor())) {
                    val highlightsBuilder =
                        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
                    AnimatedVisibility(elements.value != "") {
                        Column {
                            Markdown(
                                elements.value,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
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
                        }
                    }
                    Row(modifier = Modifier.padding(16.dp)) {
                        AnimatedVisibility(ipAddress.value != "") {
                            Text(
                                text = "发布于 ${ipAddress.value}",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                style = TextStyle(fontStyle = FontStyle.Normal)
                            )
                        }
                        AnimatedVisibility(view.value != "") {
                            Text(
                                text = " · ${view.value}浏览",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                style = TextStyle(fontStyle = FontStyle.Normal)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(CardDefaults.DefaultColor())) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(48.dp)) {
                        Text(text = "共 ${message.value} 回复", fontSize = 13.sp, modifier = Modifier.padding(16.dp))
                    }
                }
            }
            val parentComments = comment.filter { it.parentCommentId == 0 }

            items(parentComments) { parentComment ->
                Column(modifier = Modifier.background(CardDefaults.DefaultColor())) {
                    // 父级评论项
                    CommentItem(comment = parentComment)

                    // 子评论部分（支持多级嵌套）
                    Card(modifier = Modifier.padding(start = 54.dp, top = 4.dp, end = 20.dp, bottom = 10.dp),
                        color = MiuixTheme.colorScheme.background.copy(alpha = 0.5f),
                        cornerRadius = 8.dp) {
                        ChildComments(parentId = parentComment.id, comments = comment.toList())
                        Spacer(modifier = Modifier.height(4.dp))
                    }


                }
                //HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
            }
            item {
                Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+48.dp))
            }
        }
    }
}

// 4. 新增子评论组件
@Composable
private fun ChildComments(parentId: Int,comments: List<Comments>) {
    // 筛选出直接子评论
    val childComments = comments.filter { it.parentCommentId == parentId }

    Column(modifier = Modifier.padding(start = 0.dp)) { // 子评论缩进
        childComments.forEach { child ->
            Column {
                // 子评论项
                ChildCommentItem(comment = child,if (comments.filter { it.id == child.parentCommentId }[0].parentCommentId != 0) comments.filter { it.id == child.parentCommentId }[0].username else null)

                // 递归显示更深层评论
                ChildComments(parentId = child.id,comments = comments)
            }
        }
    }
}

@Composable
fun ChildCommentItem(comment: Comments,twochild: String? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
            Row(modifier = Modifier.weight(1f).padding(start = 0.dp)) {
                if (twochild == null) {
                    Text(text =  comment.username+"：",
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.primaryVariant)
                } else {
                    Text(text = comment.username,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.primaryVariant)
                    Text(text =  "回复",
                        fontSize = 14.sp)
                    Text(text =  twochild,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.primaryVariant)
                    Text(text =  "：",
                        fontSize = 14.sp)
                }
                // 评论内容
                Text(
                    text = comment.content,
                    modifier = Modifier.padding(),
                    fontSize = 14.sp
                )
            }
        }
    }
}

// 5. 改造后的 CommentItem
@Composable
fun CommentItem(comment: Comments) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDefaults.DefaultColor())
            .padding(vertical = 8.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(start = 16.dp)) {
            // 头像部分
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${comment.qq}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
            )

            // 在线状态
            Image(
                painter = painterResource(id = if (comment.online) R.drawable.point_green else R.drawable.point_gray),
                contentDescription = null,
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-9).dp, y = 18.dp)
            )

            // 主要内容
            Column(modifier = Modifier.weight(1f).padding(start = 0.dp)) {

                // 用户信息行
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.username,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.primaryVariant
                    )
                }

                // 评论内容
                Text(
                    text = comment.content,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 15.sp
                )

                val timeAgo = remember {
                    calculateTimeAgo(comment.timestamp * 1000L) // 注意 timestamp 是 Long 类型
                }
                var showFullTime by remember { mutableStateOf(false) }
                AnimatedContent(
                    targetState = showFullTime,
                    transitionSpec = { (slideInHorizontally(initialOffsetX = { -it }) + fadeIn(tween(300))) togetherWith (slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(300))) }
                ) { showFull ->
                    Text(
                        text = if (showFull)
                            transToString1(comment.timestamp * 1000L)
                        else
                            timeAgo,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        modifier = Modifier
                            .clickable { showFullTime = !showFull }
                            .padding(top = 4.dp)
                    )
                }
            }
        }
    }
}

fun calculateTimeAgo(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "${diff / 1000}秒前"
        diff < 3_600_000 -> "${diff / 60_000}分钟前"
        diff < 86_400_000 -> "${diff / 3_600_000}小时前"
        diff < 2_592_000_000 -> "${diff / 86_400_000}天前"
        diff < 31_536_000_000 -> "${diff / 2_592_000_000}个月前"
        else -> "${diff / 31_536_000_000}年前"
    }
}
