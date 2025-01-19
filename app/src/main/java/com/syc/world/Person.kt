package com.syc.world

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior

@Composable
fun Person(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    colorMode: MutableState<Int>,
    navController: NavController
) {
    Scaffold() {
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
        ) {
            item {
                MyselfInformation()
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
fun MyselfInformation() {
    var userName by remember { mutableStateOf("小夜") }
    var userQQ by remember { mutableStateOf("1640432") }
    var loginCounts by remember { mutableStateOf("12") }
    var registerTime by remember { mutableStateOf("1737276065842") }
    var registerAddress by remember { mutableStateOf("重庆") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Image(
            painterResource(R.drawable.my),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
        )
        Box(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                HeadlineInLargePrint(headline = "个人资料")
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "用户名: ",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = userName,
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QQ号: ",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = userQQ,
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "登录次数: ",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = loginCounts,
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "注册时间: ",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = registerTime.toLongOrNull()?.let { transToString(it) } ?: "无",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "注册地区: ",
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = registerAddress,
                        modifier = Modifier,
                        fontSize = 15.sp,
                        style = TextStyle(fontStyle = FontStyle.Normal)
                    )
                }
            }
        }
    }
}