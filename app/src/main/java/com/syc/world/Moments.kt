package com.syc.world

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import java.util.concurrent.TimeUnit

@Composable
fun Moments(topAppBarScrollBehavior: ScrollBehavior,
            padding: PaddingValues,
            navController: NavController
) {
    Scaffold() {
        Column(modifier = Modifier.padding(PaddingValues(top = padding.calculateTopPadding()))) {
            val tabTexts = listOf("默认","最新","热度")
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
                    MomentsItem(1737276065842,"小夜","印度")
                    Spacer(Modifier.height(WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()+65.dp))
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun MomentsItem(time: Long,author: String,ipAddress: String) {
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
        modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp).fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.my),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = 5.dp)
            )
            Image(
                modifier = Modifier
                    .size(10.dp)
                    .offset(x = (-5).dp, y = 10.dp),
                painter = painterResource(id = R.drawable.point_green),
                contentDescription = null
            )
            Column {
                Text(
                    text = author,
                    modifier = Modifier,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(fontStyle = FontStyle.Italic)
                )
                var isTimeAgo by remember { mutableStateOf(true) }
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = (if (isTimeAgo) timeAgo else transToString(time) )+ " | IP地址: ${ipAddress}",
                        modifier = Modifier
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                isTimeAgo = !isTimeAgo
                            },
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
            }
        }
    }
}
