package com.syc.world

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun Chat(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController
) {
    Global.setUnreadCountInChat("4")

    // 模拟的群组数据
    val chatGroups = listOf(
        ChatGroup(
            "3383787570",
            "联系人",
            "酸奶",
            "嘿！咱项目终于完成80%了！",
            GroupType.Internal,
            "晚上7:26",
            3
        ),
        ChatGroup(
            "10001",
            "联系人",
            "陌生人",
            "你们的项目会开源吗？",
            GroupType.External,
            "下午6:22",
            1
        ),
        ChatGroup(
            "940580064",
            "联系人",
            "沉莫",
            "下午好啊小夜",
            GroupType.Internal,
            "下午4:18",
            0
        ),
        ChatGroup(
            "3267887124",
            "群聊",
            "内部交流群",
            "沉莫：你们开发得怎么样了？",
            GroupType.Internal,
            "下午4:07",
            0
        ),
        ChatGroup(
            "2196770895",
            "群聊",
            "外部公开群",
            "陌生人：我很期待你们的项目！",
            GroupType.External,
            "下午3:48",
            0
        )

    )

    Scaffold {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = 0.1.dp,
                    color = Color.LightGray
                )
            }
            items(chatGroups) { group ->
                ChatGroupItem(navController, group)
            }
        }
    }
}

// 群组数据类
data class ChatGroup(
    val groupQQ: String,
    val groupName: String,
    val chatName: String,
    val content: String,
    val type: GroupType,
    val time: String,
    val isUnread: Int
)

// 群组类型
enum class GroupType {
    Internal, // 内部群
    External  // 外部群
}

// 群组数据类
data class ChatMessage(
    val isShowTime: Boolean,
    val chatName: String,
    val sender: SenderType,
    val senderQQ: String,
    val message: String,
    val sendTime: Long
)

// 群组类型
enum class SenderType {
    Me, // 自己
    Others  // 他人
}

// 群组项展示
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatGroupItem(navController: NavController, group: ChatGroup) {
    var imageChange by remember { mutableStateOf(false) }
    val imageSize by animateDpAsState(
        targetValue = if (imageChange) 60.dp else 50.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    if (imageSize == 60.dp) {
        imageChange = false
    }

    LaunchedEffect(Unit) {
        delay(200)
        imageChange = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                .height(70.dp)
                .clip(RoundedCornerShape(16.dp)),
            color = Color.Transparent
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) {
                        Global.setPersonNameBeingChat(group.chatName)
                        Global.setIsShowChat(true)
                        navController.navigate("ChatUi")
                    }
                    .padding(start = 5.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://q.qlogo.cn/headimg_dl?dst_uin=${group.groupQQ}&spec=640&img_type=jpg",
                        contentDescription = null,
                        modifier =
                        if (group.isUnread > 0) {
                            Modifier
                                .size(imageSize)
                                .clip(RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                        }
                    )

                    if (group.isUnread > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 5.dp)
                                .offset(x = 20.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                                    .clip(CircleShape),
                                color = Color.Red
                            ) {
                                Text(
                                    text = group.isUnread.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp)
                        .padding(
                            start = 8.dp, top = 5.dp
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = group.chatName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(bottom = 5.dp),
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = group.content,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier,
                            overflow = TextOverflow.Ellipsis,
                            color = Color.Gray
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(bottom = 30.dp, end = 10.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = group.time,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray
                    )
                }

            }
        }
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 75.dp),
            thickness = 0.1.dp,
            color = Color.LightGray
        )
    }

}

// 单条消息样式
@Composable
fun ChatMessage(message: ChatMessage) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    if (personNameBeingChat.value == message.chatName) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (message.isShowTime) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = transToString(message.sendTime),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray
                    )
                }
            }
            if (message.sender == SenderType.Others) {
                Global.setPersonQQBeingChat(message.senderQQ)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, bottom = 15.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://q.qlogo.cn/headimg_dl?dst_uin=${message.senderQQ}&spec=640&img_type=jpg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight(),
                            color = if (isDarkMode) Color(0xFF313131) else Color.White
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = message.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                }
            } else if (message.sender == SenderType.Me) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 15.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 10.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxHeight(),
                            color = Color(0xFF95EC69)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = message.message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxHeight()
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = "https://q.qlogo.cn/headimg_dl?dst_uin=${message.senderQQ}&spec=640&img_type=jpg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(45.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}


