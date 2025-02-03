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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
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
                    MomentsItem(
                        1737276065842,
                        "小夜",
                        "印度",
                        """晋太元中，武陵人捕鱼为业。缘溪行，忘路之远近。忽逢桃花林，夹岸数百步，中无杂树，芳草鲜美，落英缤纷。渔人甚异之，复前行，欲穷其林。

　　林尽水源，便得一山，山有小口，仿佛若有光。便舍船，从口入。初极狭，才通人。复行数十步，豁然开朗。土地平旷，屋舍俨然，有良田、美池、桑竹之属。阡陌交通，鸡犬相闻。其中往来种作，男女衣着，悉如外人。黄发垂髫，并怡然自乐。

　　见渔人，乃大惊，问所从来。具答之。便要还家，设酒杀鸡作食。村中闻有此人，咸来问讯。自云先世避秦时乱，率妻子邑人来此绝境，不复出焉，遂与外人间隔。问今是何世，乃不知有汉，无论魏晋。此人一一为具言所闻，皆叹惋。余人各复延至其家，皆出酒食。停数日，辞去。此中人语云：“不足为外人道也。”(间隔 一作：隔绝)

　　既出，得其船，便扶向路，处处志之。及郡下，诣太守，说如此。太守即遣人随其往，寻向所志，遂迷，不复得路。

　　南阳刘子骥，高尚士也，闻之，欣然规往。未果，寻病终。后遂无问津者。""",
                        listOf(
                            "https://i0.hdslb.com/bfs/article/784edad5f3775aa4e1bf1830e06082226751172.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/e0e409e9f92431db26c380d5b24a5e311177593795.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/d6ab97bf5efa3d0ef680f2def0bc811d1521704887.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/c0b9cc727841a7e5a50ef818548cae5b1521704887.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/article/784edad5f3775aa4e1bf1830e06082226751172.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/e0e409e9f92431db26c380d5b24a5e311177593795.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/d6ab97bf5efa3d0ef680f2def0bc811d1521704887.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/e0e409e9f92431db26c380d5b24a5e311177593795.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/article/784edad5f3775aa4e1bf1830e06082226751172.jpg@1192w.avif",
                            "https://i0.hdslb.com/bfs/new_dyn/c0b9cc727841a7e5a50ef818548cae5b1521704887.jpg@1192w.avif",

                            ),
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

@OptIn(ExperimentalAnimationApi::class)
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun MomentsItem(
    time: Long,
    author: String,
    ipAddress: String,
    elements: String,
    pic: List<String> = emptyList(),
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
            .offset(y = (-10).dp)) {
            Text(
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
            )
            if (pic.isNotEmpty()) {
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
