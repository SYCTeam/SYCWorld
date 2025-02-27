package com.syc.world

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.size.Size
import com.mikepenz.markdown.coil3.Coil3ImageTransformerImpl
import com.mikepenz.markdown.compose.Markdown
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.compose.extendedspans.ExtendedSpans
import com.mikepenz.markdown.compose.extendedspans.RoundedCornerSpanPainter
import com.mikepenz.markdown.compose.extendedspans.SquigglyUnderlineSpanPainter
import com.mikepenz.markdown.compose.extendedspans.rememberSquigglyUnderlineAnimator
import com.mikepenz.markdown.model.markdownExtendedSpans
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@SuppressLint("IntentReset")
@Composable
fun Publist_Dynamic(navController: NavController, hazeStyle: HazeStyle, hazeState: HazeState) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    Scaffold(topBar = {
        TopAppBar(
            title = "发布动态",
            color = Color.Transparent,
            modifier = Modifier.hazeEffect(
                state = hazeState,
                style = hazeStyle
            ),
            scrollBehavior = TopAppBarState,
            navigationIcon = {
                IconButton(
                    onClick = {
                        navController.popBackStack()
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
            topAppBarScrollBehavior = TopAppBarState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
        ) {
            item {
                Column(modifier = Modifier.fillMaxSize().imePadding()) {
                    val selectedTab = remember { mutableIntStateOf(0) }
                    TabRow(
                        tabs = listOf("编写","预览"),
                        selectedTabIndex = selectedTab.intValue,
                        onSelect = {
                            selectedTab.intValue = it
                        }
                    )
                    val element = remember { mutableStateOf("") }
                    AnimatedVisibility(selectedTab.intValue == 0) {
                        Column {
                            TextField(
                                modifier = Modifier.padding(horizontal = 6.dp).padding(top = 6.dp),
                                value = element.value,
                                onValueChange = { element.value = it }
                            )
                            var imageUri by remember { mutableStateOf<Uri?>(null) }
                            val launcher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.GetContent()
                            ) { uri: Uri? ->
                                uri?.let { imageUri = it }
                            }
                            imageUri?.let { uri ->
                                val painter = rememberAsyncImagePainter(
                                    model = ImageRequest.Builder(context)
                                        .data(uri)
                                        .size(Size.ORIGINAL) // 保持原始尺寸
                                        .build()
                                )

                                Image(
                                    painter = painter,
                                    contentDescription = "Preview",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Card(modifier = Modifier.fillMaxWidth().padding(top = 6.dp).padding(horizontal = 6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    IconButton(
                                        onClick = {
                                            launcher.launch("image/*")
                                        },
                                        modifier = Modifier,
                                    ) {
                                        Image(
                                            painter = painterResource(id = R.drawable.pic),
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(20.dp),
                                            colorFilter = ColorFilter.tint(MiuixTheme.colorScheme.onBackground)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    AnimatedVisibility(selectedTab.intValue == 1) {
                        Column(modifier = Modifier.padding(6.dp)) {
                            Markdown(element.value)
                        }
                    }
                    val send = remember { mutableStateOf(false) }
                    val context = LocalContext.current
                    LaunchedEffect(send.value) {
                        if (send.value == true) {
                            withContext(Dispatchers.IO) {
                                val post = postMoment(
                                    username = Global.username,
                                    password = Global.password,
                                    content = element.value
                                )
                                println(post)
                                if (post.first == "success") {
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "您的动态已发出！",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        navController.popBackStack()
                                    }
                                } else {
                                    Log.d("发动态问题", post.second)
                                    withContext(Dispatchers.Main) {
                                        Toast.makeText(
                                            context,
                                            "发送失败，原因：${post.second}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                            send.value = false
                        }
                    }
                    TextButton(
                        text = "发布动态",
                        onClick = {
                            send.value = true
                        },
                        modifier = Modifier.padding(6.dp).fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                    )
                }
            }
        }
    }
}

@Composable
fun Markdown(content: String) {
    val highlightsBuilder =
        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
    Markdown(
        content.replace(Regex("(?<!\\n\\n)(!\\[Image]\\(.*?\\))"),"\n\n$1"),
        colors = markdownColor(),
        extendedSpans = markdownExtendedSpans {
            val animator = rememberSquigglyUnderlineAnimator()
            remember {
                ExtendedSpans(
                    RoundedCornerSpanPainter(),
                    SquigglyUnderlineSpanPainter(animator = animator)
                )
            }
        },
        components = markdownComponents(
            codeBlock = {
                MarkdownHighlightedCodeBlock(
                    it.content,
                    it.node,
                    highlightsBuilder
                )
            },
            codeFence = {
                MarkdownHighlightedCodeFence(
                    it.content,
                    it.node,
                    highlightsBuilder
                )
            },
        ),
        imageTransformer = Coil3ImageTransformerImpl,
        typography = markdownTypography1()
    )
}
