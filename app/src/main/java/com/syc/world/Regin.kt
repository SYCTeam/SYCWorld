package com.syc.world

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.BasicComponentColors
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.system.exitProcess

@Composable
fun Regin(hazeStyle: HazeStyle, hazeState: HazeState, navController: NavController) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    Scaffold(topBar = {
        TopAppBar(
            title = "注册/登录",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = TopAppBarState,
            navigationIcon = {
                IconButton(
                    onClick = {
                        exitProcess(0)
                    },
                    modifier = Modifier.padding(start = 18.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.ArrowBack,
                        contentDescription = null,
                        tint = MiuixTheme.colorScheme.onBackground
                    )
                }
            }
        )
    }) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = padding.calculateTopPadding()),
            topAppBarScrollBehavior = TopAppBarState, modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            item {
                val islogin by remember { mutableStateOf(false) }
                val name = remember { mutableStateOf("") }
                val qq = remember { mutableLongStateOf(10001L) }
                val password = remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp), // 填充边距
                    verticalArrangement = Arrangement.Center, // 垂直居中
                    horizontalAlignment = Alignment.CenterHorizontally // 水平居中
                ) {
                    AsyncImage(
                        model = "http://q.qlogo.cn/headimg_dl?dst_uin=${qq}&spec=640&img_type=jpg",
                        contentDescription = null,
                    )
                    // 用户名输入框
                    TextField(
                        value = name.value,
                        label = "用户名",
                        onValueChange = { name.value = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()  // 宽度填充父布局
                            .padding(bottom = 10.dp) // 每个输入框之间的间距
                    )

                    // 动态显示的QQ输入框
                    AnimatedVisibility(visible = !islogin) {
                        TextField(
                            value = if (qq.longValue == 10001L) "" else qq.longValue.toString(),
                            label = "QQ",
                            onValueChange = { if (it.toLongOrNull() != null) qq.longValue = it.toLong() },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()  // 宽度填充父布局
                                .padding(bottom = 10.dp) // 每个输入框之间的间距
                        )
                    }

                    // 密码输入框
                    TextField(
                        value = password.value,
                        label = "密码",
                        onValueChange = { password.value = it },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()  // 宽度填充父布局
                    )
                }
            }
        }
    }
}
