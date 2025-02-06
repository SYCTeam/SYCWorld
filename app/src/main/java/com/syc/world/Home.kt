package com.syc.world

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
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
import coil3.compose.AsyncImage
import com.syc.world.Global.NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Composable
fun Home(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController
) {
    Scaffold {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                LatestContentShow()
                StepRank()
                Spacer(
                    Modifier.height(
                        WindowInsets.navigationBars.asPaddingValues()
                            .calculateBottomPadding() + 65.dp
                    )
                )
            }
        }
        ViewOthersPopup()
    }

}

fun openQQProfile(context: Context, qqNumber: String) {
    // 构建 QQ 个人资料卡 URL
    val url =
        "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=$qqNumber&card_type=person&source=qrcode"

    // 创建 Intent
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    // 如果设备上已安装 QQ 应用，跳转到个人资料卡页面
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    } else {
        // 如果没有安装 QQ，提示用户安装 QQ 或者可以选择打开网页版
        val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://w.qq.com"))
        context.startActivity(webIntent)
    }
}

@Composable
fun ViewOthersPopup() {
    val context = LocalContext.current
    var isSendMailAnimation by remember { mutableStateOf(false) }
    var isSendMail by remember { mutableStateOf(false) }
    val isShowState = Global.isShowState.collectAsState()
    val personNameBeingViewed = Global.personNameBeingViewed.collectAsState()
    val personQQBeingViewed = Global.personQQBeingViewed.collectAsState()
    val personIsOnlineBeingViewed = Global.personIsOnlineBeingViewed.collectAsState()
    val personSynopsisBeingViewed = Global.personSynopsisBeingViewed.collectAsState()
    val personRegisterAddressBeingViewed =
        Global.personRegisterAddressBeingViewed.collectAsState()
    val personLoginAddressBeingViewed = Global.personLoginAddressBeingViewed.collectAsState()
    val personLastAccessTimeBeingViewed =
        Global.personLastAccessTimeBeingViewed.collectAsState()
    val personStepCountBeingViewed = Global.personStepCountBeingViewed.collectAsState()
    val personLoginCountBeingViewed = Global.personLoginCountBeingViewed.collectAsState()

    AnimatedVisibility(
        visible = isShowState.value,
        enter = fadeIn(
            animationSpec = tween(durationMillis = 300)
        ),
        exit = fadeOut(
            animationSpec = tween(durationMillis = 300)
        )
    ) {
        Surface(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    Global.setIsShowState(false)
                }
                .fillMaxSize()
                .alpha(0.5f),
            color = Color.Black
        ) {

        }
    }

    LaunchedEffect(isShowState.value) {
        if (isShowState.value) {
            delay(500)
            isSendMailAnimation = true
        } else {
            isSendMailAnimation = false
        }
    }

    LaunchedEffect(isSendMail) {
        if (isSendMail) {
            delay(500)
            isSendMailAnimation = false
            withContext(Dispatchers.IO) {
                val mailResult = mail(personQQBeingViewed.value, Global.username)
                if (mailResult.first == "success") {
                    isSendMail = false
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "您的邀请已发出！",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    isSendMail = false
                    Log.d("邮箱问题", mailResult.second)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "邀请失败，请稍后再试。",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    AnimatedVisibility(
        visible = isShowState.value,
        enter = scaleIn(
            initialScale = 0f,
            animationSpec = tween(
                durationMillis = 500
            )
        ) + slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 500
            )
        ),
        exit = scaleOut(
            targetScale = 0f,
            animationSpec = tween(
                durationMillis = 500
            )
        ) + slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(
                durationMillis = 500
            )
        )
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            ElevatedCard(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Top
                ) {

                    Spacer(modifier = Modifier.height(10.dp))

                    HeadlineInLargePrint(headline = "$NAME · ${personNameBeingViewed.value}")

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(bottom = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = "https://q.qlogo.cn/headimg_dl?dst_uin=${personQQBeingViewed.value}&spec=640&img_type=jpg",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(CircleShape)
                                )
                                if (personIsOnlineBeingViewed.value) {
                                    Image(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .offset(x = (-20).dp, y = 30.dp),
                                        painter = painterResource(id = R.drawable.point_green),
                                        contentDescription = null
                                    )
                                } else {
                                    Image(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .offset(x = (-20).dp, y = 30.dp),
                                        painter = painterResource(id = R.drawable.point_gray),
                                        contentDescription = null
                                    )
                                }
                            }
                            if (!personIsOnlineBeingViewed.value) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${personLastAccessTimeBeingViewed.value}在线",
                                        modifier = Modifier,
                                        fontSize = 15.sp,
                                        color = Color.LightGray,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (personNameBeingViewed.value != Global.username) {
                                        AnimatedVisibility(
                                            visible = isSendMailAnimation,
                                            enter = fadeIn(
                                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                                            ) + slideInHorizontally(
                                                initialOffsetX = { it + 200 },
                                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                                            ),
                                            exit = fadeOut(
                                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                                            ) + slideOutHorizontally(
                                                targetOffsetX = { it + 200 },
                                                animationSpec = tween(500, easing = FastOutSlowInEasing)
                                            )
                                        ) {
                                            Surface(
                                                modifier = Modifier
                                                    .padding(start = 10.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .clickable {
                                                        isSendMail = true
                                                    }
                                            ) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .size(ButtonDefaults.IconSize)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                            Text(
                                text = "\" ${personSynopsisBeingViewed.value} \"",
                                modifier = Modifier.padding(5.dp),
                                fontSize = 20.sp,
                                style = TextStyle(fontStyle = FontStyle.Italic),
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "注册于",
                                    modifier = Modifier,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = personRegisterAddressBeingViewed.value,
                                    modifier = Modifier,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            if (personLoginAddressBeingViewed.value != "无") {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp, end = 10.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "目前在",
                                        modifier = Modifier,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                    Text(
                                        text = personLoginAddressBeingViewed.value,
                                        modifier = Modifier,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "今日行走",
                                    modifier = Modifier,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = personStepCountBeingViewed.value.toString(),
                                    modifier = Modifier,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "步",
                                    modifier = Modifier,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp, end = 10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "累计登录$NAME",
                                    modifier = Modifier,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                                Text(
                                    text = personLoginCountBeingViewed.value.toString(),
                                    modifier = Modifier,
                                    fontSize = 20.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "次",
                                    modifier = Modifier,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    }

                    if (personNameBeingViewed.value == Global.username) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            onClick = {
                                Global.setIsShowState(false)
                            }) {
                            Icon(
                                Icons.Filled.Done,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "我知道了",
                                textAlign = TextAlign.Center,
                                fontSize = 15.sp,
                                modifier = Modifier,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                modifier = Modifier
                                    .padding(top = 5.dp, bottom = 10.dp)
                                    .weight(1f),
                                onClick = {
                                    openQQProfile(context, personQQBeingViewed.value)
                                }) {
                                Icon(
                                    Icons.Filled.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "加个QQ",
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Button(
                                modifier = Modifier
                                    .padding(start = 10.dp, top = 5.dp, bottom = 10.dp)
                                    .weight(1f),
                                onClick = {
                                    Global.setIsShowState(false)
                                }) {
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "我知道了",
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    modifier = Modifier,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getTimeAgo(timestamp: Long): String {
    val adjustedTimestamp = if (timestamp < 1000000000000L) {
        timestamp * 1000
    } else {
        timestamp
    }

    val currentTime = System.currentTimeMillis()
    val diffInMillis = currentTime - adjustedTimestamp

    return when {
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
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.inversePrimary,
            style = TextStyle(fontWeight = FontWeight.Bold),
            overflow = TextOverflow.Ellipsis
        )
    }
}


@SuppressLint("SimpleDateFormat", "WeekBasedYear")
fun transToString(time: Long): String {
    val isMillisecond = time > 9999999999L
    val pattern = if (isMillisecond) "yyyy-MM-dd HH:mm:ss.SSS" else "yyyy-MM-dd HH:mm:ss"
    val formatter = SimpleDateFormat(pattern)
    val date = if (isMillisecond) Date(time) else Date(time * 1000)
    return formatter.format(date)
}

@SuppressLint("SimpleDateFormat", "WeekBasedYear")
fun transToString1(time: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return formatter.format(Date(time))
}

/*
// 通过字符串转化为时间戳
@SuppressLint("SimpleDateFormat", "WeekBasedYear")
fun transToTimeStamp(date:String):Long{
    return SimpleDateFormat("YY-MM-DD-hh-mm-ss").parse(date, ParsePosition(0)).time
}
*/

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun LatestContentShow() {
    val content by remember { mutableStateOf("我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...我失恋了...") }
    val author by remember { mutableStateOf("沉莫") }
    val ipAddress by remember { mutableStateOf("湖北") }
    val time by remember { mutableStateOf("1737276065842") }

    val timestamp = remember { System.currentTimeMillis() }
    val diffInMillis = timestamp - time.toLong()
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
                                text = if (isTimeAgo) timeAgo else time.toLongOrNull()
                                    ?.let { transToString(it) } ?: "",
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
                            VerticalDivider(
                                modifier = Modifier
                                    .padding(3.dp)
                                    .height(15.dp)
                                    .clip(RoundedCornerShape(18.dp)),
                                thickness = 2.dp,
                                color = Color.Gray
                            )
                            Text(
                                text = "IP地址:",
                                modifier = Modifier,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = TextStyle(fontStyle = FontStyle.Normal)
                            )
                            Text(
                                text = ipAddress,
                                modifier = Modifier,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = TextStyle(fontWeight = FontWeight.Bold)
                            )
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                if (!isFirstRun) {
                                    isChange = !isChange
                                    isClick = !isClick
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isClick) {
                            Image(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(buttonSize),
                                painter = painterResource(id = R.drawable.thumbs_up),
                                contentDescription = null
                            )
                        } else {
                            Image(
                                painter = painterResource(R.drawable.thumbs_up),
                                contentDescription = null,
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
                                colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                            )
                        }
                        Text(
                            text = animatedFavoriteCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                if (!isFirstRun) {
                                    // TODO
                                }
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.comments),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = animatedCommentsCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
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
                        modifier = Modifier
                            .fillMaxWidth()
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.share),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(30.dp),
                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onSurface)
                        )
                        Text(
                            text = animatedSharedCounts.toString(),
                            modifier = Modifier
                                .padding(10.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun StepRank() {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var userQQ by remember { mutableStateOf("...") }

    var stepCount by remember { mutableIntStateOf(0) }
    var rank by remember { mutableStateOf(emptyList<RankInfo>()) }

    var rank1Name by remember { mutableStateOf("") }
    var rank1QQ by remember { mutableStateOf("") }
    var rank1StepCount by remember { mutableIntStateOf(-1) }
    var rank1IsOnline by remember { mutableStateOf(false) }
    var rank1LastAccessTime by remember { mutableStateOf("") }
    var rank1Synopsis by remember { mutableStateOf("") }
    var rank1RegisterAddress by remember { mutableStateOf("") }
    var rank1LoginAddress by remember { mutableStateOf("") }
    var rank1LoginCount by remember { mutableIntStateOf(0) }

    var rank2Name by remember { mutableStateOf("") }
    var rank2QQ by remember { mutableStateOf("") }
    var rank2StepCount by remember { mutableIntStateOf(-1) }
    var rank2IsOnline by remember { mutableStateOf(false) }
    var rank2LastAccessTime by remember { mutableStateOf("") }
    var rank2Synopsis by remember { mutableStateOf("") }
    var rank2RegisterAddress by remember { mutableStateOf("") }
    var rank2LoginAddress by remember { mutableStateOf("") }
    var rank2LoginCount by remember { mutableIntStateOf(0) }

    var rank3Name by remember { mutableStateOf("") }
    var rank3QQ by remember { mutableStateOf("") }
    var rank3StepCount by remember { mutableIntStateOf(-1) }
    var rank3IsOnline by remember { mutableStateOf(false) }
    var rank3LastAccessTime by remember { mutableStateOf("") }
    var rank3Synopsis by remember { mutableStateOf("") }
    var rank3RegisterAddress by remember { mutableStateOf("") }
    var rank3LoginAddress by remember { mutableStateOf("") }
    var rank3LoginCount by remember { mutableIntStateOf(0) }


    var isRotating by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isRotating) 360f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )
    var isFirstRun by remember { mutableStateOf(false) }
    var isFirstFlushed by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            while (true) {
                val readResult: String = readFromFile(context, "stepCount")
                stepCount =
                    if (readResult.toIntOrNull() != null) readResult.toInt() else 0
                delay(500)
            }
        }
    }

    LaunchedEffect(isLoading) {
        if (Global.username.trim().isNotEmpty()) {
            while (isLoading) {
                withContext(Dispatchers.IO) {
                    val userInfoFirst = getUserInformation(Global.username)
                    if (isJson(userInfoFirst)) {
                        val userInfo = parseUserInfo(userInfoFirst)
                        if (userInfo != null && userInfo.qq.isNotEmpty()) {
                            userQQ = userInfo.qq
                        }

                    }
                }
                delay(1000)
            }
        }
    }

    LaunchedEffect(userQQ, rank) {
        if (userQQ.trim().isNotEmpty() && userQQ != "..." && rank.isNotEmpty()) {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        while (Global.url.contains("http") && rank.isEmpty()) {
            withContext(Dispatchers.IO) {
                if (Global.url.contains("http")) {
                    rank = getRank()

                    if (rank.isNotEmpty()) {
                        rank1Name = rank[0].username
                        rank1QQ = rank[0].qq
                        rank1StepCount = rank[0].stepCount

                        Log.d("排名问题", "第一名: $rank1Name, QQ: $rank1QQ, 步数: $rank1StepCount")

                        if (rank.size > 1) {
                            rank2Name = rank[1].username
                            rank2QQ = rank[1].qq
                            rank2StepCount = rank[1].stepCount

                            Log.d(
                                "排名问题",
                                "第二名: $rank2Name, QQ: $rank2QQ, 步数: $rank2StepCount"
                            )
                        }

                        if (rank.size > 2) {
                            rank3Name = rank[2].username
                            rank3QQ = rank[2].qq
                            rank3StepCount = rank[2].stepCount

                            Log.d(
                                "排名问题",
                                "第三名: $rank3Name, QQ: $rank3QQ, 步数: $rank3StepCount"
                            )
                        }
                    }
                    isRotating = true
                }
            }
            delay(5000)
        }

        while (Global.url.contains("http") && rank.isNotEmpty()) {
            if (!isFirstFlushed) {
                withContext(Dispatchers.IO) {
                    if (Global.url.contains("http")) {
                        rank = getRank()

                        if (rank.isNotEmpty()) {
                            rank1Name = rank[0].username
                            rank1QQ = rank[0].qq
                            rank1StepCount = rank[0].stepCount
                            Log.d(
                                "排名问题",
                                "------------------------------------------------------------"
                            )

                            Log.d(
                                "排名问题",
                                "第一名: $rank1Name, QQ: $rank1QQ, 步数: $rank1StepCount"
                            )

                            if (rank.size > 1) {
                                rank2Name = rank[1].username
                                rank2QQ = rank[1].qq
                                rank2StepCount = rank[1].stepCount
                                Log.d(
                                    "排名问题",
                                    "第二名: $rank2Name, QQ: $rank2QQ, 步数: $rank2StepCount"
                                )
                            }

                            if (rank.size > 2) {
                                rank3Name = rank[2].username
                                rank3QQ = rank[2].qq
                                rank3StepCount = rank[2].stepCount
                                Log.d(
                                    "排名问题",
                                    "第三名: $rank3Name, QQ: $rank3QQ, 步数: $rank3StepCount"
                                )

                                Log.d(
                                    "排名问题",
                                    "------------------------------------------------------------"
                                )
                            }
                        }
                    }
                }
            } else {
                isFirstFlushed = false
            }
            delay(20000)
        }
    }

    var isRank1ReLoading by remember { mutableStateOf(false) }
    var isRank2ReLoading by remember { mutableStateOf(false) }
    var isRank3ReLoading by remember { mutableStateOf(false) }

    LaunchedEffect(rank) {
        if (rank.isNotEmpty()) {
            isRank1ReLoading = true
            isRank2ReLoading = true
            isRank3ReLoading = true
        }
    }

    Column(modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp)) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HeadlineInLargePrint(headline = "步数排行")
            Row(
                modifier = Modifier
                    .padding(start = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "我的步数: ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = stepCount.toString(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = " 步",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Light
                )

                LaunchedEffect(isRotating) {
                    if (isFirstRun) {
                        withContext(Dispatchers.IO) {
                            val readResult: String = readFromFile(context, "stepCount")
                            stepCount = if (readResult.toIntOrNull() != null) readResult.toInt() else 0
                            if (Global.url.contains("http")) {
                                modifyStepCount(
                                    Global.username,
                                    Global.password,
                                    stepCount.toString()
                                )
                            }

                            if (Global.url.contains("http")) {
                                rank = getRank()

                                if (rank.isNotEmpty()) {
                                    rank1Name = rank[0].username
                                    rank1QQ = rank[0].qq
                                    rank1StepCount = rank[0].stepCount
                                    Log.d(
                                        "排名问题",
                                        "------------------------------------------------------------"
                                    )

                                    Log.d(
                                        "排名问题",
                                        "第一名: $rank1Name, QQ: $rank1QQ, 步数: $rank1StepCount"
                                    )

                                    if (rank.size > 1) {
                                        rank2Name = rank[1].username
                                        rank2QQ = rank[1].qq
                                        rank2StepCount = rank[1].stepCount
                                        Log.d(
                                            "排名问题",
                                            "第二名: $rank2Name, QQ: $rank2QQ, 步数: $rank2StepCount"
                                        )
                                    }

                                    if (rank.size > 2) {
                                        rank3Name = rank[2].username
                                        rank3QQ = rank[2].qq
                                        rank3StepCount = rank[2].stepCount
                                        Log.d(
                                            "排名问题",
                                            "第三名: $rank3Name, QQ: $rank3QQ, 步数: $rank3StepCount"
                                        )

                                        Log.d(
                                            "排名问题",
                                            "------------------------------------------------------------"
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        isFirstRun = true
                    }
                }

                Image(
                    modifier = Modifier
                        .padding(start = 10.dp, bottom = 3.dp)
                        .size(25.dp)
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            isRotating = !isRotating

                        }
                        .graphicsLayer {
                            rotationX = rotation
                        },
                    painter = painterResource(id = R.drawable.flushed),
                    contentDescription = "点击刷新。"
                )
            }
        }

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
                var step by remember { mutableFloatStateOf(0f) }
                var step2 by remember { mutableFloatStateOf(0f) }
                var step3 by remember { mutableFloatStateOf(0f) }
                LaunchedEffect(rank) {
                    while (true) {
                        if (rank.isNotEmpty()) {
                            step = rank1StepCount.toFloat()
                            step2 = rank2StepCount.toFloat()
                            step3 = rank3StepCount.toFloat()
                            break
                        }
                        delay(100)
                    }
                }
                LaunchedEffect(rank1StepCount) {
                    delay(600)
                    val randomChange = Random.nextFloat() * 100f
                    step += randomChange  // 增加随机值
                    delay(600)
                    step -= randomChange
                }
                LaunchedEffect(rank2StepCount) {
                    delay(600)
                    val randomChange = Random.nextFloat() * 100f
                    step2 += randomChange  // 增加随机值
                    delay(600)
                    step2 -= randomChange
                }
                LaunchedEffect(rank3StepCount) {
                    delay(600)
                    val randomChange = Random.nextFloat() * 100f
                    step3 += randomChange  // 增加随机值
                    delay(600)
                    step3 -= randomChange
                }
                val animatedValue = animateFloatAsState(
                    targetValue = step,
                    animationSpec = tween(durationMillis = 600)
                )
                val animatedValue2 = animateFloatAsState(
                    targetValue = step2,
                    animationSpec = tween(durationMillis = 600)
                )
                val animatedValue3 = animateFloatAsState(
                    targetValue = step3,
                    animationSpec = tween(durationMillis = 600)
                )

                var isFirstClickPerson by remember { mutableStateOf(true) }

                // 获取排行榜第1的用户的信息
                LaunchedEffect(isRank1ReLoading) {
                    withContext(Dispatchers.IO) {
                        while (isRank1ReLoading && rank.isNotEmpty()) {
                            Log.d("Rank1Data", "开始重新获取Rank1的信息...")
                            val userInfoFirst = getUserInformation(rank[0].username)
                            Log.d("Rank1Data", userInfoFirst)
                            if (isJson(userInfoFirst)) {
                                Log.d("Rank1Data", "Rank1Data是Json数据")
                                val userInfo1 = parseUserInfo(userInfoFirst)
                                if (userInfo1 != null &&
                                    userInfo1.registerIp.isNotEmpty() &&
                                    userInfo1.synopsis.isNotEmpty() &&
                                    userInfo1.loginCount.isNotEmpty() &&
                                    userInfo1.online.isNotEmpty() &&
                                    userInfo1.qq.isNotEmpty() &&
                                    userInfo1.stepCount.isNotEmpty() &&
                                    userInfo1.username.isNotEmpty()
                                ) {

                                    Log.d("Rank1Data", "开始写入变量")
                                    rank1Synopsis =
                                        userInfo1.synopsis // 防止 userInfo1.synopsis 为 null
                                    rank1RegisterAddress = getAddressFromIp(userInfo1.registerIp)
                                    rank1LoginAddress = getAddressFromIp(userInfo1.loginIp)
                                    rank1IsOnline = userInfo1.online == "在线"
                                    rank1LastAccessTime = getTimeAgo(userInfo1.lastAccessTime)

                                    // 防止空值转化为整数
                                    rank1StepCount =
                                        if (userInfo1.stepCount.toIntOrNull() != null) {
                                            userInfo1.stepCount.toInt()
                                        } else {
                                            -1 // 或者设置一个默认值
                                        }

                                    rank1LoginCount =
                                        if (userInfo1.loginCount.toIntOrNull() != null) {
                                            userInfo1.loginCount.toInt()
                                        } else {
                                            0 // 或者设置一个默认值
                                        }

                                    // 这里确保所有的字段都已正确赋值
                                    if (rank1Name.trim().isNotEmpty() &&
                                        rank1QQ.trim().isNotEmpty() &&
                                        rank1Synopsis.trim().isNotEmpty() &&
                                        rank1RegisterAddress.trim().isNotEmpty() &&
                                        rank1RegisterAddress != "无" &&
                                        rank1LoginAddress.trim().isNotEmpty() &&
                                        rank1LastAccessTime.trim().isNotEmpty() &&
                                        rank1StepCount != -1 &&
                                        rank1LoginCount != -1
                                    ) {

                                        isRank1ReLoading = false
                                        break
                                    }
                                }

                            }
                            delay(2000)
                        }
                    }
                }

                // 获取排行榜第2的用户的信息
                LaunchedEffect(isRank2ReLoading) {
                    withContext(Dispatchers.IO) {
                        while (isRank2ReLoading && rank.isNotEmpty() && rank.size > 1) {
                            Log.d("rank2Data", "开始重新获取rank2的信息...")
                            val userInfoFirst = getUserInformation(rank[1].username)
                            Log.d("rank2Data", userInfoFirst)
                            if (isJson(userInfoFirst)) {
                                Log.d("rank2Data", "rank2Data是Json数据")
                                val userInfo2 = parseUserInfo(userInfoFirst)
                                if (userInfo2 != null &&
                                    userInfo2.registerIp.isNotEmpty() &&
                                    userInfo2.synopsis.isNotEmpty() &&
                                    userInfo2.loginCount.isNotEmpty() &&
                                    userInfo2.online.isNotEmpty() &&
                                    userInfo2.qq.isNotEmpty() &&
                                    userInfo2.stepCount.isNotEmpty() &&
                                    userInfo2.username.isNotEmpty()
                                ) {

                                    Log.d("rank2Data", "开始写入变量")
                                    rank2Synopsis =
                                        userInfo2.synopsis // 防止 userInfo.synopsis 为 null
                                    rank2RegisterAddress = getAddressFromIp(userInfo2.registerIp)
                                    rank2LoginAddress = getAddressFromIp(userInfo2.loginIp)
                                    rank2IsOnline = userInfo2.online == "在线"
                                    rank2LastAccessTime = getTimeAgo(userInfo2.lastAccessTime)

                                    // 防止空值转化为整数
                                    rank2StepCount =
                                        if (userInfo2.stepCount.toIntOrNull() != null) {
                                            userInfo2.stepCount.toInt()
                                        } else {
                                            -1
                                        }

                                    rank2LoginCount =
                                        if (userInfo2.loginCount.toIntOrNull() != null) {
                                            userInfo2.loginCount.toInt()
                                        } else {
                                            0 // 或者设置一个默认值
                                        }

                                    // 这里确保所有的字段都已正确赋值
                                    if (rank2Name.trim().isNotEmpty() &&
                                        rank2QQ.trim().isNotEmpty() &&
                                        rank2Synopsis.trim().isNotEmpty() &&
                                        rank2RegisterAddress.trim().isNotEmpty() &&
                                        rank2RegisterAddress != "无" &&
                                        rank2LoginAddress.trim().isNotEmpty() &&
                                        rank2LastAccessTime.trim().isNotEmpty() &&
                                        rank2StepCount != -1 &&
                                        rank2LoginCount != -1
                                    ) {

                                        isRank2ReLoading = false
                                        break
                                    }
                                }

                            }
                            delay(2000)
                        }
                    }
                }

                // 获取排行榜第3的用户的信息
                LaunchedEffect(isRank3ReLoading) {
                    withContext(Dispatchers.IO) {
                        while (isRank3ReLoading && rank.isNotEmpty() && rank.size > 2) {
                            Log.d("Rank3Data", "开始重新获取Rank3的信息...")
                            val userInfoFirst = getUserInformation(rank[2].username)
                            Log.d("Rank3Data", userInfoFirst)
                            if (isJson(userInfoFirst)) {
                                Log.d("Rank3Data", "Rank3Data是Json数据")
                                val userInfo3 = parseUserInfo(userInfoFirst)
                                if (userInfo3 != null &&
                                    userInfo3.registerIp.isNotEmpty() &&
                                    userInfo3.synopsis.isNotEmpty() &&
                                    userInfo3.loginCount.isNotEmpty() &&
                                    userInfo3.online.isNotEmpty() &&
                                    userInfo3.qq.isNotEmpty() &&
                                    userInfo3.stepCount.isNotEmpty() &&
                                    userInfo3.username.isNotEmpty()
                                ) {

                                    Log.d("Rank3Data", "开始写入变量")
                                    rank3Synopsis =
                                        userInfo3.synopsis // 防止 userInfo3.synopsis 为 null
                                    rank3RegisterAddress = getAddressFromIp(userInfo3.registerIp)
                                    rank3LoginAddress = getAddressFromIp(userInfo3.loginIp)
                                    rank3IsOnline = userInfo3.online == "在线"
                                    rank3LastAccessTime = getTimeAgo(userInfo3.lastAccessTime)

                                    // 防止空值转化为整数
                                    rank3StepCount =
                                        if (userInfo3.stepCount.toIntOrNull() != null) {
                                            userInfo3.stepCount.toInt()
                                        } else {
                                            -1 // 或者设置一个默认值
                                        }

                                    rank3LoginCount =
                                        if (userInfo3.loginCount.toIntOrNull() != null) {
                                            userInfo3.loginCount.toInt()
                                        } else {
                                            0 // 或者设置一个默认值
                                        }

                                    // 这里确保所有的字段都已正确赋值
                                    if (rank3Name.trim().isNotEmpty() &&
                                        rank3QQ.trim().isNotEmpty() &&
                                        rank3Synopsis.trim().isNotEmpty() &&
                                        rank3RegisterAddress.trim().isNotEmpty() &&
                                        rank3RegisterAddress != "无" &&
                                        rank3LoginAddress.trim().isNotEmpty() &&
                                        rank3LastAccessTime.trim().isNotEmpty() &&
                                        rank3StepCount != -1 &&
                                        rank3LoginCount != -1
                                    ) {

                                        isRank3ReLoading = false
                                        break
                                    }
                                }

                            }
                            delay(2000)
                        }
                    }
                }

                LaunchedEffect(isFirstClickPerson) {
                    if (!isFirstClickPerson) {
                        delay(5000)
                        while (true) {
                            isRank1ReLoading = true
                            isRank2ReLoading = true
                            isRank3ReLoading = true
                            delay(10000)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            if (rank2Name.trim().isNotEmpty() && rank2QQ.trim().isNotEmpty() &&
                                rank2Synopsis.trim().isNotEmpty() && rank2RegisterAddress.trim()
                                    .isNotEmpty() &&
                                rank2RegisterAddress != "无" && rank2LoginAddress.trim()
                                    .isNotEmpty() &&
                                rank2LastAccessTime.trim().isNotEmpty() && rank2StepCount != -1
                            ) {

                                Global.setPersonNameBeingViewed(rank2Name)
                                Global.setPersonSynopsisBeingViewed(rank2Synopsis)
                                Global.setPersonRegisterAddressBeingViewed(rank2RegisterAddress)
                                Global.setPersonLoginAddressBeingViewed(rank2LoginAddress)
                                Global.setPersonIsOnlineBeingViewed(rank2IsOnline)
                                Global.setPersonQQBeingViewed(rank2QQ)
                                Global.setPersonLastAccessTimeBeingViewed(rank2LastAccessTime)
                                Global.setPersonStepCountBeingViewed(rank2StepCount)
                                Global.setPersonLoginCountBeingViewed(rank2LoginCount)
                                Global.setIsShowState(true)
                            } else {
                                // 打印出所有相关的数据以便调试
                                Log.d("Rank2Data", "rank2Name: $rank2Name")
                                Log.d("Rank2Data", "rank2QQ: $rank2QQ")
                                Log.d("Rank2Data", "rank2Synopsis: $rank2Synopsis")
                                Log.d("Rank2Data", "rank2RegisterAddress: $rank2RegisterAddress")
                                Log.d("Rank2Data", "rank2LoginAddress: $rank2LoginAddress")
                                Log.d("Rank2Data", "rank2LastAccessTime: $rank2LastAccessTime")

                                Toast.makeText(
                                    context,
                                    "信息正在获取中，请重新点击！",
                                    Toast.LENGTH_LONG
                                ).show()
                                isRank2ReLoading = true
                            }

                            if (isFirstClickPerson) {
                                isFirstClickPerson = false
                            }

                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isLoading) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                    } else {
                        AsyncImage(
                            model = "https://q.qlogo.cn/headimg_dl?dst_uin=${rank2QQ}&spec=640&img_type=jpg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                        )
                    }
                    Image(
                        painterResource(R.drawable.silver),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = (-15).dp)
                    )
                    if (rank2Name == Global.username) {
                        Text(
                            text = "我",
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-15).dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Text(
                            text = rank2Name,
                            fontSize = 13.sp,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = animatedValue2.value.toInt().toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Text(
                            text = " 步",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-17).dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            if (rank1Name.trim().isNotEmpty() && rank1QQ.trim().isNotEmpty() &&
                                rank1Synopsis.trim().isNotEmpty() && rank1RegisterAddress.trim()
                                    .isNotEmpty() &&
                                rank1RegisterAddress != "无" && rank1LoginAddress.trim()
                                    .isNotEmpty() &&
                                rank1LastAccessTime.trim().isNotEmpty() && rank1StepCount != -1
                            ) {

                                Global.setPersonNameBeingViewed(rank1Name)
                                Global.setPersonSynopsisBeingViewed(rank1Synopsis)
                                Global.setPersonRegisterAddressBeingViewed(rank1RegisterAddress)
                                Global.setPersonLoginAddressBeingViewed(rank1LoginAddress)
                                Global.setPersonIsOnlineBeingViewed(rank1IsOnline)
                                Global.setPersonQQBeingViewed(rank1QQ)
                                Global.setPersonLastAccessTimeBeingViewed(rank1LastAccessTime)
                                Global.setPersonStepCountBeingViewed(rank1StepCount)
                                Global.setPersonLoginCountBeingViewed(rank1LoginCount)
                                Global.setIsShowState(true)
                            } else {
                                // 打印出所有相关的数据以便调试
                                Log.d("Rank1Data", "rank1Name: $rank1Name")
                                Log.d("Rank1Data", "rank1QQ: $rank1QQ")
                                Log.d("Rank1Data", "rank1Synopsis: $rank1Synopsis")
                                Log.d("Rank1Data", "rank1RegisterAddress: $rank1RegisterAddress")
                                Log.d("Rank1Data", "rank1LoginAddress: $rank1LoginAddress")
                                Log.d("Rank1Data", "rank1LastAccessTime: $rank1LastAccessTime")

                                Toast.makeText(
                                    context,
                                    "信息正在获取中，请重新点击！",
                                    Toast.LENGTH_LONG
                                ).show()
                                isRank1ReLoading = true
                            }

                            if (isFirstClickPerson) {
                                isFirstClickPerson = false
                            }

                        },
                    horizontalAlignment = Alignment.CenterHorizontally  // Column 中内容水平居中
                ) {
                    if (isLoading) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)
                        )
                    } else {
                        AsyncImage(
                            model = "https://q.qlogo.cn/headimg_dl?dst_uin=${rank1QQ}&spec=640&img_type=jpg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                        )
                    }
                    Image(
                        painterResource(R.drawable.gold),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .offset(y = (-20).dp)
                    )
                    if (rank1Name == Global.username) {
                        Text(
                            text = "我",
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-15).dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Text(
                            text = rank1Name,
                            fontSize = 13.sp,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = animatedValue.value.toInt().toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Text(
                            text = " 步",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-17).dp)
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = MutableInteractionSource()
                        ) {
                            if (rank3Name.trim().isNotEmpty() && rank3QQ.trim()
                                    .isNotEmpty() && rank3Synopsis.trim()
                                    .isNotEmpty() && rank3RegisterAddress.trim()
                                    .isNotEmpty() && rank3RegisterAddress != "无" && rank3LoginAddress.trim()
                                    .isNotEmpty() && rank3LastAccessTime.trim()
                                    .isNotEmpty() && rank3StepCount != -1
                            ) {
                                Global.setPersonNameBeingViewed(rank3Name)
                                Global.setPersonSynopsisBeingViewed(rank3Synopsis)
                                Global.setPersonRegisterAddressBeingViewed(rank3RegisterAddress)
                                Global.setPersonLoginAddressBeingViewed(rank3LoginAddress)
                                Global.setPersonIsOnlineBeingViewed(rank3IsOnline)
                                Global.setPersonQQBeingViewed(rank3QQ)
                                Global.setPersonLastAccessTimeBeingViewed(rank3LastAccessTime)
                                Global.setPersonStepCountBeingViewed(rank3StepCount)
                                Global.setPersonLoginCountBeingViewed(rank3LoginCount)
                                Global.setIsShowState(true)
                            } else {
                                Toast.makeText(
                                    context,
                                    "信息正在获取中，请重新点击！",
                                    Toast.LENGTH_LONG
                                ).show()
                                isRank3ReLoading = true
                            }
                            if (isFirstClickPerson) {
                                isFirstClickPerson = false
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally  // Column 中内容水平居中
                ) {
                    if (isLoading) {
                        Image(
                            painterResource(R.drawable.my),
                            contentDescription = null,
                            modifier = Modifier.size(45.dp)

                        )
                    } else {
                        AsyncImage(
                            model = "https://q.qlogo.cn/headimg_dl?dst_uin=${rank3QQ}&spec=640&img_type=jpg",
                            contentDescription = null,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape)
                        )
                    }
                    Image(
                        painterResource(R.drawable.copper),
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .offset(y = (-15).dp)
                    )
                    if (rank3Name == Global.username) {
                        Text(
                            text = "我",
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-15).dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = TextStyle(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Text(
                            text = rank3Name,
                            fontSize = 13.sp,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = animatedValue3.value.toInt().toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-15).dp)
                        )
                        Text(
                            text = " 步",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.offset(y = (-17).dp)
                        )
                    }
                }
            }
        }
    }
}
