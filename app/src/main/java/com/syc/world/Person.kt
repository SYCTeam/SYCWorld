package com.syc.world

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
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
        val context = LocalContext.current
        val isShowEditPassword = Global.isShowEditPassword.collectAsState()
        val isShowEditQQ = Global.isShowEditQQ.collectAsState()
        val isShowAskExit = Global.isShowAskExit.collectAsState()
        var textOld by remember { mutableStateOf("") }
        var textNew by remember { mutableStateOf("") }
        var passwordHidden by remember { mutableStateOf(false) }

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

        AnimatedVisibility(
            visible = isShowEditPassword.value || isShowEditQQ.value || isShowAskExit.value,
            enter = fadeIn(
                animationSpec = tween(durationMillis = 200)
            ),
            exit = fadeOut(
                animationSpec = tween(durationMillis = 200)
            )
        ) {
            Surface(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        Global.setIsShowEditPassword(false)
                        Global.setIsShowEditQQ(false)
                        Global.setIsShowAskExit(false)
                    }
                    .fillMaxSize()
                    .alpha(0.5f),
                color = Color.Black
            ) {

            }
        }
        AnimatedVisibility(
            visible = isShowEditPassword.value,
            enter = scaleIn(
                initialScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
                )
            ),
            exit = scaleOut(
                targetScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
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

                        HeadlineInLargePrint(headline = "修改登录密码")

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = "请输入旧密码: ",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )



                                TextField(
                                    modifier = Modifier.padding(10.dp),
                                    value = textOld,
                                    onValueChange = {
                                        textOld = it
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                passwordHidden = !passwordHidden
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.visible),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text("密码")
                                    },
                                    visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None
                                )

                                Text(
                                    text = "请输入新密码: ",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                TextField(
                                    modifier = Modifier.padding(10.dp),
                                    value = textNew,
                                    onValueChange = {
                                        textNew = it
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                passwordHidden = !passwordHidden
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.visible),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text("密码")
                                    },
                                    visualTransformation = if (passwordHidden) PasswordVisualTransformation() else VisualTransformation.None
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Button(
                                modifier = Modifier.padding(10.dp),
                                onClick = {
                                    if (textOld.trim().isNotEmpty() && textNew.trim()
                                            .isNotEmpty()
                                    ) {
                                        Toast.makeText(
                                            context,
                                            "登录密码修改成功！",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        textOld = ""
                                        textNew = ""
                                        Global.setIsShowEditPassword(false)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "请确保每项不为空！",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("确认")
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isShowEditQQ.value,
            enter = scaleIn(
                initialScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
                )
            ),
            exit = scaleOut(
                targetScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
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

                        HeadlineInLargePrint(headline = "修改QQ号")

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = "请输入新QQ: ",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )

                                TextField(
                                    modifier = Modifier.padding(10.dp),
                                    value = textNew,
                                    onValueChange = {
                                        // 确保只更新数字
                                        if (it.all { char -> char.isDigit() }) {
                                            textNew = it
                                        }
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = {
                                                passwordHidden = !passwordHidden
                                            }
                                        ) {
                                            Icon(
                                                painter = painterResource(id = R.drawable.visible),
                                                contentDescription = null,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                    },
                                    label = {
                                        Text("密码")
                                    },
                                    visualTransformation = VisualTransformation.None,
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        keyboardType = KeyboardType.Number
                                    ),
                                    keyboardActions = KeyboardActions.Default
                                )

                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Button(
                                modifier = Modifier.padding(10.dp),
                                onClick = {
                                    if (textNew.trim().isNotEmpty()) {
                                        Toast.makeText(context, "QQ修改成功！", Toast.LENGTH_SHORT)
                                            .show()
                                        textOld = ""
                                        textNew = ""
                                        Global.setIsShowEditQQ(false)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "请确保QQ号不为空！",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                Icon(
                                    Icons.Filled.Done,
                                    contentDescription = null,
                                    modifier = Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                Text("确认")
                            }
                        }
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = isShowAskExit.value,
            enter = scaleIn(
                initialScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
                )
            ),
            exit = scaleOut(
                targetScale = 0f,
                animationSpec = tween(
                    durationMillis = 300
                )
            ) + slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300
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

                        HeadlineInLargePrint(headline = "退出登录")

                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier,
                                horizontalAlignment = Alignment.Start,
                                verticalArrangement = Arrangement.Top
                            ) {
                                Text(
                                    text = "您确定要退出登录吗？",
                                    modifier = Modifier
                                        .padding(10.dp),
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    modifier = Modifier.padding(10.dp),
                                    onClick = {
                                        Toast.makeText(context, "已退出登录！", Toast.LENGTH_SHORT)
                                            .show()
                                        textOld = ""
                                        textNew = ""
                                        Global.setIsShowAskExit(false)
                                    }) {
                                    Icon(
                                        Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("确认")
                                }
                                OutlinedButton(
                                    modifier = Modifier.padding(10.dp),
                                    onClick = {
                                        Global.setIsShowAskExit(false)
                                    }) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(ButtonDefaults.IconSize)
                                    )
                                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                                    Text("取消")
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
fun MyselfInformation() {
    var userName by remember { mutableStateOf("小夜") }
    var userQQ by remember { mutableStateOf("1640432") }
    var loginCounts by remember { mutableStateOf("12") }
    var registerTime by remember { mutableStateOf("1737276065842") }
    var registerAddress by remember { mutableStateOf("重庆") }

    var change by remember { mutableStateOf(false) }
    val buttonSize by animateDpAsState(
        targetValue = if (change) 30.dp else 25.dp,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        ),
        label = ""
    )

    if (buttonSize == 30.dp) {
        change = false
    }
    LaunchedEffect(Unit) {
        delay(200)
        change = true
    }

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
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {

                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.user_name),
                            contentDescription = "用户名为$userName。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "用户名: ",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 15.sp
                        )
                        Text(
                            text = userName,
                            modifier = Modifier,
                            fontSize = 15.sp,
                            style = TextStyle(fontStyle = FontStyle.Normal)
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {

                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.qq),
                            contentDescription = "QQ号为$userQQ。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
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
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {

                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.counts),
                            contentDescription = "登录次数为${loginCounts}次。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
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
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {

                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.time),
                            contentDescription = "此处为注册时间。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
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
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {

                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.address),
                            contentDescription = "注册地区为$registerAddress。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
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
                Spacer(modifier = Modifier.height(10.dp))
                HeadlineInLargePrint(headline = "安全设置")
                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                Global.setIsShowEditQQ(true)
                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.qq),
                            contentDescription = "修改QQ号。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "修改QQ号",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.enter),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 5.dp, end = 10.dp)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .height(50.dp)
                        .clip(CircleShape)
                ) {
                    Row(
                        modifier = Modifier
                            .clickable {
                                Global.setIsShowEditPassword(true)
                            }
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(buttonSize),
                            painter = painterResource(id = R.drawable.password),
                            contentDescription = "修改登录密码。"
                        )
                        Spacer(modifier = Modifier.width(20.dp))
                        Text(
                            text = "修改登录密码",
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Image(
                            painter = painterResource(id = R.drawable.enter),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .padding(top = 5.dp, end = 10.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxWidth(),
                        onClick = {
                            Global.setIsShowAskExit(true)
                        }) {
                        Text("退出登录")
                    }
                }
            }
        }
    }
}