// 聊天界面
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatUi(navController: NavController) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var driveText by remember { mutableStateOf(false) }
    var isSendButtonVisible by remember { mutableStateOf(false) }
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val unreadCountInChat = Global.unreadCountInChat.collectAsState()
    val chatSelection1 = Global.chatSelection1.collectAsState()
    var textFieldChange by remember { mutableStateOf(false) }
    var buttonChange by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        textFieldChange = true
    }

    LaunchedEffect(text) {
        if (text.trim().isNotEmpty()) {
            driveText = true
            delay(200)
            buttonChange = true
            delay(300)
            isSendButtonVisible = true
        } else {
            delay(200)
            isSendButtonVisible = false
            buttonChange = false
            delay(400)
            driveText = false
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(x = 10.dp)
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                Global.setIsShowChat(false)
                                navController.popBackStack()
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                            )

                            if (unreadCountInChat.value.toIntOrNull() != null) {
                                if (unreadCountInChat.value.toInt() > 0) {
                                    Surface(
                                        modifier = Modifier
                                            .width(25.dp)
                                            .height(25.dp)
                                            .clip(CircleShape),
                                        color = if (isDarkMode) Color(0xFF242424) else Color.LightGray
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unreadCountInChat.value,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = when {
                        unreadCountInChat.value.toIntOrNull() != null && unreadCountInChat.value.toInt() > 0 -> {
                            Modifier
                                .fillMaxHeight()
                                .width(200.dp)
                                .offset(x = -(15.dp))
                        }

                        else -> {
                            Modifier
                                .fillMaxHeight()
                                .width(200.dp)
                                .offset(x = -(3.dp))
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = personNameBeingChat.value,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(x = -(20).dp)
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                navController.navigate("ChatSettings")
                            },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp),
                            tint = if (isDarkMode) Color.White else Color.Black
                        )
                    }
                }
            }
            val chatMessage = listOf(
                ChatMessage(
                    true,
                    "酸奶",
                    SenderType.Me,
                    "1640432",
                    "在吗？",
                    1738590703
                ),
                ChatMessage(
                    false,
                    "酸奶",
                    SenderType.Others,
                    "3383787570",
                    "怎么了？",
                    1738590743
                ),
                ChatMessage(
                    false,
                    "酸奶",
                    SenderType.Me,
                    "1640432",
                    "你现在在干嘛呢？",
                    1738590743
                ),
                ChatMessage(
                    false,
                    "酸奶",
                    SenderType.Others,
                    "3383787570",
                    "我在写\"Moments.kt\"界面。",
                    1738590743
                ),
                ChatMessage(
                    false,
                    "酸奶",
                    SenderType.Me,
                    "1640432",
                    "加油！！！",
                    1738590743
                ),
                ChatMessage(
                    true,
                    "陌生人",
                    SenderType.Others,
                    "10001",
                    "你们的项目会开源吗？",
                    1738590743
                ),
                ChatMessage(
                    true,
                    "沉莫",
                    SenderType.Others,
                    "940580064",
                    "下午好啊小夜",
                    1738590743
                ),
            )



            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                items(chatMessage) { message ->
                    ChatMessage(message)
                }
            }

        }

        var lineCount by remember { mutableIntStateOf(1) }

        LaunchedEffect(text) {
            withContext(Dispatchers.IO) {
                while (true) {
                    val newLineCount = text.split("\n").size

                    lineCount = newLineCount.coerceAtMost(4)

                    if (text.length > lineCount * 11) {
                        lineCount = (text.length / 11) + 1
                    }

                    lineCount = lineCount.coerceAtMost(4)

                    delay(500)
                }
            }
        }


        val textFieldHeight by animateDpAsState(
            targetValue = if (lineCount == 1) 50.dp else (50.dp + (lineCount - 1) * 25.dp),
            animationSpec = tween(
                durationMillis = 500,
                easing = FastOutSlowInEasing
            )
        )
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom
        ) {
            Surface(
                modifier = Modifier
                    .height(textFieldHeight + 30.dp)
                    .fillMaxWidth(),
                color = if (isDarkMode) Color(0xFF252525) else Color(0xFFeeeeee)
            ) {
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {

                    val maxWidth =
                        if (text.trim().isNotEmpty()) maxWidth * 0.5f else maxWidth * 0.7f

                    val textFieldWidth by animateDpAsState(
                        targetValue = if (textFieldChange) maxWidth else 0.dp,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.text_input),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .weight(0.1f),
                            tint = if (isDarkMode) Color.White else Color.Black
                        )

                        if (chatSelection1.value) {
                            TextField(
                                modifier = Modifier
                                    .width(textFieldWidth)
                                    .height(textFieldHeight),
                                value = text,
                                onValueChange = { newText -> text = newText },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDarkMode) Color(0xFF2d2d2d) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFF2d2d2d) else Color.White,
                                    cursorColor = Color(0xFF95EC69),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF95EC69),
                                    unfocusedIndicatorColor = Color.White
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        text = ""
                                    }
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Send
                                )
                            )
                        } else {
                            TextField(
                                modifier = Modifier
                                    .width(textFieldWidth)
                                    .height(textFieldHeight),
                                value = text,
                                onValueChange = { newText ->
                                    text = newText
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDarkMode) Color((0xFF2d2d2d)) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color((0xFF2d2d2d)) else Color.White,
                                    cursorColor = Color(0xFF95EC69),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF95EC69),
                                    unfocusedIndicatorColor = Color.White
                                )
                            )
                        }


                        Surface(
                            modifier = Modifier
                                .weight(0.1f)
                                .clip(CircleShape),
                            color = Color.Transparent
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.emotion),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource()
                                    ) {

                                    },
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                        if (driveText) {

                            val buttonSize by animateDpAsState(
                                targetValue = if (buttonChange) 80.dp else 0.dp,
                                animationSpec = tween(
                                    durationMillis = 300,
                                )
                            )

                            Button(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .height(40.dp)
                                    .width(buttonSize),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF07C160),
                                    contentColor = Color.White
                                ),
                                shape = RectangleShape,
                                onClick = {
                                    text = ""
                                }
                            ) {
                                AnimatedVisibility(
                                    visible = isSendButtonVisible,
                                    enter = fadeIn(
                                        animationSpec = tween(durationMillis = 300)
                                    ),
                                    exit = fadeOut(
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "发送",
                                            style = MaterialTheme.typography.bodyMedium,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatSettings(navController: NavController) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val personQQBeingChat = Global.personQQBeingChat.collectAsState()

    val chatSelection1 = Global.chatSelection1.collectAsState()
    val chatSelection2 = Global.chatSelection2.collectAsState()
    val chatSelection3 = Global.chatSelection3.collectAsState()

    data class Selection(
        val isEnterToSendMessage: Boolean,
        val isCloseMessageReminder: Boolean,
        val isPinChat: Boolean
    )

    LaunchedEffect(Unit) {
        Gson().toJson(Selection(chatSelection1.value, chatSelection2.value,chatSelection3.value))
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                Global.setIsShowChat(false)
                                navController.popBackStack()
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset(x = -(22).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "聊天信息",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Avatar(personQQBeingChat.value, personNameBeingChat.value)
                        }
                        Box(
                            modifier = Modifier
                                .offset(y = -(11.dp)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(55.dp),
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                item {
                    Selection(1, painterResource(R.drawable.message_tip), "回车键发送消息", true)
                    Selection(2, painterResource(R.drawable.without_disturb), "消息免打扰", true)
                    Selection(3, painterResource(R.drawable.top), "置顶聊天", false)
                    Spacer(modifier = Modifier.height(20.dp))
                    SelectionWithoutButton(
                        { println("用户点击了投诉") },
                        painterResource(R.drawable.complaints),
                        "投诉"
                    )
                }
            }
        }
    }
}

@Composable
fun Selection(ordinal: Int, painter: Painter, description: String, isDivider: Boolean) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val chatSelection1 = Global.chatSelection1.collectAsState()
    val chatSelection2 = Global.chatSelection2.collectAsState()
    val chatSelection3 = Global.chatSelection3.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, end = 1.dp)
            .height(55.dp),
        color = if (isDarkMode) Color(0xFF1c1c1c) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                        .padding(top = 4.dp),
                    tint = Color.LightGray
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Switch(
                        checked = when (ordinal) {
                            1 -> chatSelection1.value
                            2 -> chatSelection2.value
                            3 -> chatSelection3.value
                            else -> false
                        },
                        onCheckedChange = {
                            when (ordinal) {
                                1 -> {
                                    Global.setChatSelection1(!chatSelection1.value)
                                }

                                2 -> {
                                    Global.setChatSelection2(!chatSelection2.value)
                                }

                                3 -> {
                                    Global.setChatSelection3(!chatSelection3.value)
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF07C160),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }
            if (isDivider) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 4.dp),
                    thickness = 0.1.dp,
                    color = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun SelectionWithoutButton(operation: () -> Unit, painter: Painter, description: String) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, end = 1.dp, bottom = 10.dp)
            .height(55.dp),
        color = if (isDarkMode) Color(0xFF1c1c1c) else Color.White
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    operation()
                }
                .fillMaxSize()
                .padding(start = 15.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(ButtonDefaults.IconSize),
                tint = Color.LightGray
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.enter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(15.dp),
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun Avatar(qq: String, name: String) {
    Column(
        modifier = Modifier
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (qq.trim().isNotEmpty()) {
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painterResource(R.drawable.my),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray
        )
    }
}