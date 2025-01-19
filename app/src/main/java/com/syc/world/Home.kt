package com.syc.world

import android.annotation.SuppressLint
import android.content.Intent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun Home(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController
) {
    Scaffold() {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                LatestContentShow()
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

@Composable
fun HeadlineInLargePrint(headline: String) {
    Row(
        modifier = Modifier
            .padding(bottom = 10.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerticalDivider(
            modifier = Modifier
                .padding(end = 10.dp)
                .height(30.dp)
                .clip(RoundedCornerShape(18.dp)),
            thickness = 8.dp,
            color = Color.LightGray
        )
        Text(
            text = headline,
            modifier = Modifier,
            textAlign = TextAlign.Center,
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.onBackground,
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun LatestContentShow() {
    val content by remember { mutableStateOf("我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...") }
    val author by remember { mutableStateOf("沉莫") }
    val ipAddress by remember { mutableStateOf("湖北") }
    val time by remember { mutableStateOf("2025-01-19 16:00:00") }
    Box(
        modifier = Modifier
            .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            HeadlineInLargePrint(headline = "最新动态")
            OutlinedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                border = BorderStroke(1.dp, Color.Black),
                modifier = Modifier
                    .height(200.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        // TODO
                    }
            ) {
                Box(
                    modifier = Modifier
                ) {
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(0.2f)
                        ) {
                            OutlinedCard(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                ),
                                border = BorderStroke(1.dp, Color.Black),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                                    horizontalAlignment = Alignment.Start,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Row(
                                        modifier = Modifier,
                                        horizontalArrangement = Arrangement.Start,
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        Image(
                                            painterResource(R.drawable.my),
                                            contentDescription = null,
                                            modifier = Modifier.size(30.dp)
                                        )
                                        Image(
                                            modifier = Modifier
                                                .size(10.dp),
                                            painter = painterResource(id = R.drawable.point_green),
                                            contentDescription = null
                                        )
                                    }
                                    Text(
                                        text = author,
                                        modifier = Modifier
                                            .padding(10.dp),
                                        textAlign = TextAlign.Center,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = TextStyle(fontStyle = FontStyle.Normal)
                                    )
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(0.8f)
                        ) {
                            Row(
                                modifier = Modifier,
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "发布于",
                                    modifier = Modifier
                                        .padding(start = 10.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = TextStyle(fontStyle = FontStyle.Normal)
                                )
                                Text(
                                    text = ipAddress,
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                                VerticalDivider(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .height(15.dp)
                                        .clip(RoundedCornerShape(18.dp)),
                                    thickness = 2.dp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = time,
                                    modifier = Modifier,
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = TextStyle(fontStyle = FontStyle.Normal)
                                )
                            }
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = content,
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 20.dp),
                        textAlign = TextAlign.Center,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontStyle = FontStyle.Normal),
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            var isFirstRun by remember { mutableStateOf(true) }
            // 点赞数
            var favoriteCounts by remember { mutableIntStateOf(0) }
            // 评论数
            var commentsCounts by remember { mutableIntStateOf(0) }
            // 转发数
            var sharedCounts by remember { mutableIntStateOf(0) }

            val animatedFavoriteCounts by animateIntAsState(
                targetValue = favoriteCounts,
                animationSpec = if (isFirstRun) tween(durationMillis = 1000) else tween(
                    durationMillis = 0
                )
            )

            val animatedCommentsCounts by animateIntAsState(
                targetValue = commentsCounts,
                animationSpec = if (isFirstRun) tween(durationMillis = 1000) else tween(
                    durationMillis = 0
                )
            )

            val animatedSharedCounts by animateIntAsState(
                targetValue = sharedCounts,
                animationSpec = if (isFirstRun) tween(durationMillis = 1000) else tween(
                    durationMillis = 0
                )
            )

            LaunchedEffect(Unit) {
                favoriteCounts = 2
                commentsCounts = 64
                sharedCounts = 128
            }
            var isClick by remember { mutableStateOf(false) }
            var isChange by remember { mutableStateOf(false) }
            val buttonSize by animateDpAsState(
                targetValue = if (isChange) 40.dp else 30.dp,
                animationSpec = tween(
                    durationMillis = 100,
                    easing = FastOutSlowInEasing
                ),
                label = ""
            )
            if (buttonSize == 40.dp) {
                isChange = false
            }
            LaunchedEffect(isClick) {
                if (isClick) {
                    favoriteCounts++
                } else if (!isFirstRun) {
                    favoriteCounts--
                } else {
                    delay(1000)
                    isFirstRun = false
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 点赞
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isClick) {
                            Image(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(buttonSize)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource()
                                    ) {
                                        if (!isFirstRun) {
                                            isChange = !isChange
                                            isClick = !isClick
                                        }
                                    },
                                painter = painterResource(id = R.drawable.thumbs_up),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(buttonSize)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource()
                                    ) {
                                        if (!isFirstRun) {
                                            isChange = !isChange
                                            isClick = !isClick
                                        }
                                    },
                                painter = painterResource(id = R.drawable.thumbs_up),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = animatedFavoriteCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                }
                // 评论
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    if (!isFirstRun) {
                                        // TODO
                                    }
                                },
                            painter = painterResource(id = R.drawable.comments),
                            contentDescription = null
                        )
                        Text(
                            text = animatedCommentsCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                }
                val context = LocalContext.current
                // 转发
                Box(
                    modifier = Modifier
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = MutableInteractionSource()
                                ) {
                                    if (!isFirstRun) {
                                        // 创建分享的 Intent
                                        val shareIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(
                                                Intent.EXTRA_TEXT,
                                                "【来自${author}的动态】: $content"
                                            )
                                            type = "text/plain"
                                        }
                                        context.startActivity(
                                            Intent.createChooser(
                                                shareIntent,
                                                "分享到"
                                            )
                                        )
                                    }
                                },
                            painter = painterResource(id = R.drawable.share),
                            contentDescription = null
                        )
                        Text(
                            text = animatedSharedCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                }
            }
            HeadlineInLargePrint(headline = "步数排行")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 30.dp)
                        .padding(bottom = 6.dp, top = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally  // Column 中内容水平居中
                    ) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                        Image(
                            painterResource(R.drawable.silver),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(y = (-15).dp)
                        )
                        Text(
                            text = "酸奶",
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "6000",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-15).dp)
                            )
                            Text(
                                text = " 步",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-17).dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally  // Column 中内容水平居中
                    ) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(55.dp)
                        )
                        Image(
                            painterResource(R.drawable.gold),
                            contentDescription = null,
                            modifier = Modifier
                                .size(35.dp)
                                .offset(y = (-20).dp)
                        )
                        Text(
                            text = "沉莫",
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "10000",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-15).dp)
                            )
                            Text(
                                text = " 步",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-17).dp)
                            )
                        }
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally  // Column 中内容水平居中
                    ) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                        Image(
                            painterResource(R.drawable.copper),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .offset(y = (-15).dp)
                        )
                        Text(
                            text = "小夜",
                            fontSize = 13.sp,
                            color = Color.Black,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "3000",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-15).dp)
                            )
                            Text(
                                text = " 步",
                                fontSize = 13.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Light,
                                modifier = Modifier.offset(y = (-17).dp)
                            )
                        }
                    }
                }

            }
        }
    }
}
