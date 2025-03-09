package com.syc.world

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
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
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.TabRow
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.theme.MiuixTheme

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("IntentReset")
@Composable
fun Publist_Dynamic(navController: NavController, hazeStyle: HazeStyle, hazeState: HazeState) {
    val TopAppBarState = MiuixScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current

    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        selectedUris += uris
    }

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
        val element = remember { mutableStateOf(TextFieldValue(text = "")) }
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
                    AnimatedVisibility(selectedTab.intValue == 0) {
                        Column {
                            SmallTitle(selectedUris.toString())
                            TextField(
                                modifier = Modifier.padding(horizontal = 6.dp).padding(top = 6.dp),
                                value = element.value,
                                onValueChange = { element.value = it }
                            )
                            Card(modifier = Modifier.fillMaxWidth().padding(top = 6.dp).padding(horizontal = 6.dp)) {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    IconButton(
                                        onClick = {
                                            pickMediaLauncher.launch(
                                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            )
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
                            Markdown(element.value.text)
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
                                    content = element.value.text
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
            if (selectedUris.isNotEmpty()) {
                items(selectedUris, key = { it.toString() }) { uri ->
                    val uploadStatus = rememberSaveable { mutableStateOf("上传中") }
                    val uploadedImageUrl = rememberSaveable { mutableStateOf("") }
                    val process = rememberSaveable { mutableIntStateOf(0) }
                    uploadImage(
                        context = context,
                        fileUri = uri,
                        onResponse = { status, imageUrl ->
                            uploadStatus.value = status
                            uploadedImageUrl.value = imageUrl
                            if (status == "success") {
                                Log.d("上传问题", "图片 URL: $imageUrl")
                            } else {
                                Log.d("上传问题", "原因: $imageUrl")
                            }
                        },
                        onProgress = { progress ->
                            process.intValue = progress
                            Log.d("上传问题", "当前进度: $progress%")
                        },
                        username = Global.username,
                        password = Global.password
                    )
                    LaunchedEffect(uploadStatus.value) {

                    }
                    val clipboardManager = LocalClipboardManager.current

                    Card(modifier = Modifier.height(75.dp).fillMaxWidth().padding(vertical = 3.dp, horizontal = 6.dp), cornerRadius = 5.dp) {
                        Row(modifier = Modifier.combinedClickable(
                                onClick = {
                                    if (uploadStatus.value == "success") {
                                        element.value = insertAtCursor(
                                            element.value,
                                            "![Image](${uploadedImageUrl.value})"
                                        )
                                    }
                                },
                        onLongClick = {
                            if (uploadStatus.value == "success") {
                                // 复制链接到剪贴板
                                clipboardManager.setText(AnnotatedString(uploadedImageUrl.value))
                                // 显示反馈（可选）
                                Toast.makeText(
                                    context,
                                    "链接已复制",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            true // 返回 true 表示已消费长按事件
                        }
                        )) {
                            val painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data(uri)
                                    .size(Size.ORIGINAL)
                                    .build()
                            )

                            Image(
                                painter = painter,
                                contentDescription = "已选图片",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .background(Color.LightGray),
                                contentScale = ContentScale.Crop
                            )
                            Column {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(uploadStatus.value,modifier = Modifier.padding(start = 6.dp, top = 6.dp))
                                    Text(uploadedImageUrl.value,modifier = Modifier.padding(start = 6.dp, top = 6.dp))
                                }
                                LinearProgressIndicator(progress = process.intValue.toFloat() / 100, modifier = Modifier.fillMaxWidth())
                            }
                        }
                    }
                }
            }
        }
    }
}

fun insertAtCursor(currentValue: TextFieldValue, insertStr: String): TextFieldValue {
    val originalText = currentValue.text
    val selection = currentValue.selection

    return if (selection.collapsed) {
        // 光标模式（非选择文本）
        val newText = buildString {
            append(originalText.substring(0, selection.start))
            append(insertStr)
            append(originalText.substring(selection.end, originalText.length))
        }
        // 计算新光标位置
        val newCursorPos = selection.start + insertStr.length
        currentValue.copy(
            text = newText,
            selection = TextRange(newCursorPos)
        )
    } else {
        // 替换选中文本
        val newText = originalText.replaceRange(selection.start, selection.end, insertStr)
        val newSelection = TextRange(selection.start + insertStr.length)
        currentValue.copy(text = newText, selection = newSelection)
    }
}

@Composable
fun LinearProgressIndicator(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    colors: com.syc.world.ProgressIndicatorDefaults.ProgressIndicatorColors = com.syc.world.ProgressIndicatorDefaults.progressIndicatorColors(),
    height: Dp = com.syc.world.ProgressIndicatorDefaults.DefaultLinearProgressIndicatorHeight
) {
    if (progress == null) {
        val animatedValue = remember { Animatable(initialValue = 0f) }

        LaunchedEffect(Unit) {
            animatedValue.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 1250, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        }

        Canvas(modifier = modifier.fillMaxWidth().height(height)) {
            drawRoundRect(
                color = colors.backgroundColor(),
                size = androidx.compose.ui.geometry.Size(size.width, size.height),
                cornerRadius = CornerRadius(size.height / 2)
            )

            val value = animatedValue.value
            val segmentWidth = 0.45f
            val gap = 0.55f

            val positions = listOf(
                value,
                value - (segmentWidth + gap),
                value - 2 * (segmentWidth + gap)
            )

            positions.forEach { position ->
                val adjustedPos = (position % 1f + 1f) % 1f

                if (adjustedPos < 1f - segmentWidth) {
                    val startX = size.width * adjustedPos
                    val width = size.width * segmentWidth

                    drawRoundRect(
                        color = colors.foregroundColor(true),
                        topLeft = Offset(startX, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height),
                        cornerRadius = CornerRadius(size.height / 2)
                    )
                } else {
                    val startX = size.width * adjustedPos
                    val width = size.width * (1f - adjustedPos)

                    drawRoundRect(
                        color = colors.foregroundColor(true),
                        topLeft = Offset(startX, 0f),
                        size = androidx.compose.ui.geometry.Size(width, size.height),
                        cornerRadius = CornerRadius(size.height / 2)
                    )

                    val remainingWidth = adjustedPos + segmentWidth - 1f
                    if (remainingWidth > 0) {
                        drawRoundRect(
                            color = colors.foregroundColor(true),
                            topLeft = Offset(0f, 0f),
                            size = androidx.compose.ui.geometry.Size(size.width * remainingWidth, size.height),
                            cornerRadius = CornerRadius(size.height / 2)
                        )
                    }
                }
            }
        }
    } else {
        val progressValue = progress.coerceIn(0f, 1f)

        Canvas(modifier = modifier.fillMaxWidth().height(height)) {
            val cornerRadius = size.height / 2

            drawRoundRect(
                color = colors.backgroundColor(),
                size = androidx.compose.ui.geometry.Size(size.width, size.height),
                cornerRadius = CornerRadius(cornerRadius)
            )

            val minWidth = cornerRadius * 2
            val progressWidth = if (progressValue == 0f) {
                minWidth
            } else {
                minWidth + (size.width - minWidth) * progressValue
            }

            drawRoundRect(
                color = colors.foregroundColor(true),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(progressWidth, size.height),
                cornerRadius = CornerRadius(cornerRadius)
            )
        }
    }
}

object ProgressIndicatorDefaults {
    /** The default height of [LinearProgressIndicator]. */
    val DefaultLinearProgressIndicatorHeight = 6.dp

    /** The default stroke width of [CircularProgressIndicator]. */
    val DefaultCircularProgressIndicatorStrokeWidth = 4.dp

    /** The default size of [CircularProgressIndicator]. */
    val DefaultCircularProgressIndicatorSize = 30.dp

    /** The default stroke width of [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorStrokeWidth = 2.dp

    /** The default radius width of the orbiting dot in [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorOrbitingDotSize = 2.dp

    /** The default size of [InfiniteProgressIndicator]. */
    val DefaultInfiniteProgressIndicatorSize = 20.dp

    /**
     * The default [ProgressIndicatorColors] used by [LinearProgressIndicator] and [CircularProgressIndicator].
     */
    @Composable
    fun progressIndicatorColors(
        foregroundColor: Color = MiuixTheme.colorScheme.primary,
        disabledForegroundColor: Color = MiuixTheme.colorScheme.disabledPrimarySlider,
        backgroundColor: Color = MiuixTheme.colorScheme.tertiaryContainerVariant
    ): ProgressIndicatorColors {
        return ProgressIndicatorColors(
            foregroundColor = foregroundColor,
            disabledForegroundColor = disabledForegroundColor,
            backgroundColor = backgroundColor
        )
    }

    @Immutable
    class ProgressIndicatorColors(
        private val foregroundColor: Color,
        private val disabledForegroundColor: Color,
        private val backgroundColor: Color
    ) {
        @Stable
        internal fun foregroundColor(enabled: Boolean): Color =
            if (enabled) foregroundColor else disabledForegroundColor

        @Stable
        internal fun backgroundColor(): Color = backgroundColor
    }
}

@Composable
fun Markdown(content: String) {
    val highlightsBuilder =
        Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isSystemInDarkTheme()))
    Markdown(
        content.replace(Regex("(?<!\\n\\n)(!\\[(.*?)]\\((.*?)(?:\\s+[\"'](.*?)[\"'])?\\))"),"\n\n$1"),
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
