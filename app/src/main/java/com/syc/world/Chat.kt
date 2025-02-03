package com.syc.world

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior

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
            GroupType.Internal,
            "下午6:22",
            1
        ),
        ChatGroup(
            "940580064",
            "联系人",
            "沉莫",
            "下午好啊小夜",
            GroupType.External,
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
        ),

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
                    thickness = 0.5.dp,
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

// 群组项展示
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
                    .clickable {
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
                            color = MaterialTheme.colorScheme.onBackground
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
            thickness = 0.5.dp,
            color = Color.LightGray
        )
    }

}

// 聊天界面
@Composable
fun ChatUi(navController: NavController) {
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val unreadCountInChat = Global.unreadCountInChat.collectAsState()
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
                            .clickable {
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
                                    .size(40.dp)
                            )

                            if (unreadCountInChat.value.toIntOrNull() != null) {
                                if (unreadCountInChat.value.toInt() > 0) {
                                    Surface(
                                        modifier = Modifier
                                            .width(25.dp)
                                            .height(25.dp)
                                            .clip(CircleShape),
                                        color = Color.LightGray
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
                                                overflow = TextOverflow.Ellipsis,
                                                color = MaterialTheme.colorScheme.onBackground
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
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onBackground
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
                            .clickable {
                                // 点击事件
                            },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        }
    }
}