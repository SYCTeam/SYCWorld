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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

    // 模拟的群组数据
    val chatGroups = listOf(
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),
        ChatGroup("联系人", "酸奶", GroupType.Internal),
        ChatGroup("联系人", "沉莫", GroupType.External),
        ChatGroup("群聊", "内部交流群", GroupType.Internal),
        ChatGroup("群聊", "外部公开群", GroupType.External),

    )

    Scaffold() {
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
                ChatGroupItem(group)
            }
        }
    }
}

// 群组数据类
data class ChatGroup(val groupName: String, val chatName: String, val type: GroupType)

// 群组类型
enum class GroupType {
    Internal, // 内部群
    External  // 外部群
}

// 群组项展示
@Composable
fun ChatGroupItem(group: ChatGroup) {
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

                    }
                    .padding(start = 5.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://q.qlogo.cn/headimg_dl?dst_uin=3286190647&spec=640&img_type=jpg",
                    contentDescription = null,
                    modifier = Modifier
                        .size(imageSize)
                        .clip(RoundedCornerShape(8.dp))
                )

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(100.dp)
                        .padding(
                            start = 8.dp, top = 5.dp
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = group.chatName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(bottom = 5.dp),
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "今晚吃点啥？",
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
                        text = "下午4:18",
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
