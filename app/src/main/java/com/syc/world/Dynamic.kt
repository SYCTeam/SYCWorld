package com.syc.world

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
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
import com.mikepenz.markdown.model.DefaultMarkdownTypography
import com.mikepenz.markdown.model.MarkdownTypography
import com.mikepenz.markdown.model.markdownExtendedSpans
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.SuperDialogDefaults
import top.yukonga.miuix.kmp.extra.dialogStates
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.showDialog
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Dynamic(navController: NavController,postId: Int,hazeState: HazeState,hazeStyle: HazeStyle,isReply: MutableState<Boolean>) {
    val listState = rememberLazyListState()
    val topAppBarState = rememberTopAppBarState()
    val TopAppBarState = MiuixScrollBehavior(topAppBarState)
    val nestedScrollConnection = remember { TopAppBarState.nestedScrollConnection }
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
    val context = LocalContext.current
    val online = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            comment.clear()
            val post = getPost("latest", postId = postId.toString(), username = Global.username, password = Global.password).second
            if (post.isNotEmpty()) {
                qq.value = post[0].qq.toString()
                time.longValue = post[0].timestamp*1000
                author.value = post[0].username
                ipAddress.value = getIpaddress(context,post[0].ip).second
                elements.value = post[0].content
                view.value = post[0].views.toString()
                message.value = post[0].commentsCount
                like.value = post[0].likes
                share.value = post[0].shares
                Global.setChatSelection3(false)
                comment.addAll(post[0].comments)
                zanok.value = post[0].islike
                online.value = post[0].online
                delay(0)
                withContext(Dispatchers.Main) {
                    if (isReply.value) listState.animateScrollToItem(1)
                }
            }
        }
    }
    val showreply = remember { mutableStateOf(false) }
    val replyid = remember { mutableStateOf(0) }
    val replyname = remember { mutableStateOf("") }

    val showMainComment = remember { mutableStateOf(false) }
    val mainCommentId = remember { mutableStateOf(0) }

    Scaffold(topBar = {
        Column {
            SmallTopAppBar(
                title = "",
                color = CardDefaults.DefaultColor(),
                modifier = Modifier
                    .hazeEffect(
                        state = hazeState,
                        style = hazeStyle
                    )
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
                                Image(
                                    painter = painterResource(id = if (online.value) R.drawable.point_green else R.drawable.point_gray),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(10.dp)
                                        .offset(x = (-9).dp, y = 22.dp)
                                )
                                Spacer(modifier = Modifier.width(0.dp))
                                Column {
                                    val timeAgo = remember {
                                        calculateTimeAgo(time.longValue)
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
                                                        (slideOutHorizontally(targetOffsetX = { it }) + fadeOut(tween(300)))
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
            HorizontalDivider(
                modifier = Modifier.height(0.1.dp),
                color = MiuixTheme.colorScheme.background
            )
        }
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
                    .clickable {
                        showreply.value = true
                        replyid.value = 0
                    }
                    .fillMaxSize(), color = MiuixTheme.colorScheme.background.copy(alpha = 1f)) {
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
            val coroutineScope = rememberCoroutineScope()
            Row(modifier = Modifier
                .fillMaxHeight()
                .weight(0.8f)
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )) {
                Row(modifier = Modifier
                    .weight(1f)
                    .clickable {
                        coroutineScope.launch {
                            listState.animateScrollToItem(1)
                        }
                    }
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
        if (qq.value != "0") {
            Column {
                Spacer(modifier = Modifier.height(padding.calculateTopPadding()))
                LazyColumn(
                    state = listState,
                    //contentPadding = PaddingValues(top = padding.calculateTopPadding()),
                    //topAppBarScrollBehavior = TopAppBarState,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrollConnection)
                        .hazeSource(state = hazeState),
                ) {
                    item {
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .background(CardDefaults.DefaultColor())) {
                            if (elements.value != "") {
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)) {
                                    Markdown(elements.value)
                                }
                            }
                            Row(modifier = Modifier.padding(16.dp)) {
                                if (ipAddress.value != "") {
                                    Text(
                                        text = "发布于 ${ipAddress.value}",
                                        fontSize = 13.sp,
                                        color = Color.Gray,
                                        style = TextStyle(fontStyle = FontStyle.Normal)
                                    )
                                }
                                if (view.value != "") {
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
                    }
                    stickyHeader {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CardDefaults.DefaultColor())
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "共 ${message.value} 回复",
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                    val parentComments = comment.filter { it.parentCommentId == 0 }.sortedByDescending { it.timestamp }

                    items(parentComments) { parentComment ->
                        Column(modifier = Modifier.background(CardDefaults.DefaultColor())) {
                            // 父级评论项
                            CommentItem(comment = parentComment, show = showreply, replyid = replyid, replyname = replyname)

                            if (comment.toList().filter { it.parentCommentId == parentComment.id }.size != 0) {
                                // 子评论部分（支持多级嵌套）
                                Card(modifier = Modifier.padding(start = 54.dp, top = 0.dp, end = 20.dp, bottom = 10.dp),
                                    color = MiuixTheme.colorScheme.background.copy(alpha = 1f),
                                    cornerRadius = 8.dp) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    ChildComments(parentId = parentComment.id, comments = comment.toList(), show = showreply, replyid = replyid, replyname = replyname)
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                mainCommentId.value = parentComment.id
                                                showMainComment.value = true
                                            }
                                    ) {
                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp)) {
                                            Row(modifier = Modifier.weight(1f)) {
                                                Text(text = "查看全部回复(postId:${parentComment.id})",
                                                    fontSize = 14.sp,
                                                    color = MiuixTheme.colorScheme.primaryVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //HorizontalDivider(thickness = 0.5.dp, color = Color.LightGray)
                    }
                    item {
                        AnimatedVisibility(comment.size == 0) {
                            Column(modifier = Modifier
                                .fillMaxSize()
                                .height(200.dp)
                                .background(CardDefaults.DefaultColor()), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("还没有回复哦，快来回复一下吧~", fontSize = 14.sp)
                            }
                        }
                    }
                    item {
                        Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+48.dp))
                    }
                }
            }
        } else {
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
        }
        if (showMainComment.value) {
            ModalBottomSheet(
                onDismissRequest = {
                    showMainComment.value = false
                    mainCommentId.value = 0},
                containerColor = MiuixTheme.colorScheme.background,
                contentColor = MiuixTheme.colorScheme.background,
                modifier = Modifier.zIndex(1.1f)
            ) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val parentComments = comment.filter { it.id == mainCommentId.value }.sortedByDescending { it.timestamp }

                    items(parentComments) { parentComment ->
                        Column(modifier = Modifier) {
                            // 父级评论项
                            CommentItem(comment = parentComment, show = showreply, replyid = replyid, replyname = replyname)

                            if (comment.toList().filter { it.parentCommentId == parentComment.id }.size != 0) {
                                // 子评论部分（支持多级嵌套）
                                Column() {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    MainChildComments(parentId = parentComment.id, comments = comment.toList(), show = showreply, replyid = replyid, replyname = replyname)
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        ReplyDialog(
            show = showreply,
            replyid = replyid.value,
            postId = postId,
            comment = comment,
            message = message,
            replyname = replyname.value,
            modifier = Modifier.zIndex(1f)
        )
    }
}

// 4. 新增子评论组件
@Composable
private fun ChildComments(parentId: Int,comments: List<Comments>, show: MutableState<Boolean>, replyid: MutableState<Int>,replyname: MutableState<String>) {
    // 筛选出直接子评论
    val childComments = comments.filter { it.parentCommentId == parentId }

    Column(modifier = Modifier.padding(start = 0.dp)) { // 子评论缩进
        childComments.forEach { child ->
            Column {
                // 子评论项
                ChildCommentItem(comment = child,
                    if (comments.filter { it.id == child.parentCommentId }[0].parentCommentId != 0) comments.filter { it.id == child.parentCommentId }[0].username else null,
                    show, replyid,replyname)

                // 递归显示更深层评论
                ChildComments(parentId = child.id,comments = comments,show, replyid,replyname)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChildCommentItem(comment: Comments, twochild: String? = null, show: MutableState<Boolean>, replyid: MutableState<Int>,replyname: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                show.value = true
                replyid.value = comment.id
                replyname.value = comment.username
            }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 0.dp, bottom = 4.dp)) {
            FlowRow(modifier = Modifier.weight(1f)) {
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
                /*Text(
                    text = comment.content,
                    modifier = Modifier.padding(),
                    fontSize = 14.sp
                )*/
                Column(modifier = Modifier.padding(top = 0.dp, end = 8.dp).offset(y = (-2).dp)) {
                    Markdown2(comment.content)
                }
            }
        }
    }
}

@Composable
private fun MainChildComments(parentId: Int,comments: List<Comments>, show: MutableState<Boolean>, replyid: MutableState<Int>,replyname: MutableState<String>) {
    // 筛选出直接子评论
    val childComments = comments.filter { it.parentCommentId == parentId }

    Column(modifier = Modifier.padding(start = 0.dp)) { // 子评论缩进
        childComments.forEach { child ->
            Column {
                // 子评论项
                MainChildCommentItem(comment = child,
                    if (comments.filter { it.id == child.parentCommentId }[0].parentCommentId != 0) comments.filter { it.id == child.parentCommentId }[0].username else null,
                    if (comments.filter { it.id == child.parentCommentId }[0].parentCommentId != 0) comments.filter { it.id == child.parentCommentId }[0].content else null,
                    show, replyid,replyname)

                // 递归显示更深层评论
                MainChildComments(parentId = child.id,comments = comments,show, replyid,replyname)
            }
        }
    }
}

@Composable
fun MainChildCommentItem(comment: Comments, twochildname: String? = null,twochildcontent: String? = null, show: MutableState<Boolean>, replyid: MutableState<Int>,replyname: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                show.value = true
                replyid.value = comment.id
                replyname.value = comment.username
            }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .padding(vertical = 8.dp)) {
            // 头像部分
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${comment.qq}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(20.dp))
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
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 0.dp)) {

                // 用户信息行
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (twochildname == null) {
                        Text(text =  comment.username,
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.primaryVariant)
                    } else {
                        Text(text = comment.username,
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.primaryVariant)
                        Text(text =  "回复",
                            fontSize = 14.sp)
                        Text(text =  twochildname,
                            fontSize = 14.sp,
                            color = MiuixTheme.colorScheme.primaryVariant)
                    }
                }
                if (twochildcontent != null && twochildname != null) {
                    Card(Modifier.padding(top = 4.dp, end = 14.dp).fillMaxWidth(), color = CardDefaults.DefaultColor().copy(alpha = 0.5f), cornerRadius = 8.dp) {
                        Text(
                            text = twochildname,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                            fontSize = 14.sp
                        )
                        Text(
                            text = twochildcontent,
                            modifier = Modifier.padding(start = 8.dp, top = 2.dp, bottom = 4.dp),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                // 评论内容
                /*Text(
                    text = comment.content,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 15.sp
                )*/
                Column(modifier = Modifier.padding(top = 0.dp, end = 8.dp)) {
                    Markdown1(comment.content)
                }

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

// 5. 改造后的 CommentItem
@Composable
fun CommentItem(comment: Comments, show: MutableState<Boolean>, replyid: MutableState<Int>,replyname: MutableState<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDefaults.DefaultColor())
            .clickable {
                show.value = true
                replyid.value = comment.id
                replyname.value = comment.username
            }
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .padding(vertical = 8.dp)) {
            // 头像部分
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${comment.qq}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(20.dp))
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
            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 0.dp)) {

                // 用户信息行
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = comment.username,
                        fontSize = 14.sp,
                        color = MiuixTheme.colorScheme.primaryVariant
                    )
                }

                // 评论内容
                /*Text(
                    text = comment.content,
                    modifier = Modifier.padding(top = 4.dp),
                    fontSize = 15.sp
                )*/
                Column(modifier = Modifier.padding(top = 0.dp, end = 8.dp)) {
                    Markdown1(comment.content)
                }

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

@Composable
fun Markdown1(content: String,modifier: Modifier = Modifier) {
    val highlightsBuilder =
        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
    Markdown(
        content.replace(Regex("(?<!\\n\\n)(!\\[Image]\\(.*?\\))"),"\n\n$1"),
        colors = markdownColor(),
        modifier = Modifier,
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
        typography = markdownTypography2()
    )
}

@Composable
fun markdownTypography2(
    h1: TextStyle = MaterialTheme.typography.displayLarge.copy(
        fontSize = 24.sp,
        lineHeight = 30.sp
    ),
    h2: TextStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 21.sp,
        lineHeight = 26.sp
    ),
    h3: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 18.sp,
        lineHeight = 22.sp
    ),
    h4: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 16.sp,
        lineHeight = 20.sp
    ),
    h5: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    h6: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = 13.sp,
        lineHeight = 17.sp
    ),
    text: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    code: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 13.sp,
        lineHeight = 17.sp
    ),
    inlineCode: TextStyle = text,
    quote: TextStyle = MaterialTheme.typography.bodyMedium.plus(
        SpanStyle(fontStyle = FontStyle.Italic)
    ).copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    paragraph: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    ordered: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    bullet: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    list: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    link: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp,
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
fun Markdown2(content: String,modifier: Modifier= Modifier) {
    val highlightsBuilder =
        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
    Markdown(
        content.replace(Regex("(?<!\\n\\n)(!\\[Image]\\(.*?\\))"),"\n\n$1"),
        colors = markdownColor(),
        modifier = Modifier,
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
        typography = markdownTypography3()
    )
}

@Composable
fun markdownTypography3(
    h1: TextStyle = MaterialTheme.typography.displayLarge.copy(
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    h2: TextStyle = MaterialTheme.typography.displayMedium.copy(
        fontSize = 19.sp,
        lineHeight = 24.sp
    ),
    h3: TextStyle = MaterialTheme.typography.displaySmall.copy(
        fontSize = 17.sp,
        lineHeight = 21.sp
    ),
    h4: TextStyle = MaterialTheme.typography.headlineMedium.copy(
        fontSize = 15.sp,
        lineHeight = 19.sp
    ),
    h5: TextStyle = MaterialTheme.typography.headlineSmall.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    h6: TextStyle = MaterialTheme.typography.titleLarge.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    text: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    code: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    inlineCode: TextStyle = text,
    quote: TextStyle = MaterialTheme.typography.bodyMedium.plus(
        SpanStyle(fontStyle = FontStyle.Italic)
    ).copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    paragraph: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    ordered: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    bullet: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    list: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp
    ),
    link: TextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 14.sp,
        lineHeight = 18.sp,
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

@Composable
fun ReplyDialog(
    show: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    onDismissRequest: (() -> Unit)? = { dismissDialog(show)  },
    replyid: Int,
    postId: Int,
    comment: SnapshotStateList<Comments>,
    message: MutableState<Int>,
    replyname: String? = null
) {
    if (!show.value) {
        dialogStates.remove(show)
        onDismissRequest?.invoke()
    } else {
        if (!dialogStates.contains(show)) dialogStates.add(show)
        LaunchedEffect(show.value) {
            if (show.value) {
                dialogStates.forEach { state -> if (state != show) state.value = false }
            }
        }

        val density = LocalDensity.current
        val getWindowSize by rememberUpdatedState(getWindowSize())
        val windowWidth by rememberUpdatedState(getWindowSize.width.dp / density.density)
        val windowHeight by rememberUpdatedState(getWindowSize.height.dp / density.density)
        val bottomCornerRadius by remember { derivedStateOf { 12.dp } }
        val contentAlignment by remember { derivedStateOf { if (windowHeight >= 480.dp && windowWidth >= 840.dp) Alignment.Center else Alignment.BottomCenter } }

        BackHandler(enabled = show.value) {
            onDismissRequest?.invoke()
        }

        showDialog(
            content = {
                Box(
                    modifier = modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    onDismissRequest?.invoke()
                                }
                            )
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .imePadding()
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures { /* Do nothing to consume the click */ }
                            }
                            .align(contentAlignment)
                            .graphicsLayer(
                                shape = RoundedCornerShape(
                                    topStart = bottomCornerRadius,
                                    topEnd = bottomCornerRadius
                                ),
                                clip = false
                            )
                            .background(
                                color = SuperDialogDefaults.backgroundColor(),
                                shape = RoundedCornerShape(
                                    topStart = bottomCornerRadius,
                                    topEnd = bottomCornerRadius
                                )
                            )
                    ) {
                        val replycontent = remember { mutableStateOf("") }
                        val sendreply = remember { mutableStateOf(false) }
                        val context = LocalContext.current
                        LaunchedEffect(sendreply.value) {
                            if (sendreply.value) {
                                withContext(Dispatchers.IO) {
                                    val timestamp = System.currentTimeMillis() / 1000
                                    val post = sendComment(
                                        username = Global.username,
                                        password = Global.password,
                                        postId = postId,
                                        parentCommentId = replyid,
                                        content = replycontent.value
                                    )
                                    if (post.first == "success") {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                "回复成功！",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                        message.value += 1
                                        dismissDialog(show)
                                        comment.add(Comments(post.second.toInt(), parentCommentId = replyid, Global.username, replycontent.value, timestamp, Global.userQQ.toLong(), true))
                                    } else {
                                        withContext(Dispatchers.Main) {
                                            Toast.makeText(
                                                context,
                                                "评论发送失败，原因：${post.second}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                                sendreply.value = false
                            }
                        }
                        Row() {
                            Text("回复",modifier = Modifier.padding(18.dp))
                            Spacer(modifier = Modifier.weight(1f))
                            Text("发布",modifier = Modifier
                                .padding(18.dp)
                                .clickable {
                                    sendreply.value = true
                                },
                                color = MiuixTheme.colorScheme.primaryVariant)
                        }
                        ReplyTextField(value = replycontent.value,
                            onValueChange = { replycontent.value = it },
                            backgroundColor = Color.Transparent,
                            cornerRadius = 0.dp,
                            label = "回复" + if (replyid == 0) "楼主" else "@${replyname}",
                            minLines = 5,
                            insideMargin = DpSize(16.dp,15.dp),
                            modifier = Modifier.offset(y = (-13).dp)
                        )
                        SmallTitle("postId：${postId}, parentCommentId：${replyid}")
                        Spacer(modifier = Modifier.height(WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding()))
                    }
                }
            }
        )
    }
}

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ReplyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    insideMargin: DpSize = DpSize(16.dp, 15.dp),
    backgroundColor: Color = MiuixTheme.colorScheme.secondaryContainer,
    cornerRadius: Dp = 16.dp,
    label: String = "",
    labelColor: Color = MiuixTheme.colorScheme.onSecondaryContainer,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = MiuixTheme.textStyles.main,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    interactionSource: MutableInteractionSource? = null
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val paddingModifier = remember(insideMargin, leadingIcon, trailingIcon) {
        if (leadingIcon == null && trailingIcon == null) Modifier.padding(insideMargin.width, vertical = insideMargin.height)
        else if (leadingIcon == null) Modifier
            .padding(start = insideMargin.width)
            .padding(vertical = insideMargin.height)
        else if (trailingIcon == null) Modifier
            .padding(end = insideMargin.width)
            .padding(vertical = insideMargin.height)
        else Modifier.padding(vertical = insideMargin.height)
    }
    val labelOffsetY by animateDpAsState(if (value.isNotEmpty()) -(insideMargin.height / 2) else 0.dp)
    val innerTextOffsetY by animateDpAsState(if (value.isNotEmpty()) (insideMargin.height / 2) else 0.dp)
    val labelFontSize by animateDpAsState(if (value.isNotEmpty()) 10.dp else 16.dp)
    val labelOffset = if (label != "") Modifier.offset(y = labelOffsetY) else Modifier
    val innerTextOffset = if (label != "") Modifier.offset(y = innerTextOffsetY) else Modifier

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        visualTransformation = visualTransformation,
        onTextLayout = onTextLayout,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = backgroundColor,
                        shape = SmoothRoundedCornerShape(cornerRadius)
                    )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        leadingIcon()
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(paddingModifier)
                    ) {
                        Text(
                            text = label,
                            textAlign = TextAlign.Start,
                            fontWeight = FontWeight.Medium,
                            fontSize = labelFontSize.value.sp,
                            modifier = Modifier.then(labelOffset),
                            color = labelColor
                        )
                        Box(
                            modifier = Modifier.then(innerTextOffset),
                            contentAlignment = Alignment.BottomStart
                        ) {
                            innerTextField()
                        }
                    }
                    if (trailingIcon != null) {
                        trailingIcon()
                    }
                }
            }
        }
    )
}
