package com.syc.world

import android.annotation.SuppressLint
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import com.syc.world.ForegroundService.ChatNewMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit
import java.util.Locale

data class SelectionGlobal(
    val isEnterToSendMessage: Boolean,
)

data class SelectionDetail(
    val isCloseMessageReminder: Boolean,
    val isPinChat: Boolean
)

fun calculateTimeDifference(lastTimestamp: String, currentTimestamp: String): Long {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    try {
        val lastDate = dateFormat.parse(lastTimestamp)
        val currentDate = dateFormat.parse(currentTimestamp)

        if (lastDate != null && currentDate != null) {
            val diffInMillis = currentDate.time - lastDate.time
            return diffInMillis / (1000 * 60)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return 0L
}

fun getCurrentTime(): String {
    val currentTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return currentTime.format(formatter)
}

fun getCurrentTimeForChatList(): String {
    val currentTime = LocalDateTime.now()
    val hour = currentTime.hour
    val minute = currentTime.minute

    // 判断时间段
    val timePeriod = when (hour) {
        in 0..5 -> "凌晨" // 0点到5点之间为凌晨
        in 6..11 -> "早上" // 6点到11点之间为早上
        in 12..17 -> "下午" // 12点到17点之间为下午
        else -> "晚上" // 18点到23点之间为晚上
    }

    return "$timePeriod$hour:${minute.toString().padStart(2, '0')}"
}

fun formatTime(inputTime: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    try {
        val inputDateTime = LocalDateTime.parse(inputTime, formatter)
        val currentDateTime = LocalDateTime.now()

        val daysBetween =
            ChronoUnit.DAYS.between(inputDateTime.toLocalDate(), currentDateTime.toLocalDate())
        val yearsBetween =
            ChronoUnit.YEARS.between(inputDateTime.toLocalDate(), currentDateTime.toLocalDate())

        val hour = inputDateTime.hour
        val minute = inputDateTime.minute
        val timePeriod = when (hour) {
            in 0..5 -> "凌晨"
            in 6..11 -> "早上"
            12 -> "中午"
            in 13..17 -> "下午"
            else -> "晚上" // 18..23
        }

        val formattedHour = if (hour == 12) 12 else hour % 12
        val formattedTime = "$timePeriod${formattedHour}:${minute.toString().padStart(2, '0')}"

        return when {
            daysBetween == 0L -> {
                // 今天
                formattedTime
            }

            daysBetween in 1L..6L -> {
                // 过去一周内
                val dayOfWeek = inputDateTime.dayOfWeek
                val weekDay = when (dayOfWeek) {
                    DayOfWeek.MONDAY -> "周一"
                    DayOfWeek.TUESDAY -> "周二"
                    DayOfWeek.WEDNESDAY -> "周三"
                    DayOfWeek.THURSDAY -> "周四"
                    DayOfWeek.FRIDAY -> "周五"
                    DayOfWeek.SATURDAY -> "周六"
                    DayOfWeek.SUNDAY -> "周日"
                    else -> "未知"
                }
                "$weekDay $formattedTime"
            }

            yearsBetween == 0L -> {
                // 过去一年内
                "${inputDateTime.monthValue}月${inputDateTime.dayOfMonth}日 $formattedTime"
            }

            else -> {
                // 一年以前
                "${inputDateTime.year}年${inputDateTime.monthValue}月${inputDateTime.dayOfMonth}日 $formattedTime"
            }
        }

    } catch (e: DateTimeParseException) {
        return inputTime
    }
}

fun readChatMessagesFromFile(context: Context, senderName: String): List<ChatNewMessage> {
    val fileName = "ChatMessage/NewMessage/$senderName.json"
    val existingData = readFromFile(context, fileName)
    return if (existingData.isNotEmpty()) {
        Gson().fromJson(existingData, Array<ChatNewMessage>::class.java).toList()
    } else {
        emptyList()
    }
}

fun getMessageFromFile(context: Context, senderName: String): List<ChatMessage> {
    val fileName = "ChatMessage/Message/$senderName"
    val existingData = readFromFile(context, fileName)

    if (existingData.isNotEmpty()) {
        val messageList = mutableListOf<ChatMessage>()

        // Improved regex to match ChatMessage strings more accurately
        val regex =
            """ChatMessage\(isFake=(\w+), isShowTime=(\w+), chatName=([\w\u4e00-\u9fa5]+), sender=(\w+), senderQQ=(\w+), message=([^)]+), sendTime=([\d\-:\s]+)\)""".toRegex()

        val matches = regex.findAll(existingData)

        matches.forEach { match ->
            try {
                val isFake = match.groupValues[1].toBoolean()
                val isShowTime = match.groupValues[2].toBoolean()
                val chatName = match.groupValues[3]
                val senderType = try {
                    SenderType.valueOf(match.groupValues[4])
                } catch (e: IllegalArgumentException) {
                    Log.e("getMessageFromFile", "Invalid SenderType: ${match.groupValues[4]}")
                    SenderType.Others
                }
                val senderQQ = match.groupValues[5]
                val message = match.groupValues[6]
                val sendTime = match.groupValues[7]

                val chatMessage =
                    ChatMessage(
                        isFake,
                        isShowTime,
                        chatName,
                        senderType,
                        senderQQ,
                        message,
                        sendTime
                    )
                messageList.add(chatMessage)
            } catch (e: Exception) {
                Log.e("getMessageFromFile", "Error parsing ChatMessage: ${match.value}", e)
            }
        }

        return messageList
    } else {
        return emptyList()
    }
}

fun getChatListFromFile(context: Context): Pair<String, List<User>> {
    val fileName = "/ChatList/chatList"  // 你保存数据的文件路径
    val existingData = readFromFile(context, fileName)

    if (existingData.isNotEmpty()) {
        try {
            val regex = """\((\w+), \[(.*)]\)""".toRegex()
            val matchResult = regex.find(existingData)

            if (matchResult != null) {
                val status = matchResult.groupValues[1] // "success"
                val usersData = matchResult.groupValues[2] // 用户数据部分

                val userRegex =
                    """User\(index=(\d+), username=([\w\u4e00-\u9fa5]+), qq=(\d+), online=(true|false), isPinned=(true|false)\)""".toRegex()
                val usersList = mutableListOf<User>()

                val userMatches = userRegex.findAll(usersData)

                userMatches.forEach { userMatch ->
                    val index = userMatch.groupValues[1].toInt()
                    val username = userMatch.groupValues[2]
                    val qq = userMatch.groupValues[3]
                    val online = userMatch.groupValues[4].toBoolean()
                    val isPinned = userMatch.groupValues[5].toBoolean()

                    val user = User(index, username, qq, online, isPinned)
                    usersList.add(user)
                }

                return Pair(status, usersList)
            } else {
                return Pair("error", emptyList()) // 错误时返回 Pair(error, 空列表)
            }
        } catch (e: Exception) {
            return Pair("error", emptyList()) // 错误时返回 Pair(error, 空列表)
        }
    } else {
        return Pair("error", emptyList()) // 没有数据时返回 Pair(error, 空列表)
    }
}

@Composable
fun Chat(
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    navController: NavController
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    val isUpdateChatList = Global.isUpdateChatList.collectAsState()
    var chatGroups by remember { mutableStateOf(listOf<ChatGroup>()) }
    val unreadCountInChat = Global.unreadCountInChat.collectAsState()
    var userQQ = ""
    var chatList: Pair<String, List<User>> = getChatListFromFile(context)

    LaunchedEffect(Unit) {
        while (true) {
            if (chatList.first == "success" && userQQ != "") {
                chatGroups = chatList.second.map { chatItem ->
                    val newMessage =
                        readChatMessagesFromFile(context, chatItem.username)
                    val messageList = getMessageFromFile(context, chatItem.username)

                    val latestMessage = newMessage.lastOrNull()?.content ?: ""

                    val latestMessageTime =
                        newMessage.lastOrNull()?.time ?: getCurrentTimeForChatList()

                    val newMessageCount = newMessage.lastOrNull()?.messageCount ?: 0

                    val isMe =
                        messageList.find { it.sender == SenderType.Me && it.senderQQ == Global.userQQ }

                    if (isMe != null) {
                        if (latestMessage != "" && latestMessage.trim().isNotEmpty()) {
                            Global.setUnreadCountInChat(unreadCountInChat.value + newMessageCount)
                            // 创建 ChatGroup 对象
                            ChatGroup(
                                chatItem.qq,
                                "联系人",
                                chatItem.username,
                                latestMessage,
                                latestMessageTime,
                                chatItem.isPinned,
                                chatItem.online,
                                newMessageCount
                            )
                        } else {
                            Log.d(
                                "列表问题",
                                messageList.lastOrNull()?.message ?: "6666"
                            )
                            // 创建 ChatGroup 对象
                            ChatGroup(
                                chatItem.qq,
                                "联系人",
                                chatItem.username,
                                messageList.lastOrNull()?.message ?: "",
                                formatTime(
                                    messageList.lastOrNull()?.sendTime
                                        ?: getCurrentTimeForChatList()
                                ),
                                chatItem.isPinned,
                                chatItem.online,
                                newMessageCount
                            )
                        }
                    } else {
                        ChatGroup(
                            chatItem.qq,
                            "联系人",
                            chatItem.username,
                            "",
                            "",
                            chatItem.isPinned,
                            chatItem.online,
                            0
                        )
                    }
                }
                Global.setIsUpdateChatList(false)
                isLoading = false
            }
            delay(500)
        }
    }

    LaunchedEffect(Unit) {
        Global.setChatIsChatMessageAnimation(false)
        Global.setIsSelectImageSuccessfully(false)
        withContext(Dispatchers.IO) {
            val readResult = getChatListFromFile(context)
            if (readResult.first == "success") {
                chatList = readResult
            }
        }
        withContext(Dispatchers.IO) {
            while (userQQ == "") {
                val informationResult = getUserInformation(Global.username)
                if (informationResult.isNotEmpty() && isJson(informationResult)) {
                    val userInfo = parseUserInfo(informationResult)
                    if (userInfo != null) {
                        if (userInfo.qq.isNotEmpty()) {
                            userQQ = userInfo.qq
                            Global.userQQ = userInfo.qq
                        }
                    }
                }
                delay(2000)
            }
        }
        withContext(Dispatchers.IO) {
            while (true) {
                if (isUpdateChatList.value || isLoading) {
                    if (userQQ != "") {
                        if (chatList.first == "error") {
                            chatList =
                                getChatList(Global.username, Global.password)
                        } else if (chatList.first == "success") {
                            chatGroups = chatList.second.map { chatItem ->
                                val newMessage =
                                    readChatMessagesFromFile(context, chatItem.username)
                                val messageList = getMessageFromFile(context, chatItem.username)

                                val latestMessage = newMessage.lastOrNull()?.content ?: ""

                                val latestMessageTime =
                                    newMessage.lastOrNull()?.time ?: getCurrentTimeForChatList()

                                val newMessageCount = newMessage.lastOrNull()?.messageCount ?: 0

                                val isMe =
                                    messageList.find { it.sender == SenderType.Me && it.senderQQ == Global.userQQ }

                                if (isMe != null) {
                                    if (latestMessage != "" && latestMessage.trim().isNotEmpty()) {
                                        Global.setUnreadCountInChat(unreadCountInChat.value + newMessageCount)
                                        // 创建 ChatGroup 对象
                                        ChatGroup(
                                            chatItem.qq,
                                            "联系人",
                                            chatItem.username,
                                            latestMessage,
                                            latestMessageTime,
                                            chatItem.isPinned,
                                            chatItem.online,
                                            newMessageCount
                                        )
                                    } else {
                                        Log.d(
                                            "列表问题",
                                            messageList.lastOrNull()?.message ?: "6666"
                                        )
                                        // 创建 ChatGroup 对象
                                        ChatGroup(
                                            chatItem.qq,
                                            "联系人",
                                            chatItem.username,
                                            messageList.lastOrNull()?.message ?: "",
                                            formatTime(
                                                messageList.lastOrNull()?.sendTime
                                                    ?: getCurrentTimeForChatList()
                                            ),
                                            chatItem.isPinned,
                                            chatItem.online,
                                            newMessageCount
                                        )
                                    }
                                } else {
                                    ChatGroup(
                                        chatItem.qq,
                                        "联系人",
                                        chatItem.username,
                                        "",
                                        "",
                                        chatItem.isPinned,
                                        chatItem.online,
                                        0
                                    )
                                }
                            }
                            Global.setIsUpdateChatList(false)
                            isLoading = false
                        }
                    }
                }
                delay(2000)
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            chatList = getChatList(Global.username, Global.password)
            if (chatList.first == "success") {
                writeToFile(
                    context,
                    "/ChatList",
                    "chatList",
                    chatList.toString()
                )
                chatGroups = chatList.second.map { chatItem ->
                    val newMessage =
                        readChatMessagesFromFile(context, chatItem.username)
                    val messageList = getMessageFromFile(context, chatItem.username)

                    val latestMessage = newMessage.lastOrNull()?.content ?: ""

                    val latestMessageTime =
                        newMessage.lastOrNull()?.time ?: getCurrentTimeForChatList()

                    val newMessageCount = newMessage.lastOrNull()?.messageCount ?: 0

                    val isMe =
                        messageList.find { it.sender == SenderType.Me && it.senderQQ == Global.userQQ }

                    if (isMe != null) {
                        if (latestMessage != "" && latestMessage.trim().isNotEmpty()) {
                            Global.setUnreadCountInChat(unreadCountInChat.value + newMessageCount)
                            // 创建 ChatGroup 对象
                            ChatGroup(
                                chatItem.qq,
                                "联系人",
                                chatItem.username,
                                latestMessage,
                                latestMessageTime,
                                chatItem.isPinned,
                                chatItem.online,
                                newMessageCount
                            )
                        } else {
                            // 创建 ChatGroup 对象
                            ChatGroup(
                                chatItem.qq,
                                "联系人",
                                chatItem.username,
                                messageList.lastOrNull()?.message ?: "",
                                formatTime(
                                    messageList.lastOrNull()?.sendTime
                                        ?: getCurrentTimeForChatList()
                                ),
                                chatItem.isPinned,
                                chatItem.online,
                                newMessageCount
                            )
                        }
                    } else {
                        ChatGroup(
                            chatItem.qq,
                            "联系人",
                            chatItem.username,
                            "",
                            "",
                            chatItem.isPinned,
                            chatItem.online,
                            0
                        )
                    }
                }
                Global.setIsUpdateChatList(false)
                isLoading = false
            }
        }
    }

    Scaffold {
        if (isUpdateChatList.value || isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = padding.calculateTopPadding()),
                topAppBarScrollBehavior = topAppBarScrollBehavior, modifier = Modifier.fillMaxSize()
            ) {
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth(),
                        thickness = 0.2.dp,
                        color = Color.Gray
                    )
                }
                items(chatGroups) { group ->
                    Log.d("聊天列表问题", "group.isPin: ${group.isPinned}")
                    ChatGroupItem(navController, group)
                }

            }
        }
    }
}

@Composable
fun ImageUploadScreen() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var uploadStatus by remember { mutableStateOf("未上传图片") }
    var uploadedImageUrl by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val isOpenMoreFunction = Global.isOpenMoreFunction.collectAsState()
    var isRun by remember { mutableStateOf(false) }
    var isRunSelectImage by remember { mutableStateOf(false) }

    LocalContext.current
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri == null) {
            Global.setIsOpenMoreFunction(false)
            Global.setIsSelectImageSuccessfully(false)
        } else {
            selectedImageUri = uri
        }
    }

    BackHandler {
        Global.setIsOpenMoreFunction(false)
        Global.setIsSelectImageSuccessfully(false)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color =
        if (isDarkMode) Color(0xFF252525) else Color(0xFFe6e6e6)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp)
        ) {
            if (selectedImageUri != null) {
                if (!isRun) {
                    isRun = true
                    Global.setIsSelectImageSuccessfully(true)
                }
                val image: Painter = rememberAsyncImagePainter(selectedImageUri)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = image,
                            contentDescription = "您已选择图片。",
                            modifier = Modifier
                                .size(300.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                        Button(onClick = {
                            selectedImageUri?.let { uri ->
                                uploadImage(
                                    context = context,
                                    fileUri = uri,
                                    onResponse = { status, imageUrl ->
                                        uploadStatus = status
                                        uploadedImageUrl = imageUrl
                                        if (status == "success") {
                                            Log.d("上传问题", "图片 URL: $imageUrl")
                                        } else {
                                            Log.d("上传问题", "原因: $imageUrl")
                                        }
                                    },
                                    onProgress = { progress ->
                                        Log.d("上传问题", "当前进度: $progress%")
                                    },
                                    username = Global.username,
                                    password = Global.password
                                )
                            } ?: run {
                                Log.d("上传问题", "选中的图片为空")
                            }

                        }) {
                            Text("上传图片")
                        }
                    }

                }
            }

            LaunchedEffect(isOpenMoreFunction.value) {
                if (isOpenMoreFunction.value) {
                    delay(300)
                    activityResultLauncher.launch("image/*")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uploadStatus == "success" && !isRunSelectImage) {
                isRunSelectImage = true
                Global.setIsOpenMoreFunction(false)
                Global.setIsSelectImageSuccessfully(false)
                copyToClipboard(context, uploadedImageUrl)
            }
        }
    }
}

@SuppressLint("ServiceCast")
fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = android.content.ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)

    Toast.makeText(context, "复制成功", Toast.LENGTH_SHORT).show()
}

// 群组数据类
data class ChatGroup(
    val groupQQ: String,
    val groupName: String,
    val chatName: String,
    val content: String,
    val time: String,
    val isPinned: Boolean,
    val isOnline: Boolean,
    val isUnread: Int
)

// 群组数据类
data class ChatMessage(
    val isFake: Boolean,
    val isShowTime: Boolean,
    val chatName: String,
    val sender: SenderType,
    val senderQQ: String,
    val message: String,
    val sendTime: String
)

// 群组类型
enum class SenderType {
    Me, // 自己
    Others  // 他人
}

// 群组项展示
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatGroupItem(navController: NavController, group: ChatGroup) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val unreadCountInChat = Global.unreadCountInChat.collectAsState()
    var isNavigate by remember { mutableStateOf(false) }

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
                .height(70.dp),
            color = when {
                group.isPinned -> if (isDarkMode) Color(0xFF252525) else Color(0xFFe6e6e6)
                isDarkMode -> Color(0xFF1E1B1B)
                else -> Color.Transparent
            }
        ) {
            Row(
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = MutableInteractionSource()
                    ) {
                        if (!isNavigate) {
                            val newMessage = readChatMessagesFromFile(context, group.chatName)

                            val newMessageCount = newMessage.lastOrNull()?.messageCount ?: 0

                            if (newMessage.isNotEmpty()) {
                                if (unreadCountInChat.value.toIntOrNull() != null) {
                                    if (unreadCountInChat.value.toInt() - newMessageCount >= 0) {
                                        Global.setUnreadCountInChat({ unreadCountInChat.value.toInt() - newMessageCount }.toString())
                                    }
                                }
                            }
                            deleteFile(context, "ChatMessage/NewMessage/${group.chatName}.json")
                            Global.setPersonNameBeingChat(group.chatName)
                            Global.setPersonQQBeingChat(group.groupQQ)
                            Global.setPersonIsOnlineBeingChat(group.isOnline)
                            navController.navigate("ChatUi")
                            isNavigate = true
                        }
                    }
                    .padding(start = 15.dp)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = "https://q.qlogo.cn/headimg_dl?dst_uin=${group.groupQQ}&spec=640&img_type=jpg",
                        contentDescription = null,
                        modifier =
                        if (group.isUnread > 0) {
                            Modifier
                                .size(imageSize)
                                .clip(RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                        }
                    )

                    if (group.isUnread > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(top = 5.dp)
                                .offset(x = 20.dp),
                            contentAlignment = Alignment.TopEnd
                        ) {
                            Surface(
                                modifier = Modifier
                                    .width(20.dp)
                                    .height(20.dp)
                                    .clip(CircleShape),
                                color = Color.Red
                            ) {
                                Text(
                                    text = group.isUnread.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.fillMaxSize(),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(200.dp)
                        .padding(
                            start = 8.dp, top = 5.dp
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(bottom = 10.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = group.chatName,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .padding(bottom = 5.dp),
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = group.content,
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
                        text = group.time,
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
            thickness = 0.2.dp,
            color = Color.Gray
        )
    }

}

fun isUrl(text: String): Boolean {
    return android.util.Patterns.WEB_URL.matcher(text).matches()
}

fun openUrl(context: Context, url: String) {
    val validUrl = if (isUrl(url)) url else "http://$url" // 如果不是 URL，加上 http://
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(validUrl))
    context.startActivity(intent)
}

// 单条消息样式
@Composable
fun ChatMessage(message: ChatMessage) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val chatIsChatMessageAnimation = Global.chatIsChatMessageAnimation.collectAsState()

    LaunchedEffect(Unit) {
        delay(500)
        Global.setChatIsChatMessageAnimation(true)
    }

    if (personNameBeingChat.value == message.chatName) {
        Column(
            modifier = Modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (message.isShowTime) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp, bottom = 25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = formatTime(message.sendTime),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Gray
                    )
                }
            }
            if (message.sender == SenderType.Others) {
                AnimatedVisibility(
                    visible = chatIsChatMessageAnimation.value,
                    enter = fadeIn(tween(durationMillis = 500)) + slideInHorizontally(
                        initialOffsetX = { +300 },
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = fadeOut(tween(durationMillis = 300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 5.dp, end = 65.dp, bottom = 15.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${message.senderQQ}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(start = 10.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .fillMaxHeight(),
                                color = if (isDarkMode) Color(0xFF313131) else Color.White
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isUrl(message.message)) {

                                        val annotatedMessage = buildAnnotatedString {
                                            var currentIndex = 0

                                            // 使用正则表达式查找所有 URL
                                            val urlRegex = """https?://\S+""".toRegex()

                                            // 在消息中查找所有 URL
                                            urlRegex.findAll(message.message)
                                                .forEach { matchResult ->
                                                    // 将 URL 之前的文本添加为普通文本
                                                    append(
                                                        message.message.substring(
                                                            currentIndex,
                                                            matchResult.range.first
                                                        )
                                                    )

                                                    // 给 URL 添加注解和样式
                                                    pushStringAnnotation(
                                                        tag = "URL",
                                                        annotation = matchResult.value
                                                    )
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = Color.Blue,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    ) {
                                                        append(matchResult.value)
                                                    }
                                                    pop()

                                                    // 更新当前索引为 URL 后的位置
                                                    currentIndex = matchResult.range.last + 1
                                                }

                                            // 添加 URL 后的剩余文本
                                            append(message.message.substring(currentIndex))
                                        }

                                        Text(
                                            text = annotatedMessage,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Color.Blue,
                                                textDecoration = TextDecoration.Underline
                                            ),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxHeight()
                                                .animateContentSize(
                                                    animationSpec = tween(
                                                        durationMillis = 300,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                )
                                                .clickable {
                                                    openUrl(context, message.message)
                                                }
                                        )
                                    } else {
                                        Text(
                                            text = message.message,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxHeight()
                                                .animateContentSize(
                                                    animationSpec = tween(
                                                        durationMillis = 300,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (message.sender == SenderType.Me) {
                AnimatedVisibility(
                    visible = chatIsChatMessageAnimation.value,
                    enter = fadeIn(tween(durationMillis = 500)) + slideInHorizontally(
                        initialOffsetX = { -300 },
                        animationSpec = tween(durationMillis = 500)
                    ),
                    exit = fadeOut(tween(durationMillis = 300))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 65.dp, end = 5.dp, bottom = 15.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            contentAlignment = Alignment.CenterEnd // 确保 Surface 从左开始扩展
                        ) {
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .fillMaxHeight(),
                                color = if (isDarkMode) Color(0xFF3EB174) else Color(0xFF95EC69)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight(),
                                    contentAlignment = Alignment.Center // 文本对齐保持从左开始
                                ) {
                                    if (isUrl(message.message)) {
                                        val annotatedMessage = buildAnnotatedString {
                                            var currentIndex = 0

                                            // 使用正则表达式查找所有 URL
                                            val urlRegex = """https?://\S+""".toRegex()

                                            // 在消息中查找所有 URL
                                            urlRegex.findAll(message.message)
                                                .forEach { matchResult ->
                                                    // 将 URL 之前的文本添加为普通文本
                                                    append(
                                                        message.message.substring(
                                                            currentIndex,
                                                            matchResult.range.first
                                                        )
                                                    )

                                                    // 给 URL 添加注解和样式
                                                    pushStringAnnotation(
                                                        tag = "URL",
                                                        annotation = matchResult.value
                                                    )
                                                    withStyle(
                                                        style = SpanStyle(
                                                            color = Color.Blue,
                                                            textDecoration = TextDecoration.Underline
                                                        )
                                                    ) {
                                                        append(matchResult.value)
                                                    }
                                                    pop()

                                                    // 更新当前索引为 URL 后的位置
                                                    currentIndex = matchResult.range.last + 1
                                                }

                                            // 添加 URL 后的剩余文本
                                            append(message.message.substring(currentIndex))
                                        }

                                        Text(
                                            text = annotatedMessage,
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Color.Blue,
                                                textDecoration = TextDecoration.Underline
                                            ),
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxHeight()
                                                .animateContentSize(
                                                    animationSpec = tween(
                                                        durationMillis = 300,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                )
                                                .clickable {
                                                    openUrl(context, message.message)
                                                }
                                        )
                                    } else {
                                        Text(
                                            text = message.message,
                                            style = MaterialTheme.typography.bodyLarge,
                                            textAlign = TextAlign.Start,
                                            modifier = Modifier
                                                .padding(10.dp)
                                                .fillMaxHeight()
                                                .animateContentSize(
                                                    animationSpec = tween(
                                                        durationMillis = 300,
                                                        easing = FastOutSlowInEasing
                                                    )
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        // Spacer 用于固定 Surface 和图片之间的间距
                        Spacer(modifier = Modifier.width(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(end = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${message.senderQQ}&spec=640&img_type=jpg",
                                contentDescription = null,
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop // 保持图片比例
                            )
                        }
                    }

                }
            }
        }
    }
}


// 聊天界面
@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatUi(navController: NavController) {
    val context = LocalContext.current
    var text by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var driveText by remember { mutableStateOf(false) }
    var isSendButtonVisible by remember { mutableStateOf(false) }
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val personIsOnlineBeingChat = Global.personIsOnlineBeingChat.collectAsState()
    val unreadCountInChat = Global.unreadCountInChat.collectAsState()
    val chatSelection1 = Global.chatSelection1.collectAsState()
    var textFieldChange by remember { mutableStateOf(false) }
    var buttonChange by remember { mutableStateOf(false) }
    var isSend by remember { mutableStateOf(false) }
    var isFocusTextField by remember { mutableStateOf(false) }

    val isOpenMoreFunction = Global.isOpenMoreFunction.collectAsState()
    val isSelectImageSuccessfully = Global.isSelectImageSuccessfully.collectAsState()
    var isShowMoreFunctionAnimation by remember { mutableStateOf(false) }

    val chatMessage = remember { mutableStateListOf<ChatMessage>() }

    val listState = rememberLazyListState()

    var isShowTime by remember { mutableStateOf(true) }

    var myMessage = ChatMessage(
        true,
        isShowTime,
        personNameBeingChat.value,
        SenderType.Me,
        Global.userQQ,
        text,
        getCurrentTime()
    )

    LaunchedEffect(isOpenMoreFunction.value) {
        if (isOpenMoreFunction.value) {
            delay(100)
            isShowMoreFunctionAnimation = true
        } else {
            delay(100)
            isShowMoreFunctionAnimation = false
        }
    }

    LaunchedEffect(chatMessage.size, isLoading, text) {
        if (chatMessage.isNotEmpty() && !isLoading || (chatMessage.isNotEmpty() && isFocusTextField)
        ) {
            listState.scrollToItem(chatMessage.size - 1, Int.MAX_VALUE)
        }
    }

    LaunchedEffect(Unit, chatMessage.size, isLoading, isSend) {
        if (readFromFile(
                context,
                "isInForeground"
            ) == "true"
        ) {
            if (chatMessage.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    writeToFile(
                        context,
                        "/ChatMessage/Count",
                        personNameBeingChat.value,
                        chatMessage.size.toString()
                    )
                    Log.d("写入问题", "已经写入count: ${chatMessage.size}")
                    writeToFile(
                        context,
                        "/ChatMessage/Message",
                        personNameBeingChat.value,
                        chatMessage.toString()
                    )
                }
            }
        }
        val newMessage = readChatMessagesFromFile(
            context,
            personNameBeingChat.value
        )

        val newMessageCount = newMessage.lastOrNull()?.messageCount ?: 0

        if (newMessage.isNotEmpty()) {
            if (Global.unreadCountInChat.value.toIntOrNull() != null) {
                if (Global.unreadCountInChat.value.toInt() - newMessageCount >= 0) {
                    Global.setUnreadCountInChat({ Global.unreadCountInChat.value.toInt() - newMessageCount }.toString())
                }
            }
        }
        deleteFile(context, "ChatMessage/NewMessage/${personNameBeingChat.value}.json")
        Global.setIsUpdateChatList(true)
    }

    LaunchedEffect(Unit) {
        Global.setIsOpenMoreFunction(false)
        val messageList = getMessageFromFile(context, personNameBeingChat.value)
        val isMe = messageList.find { it.sender == SenderType.Me && it.senderQQ == Global.userQQ }
        if (messageList.isNotEmpty() && isMe != null) {
            chatMessage.addAll(messageList)
        }

        while (true) {
            withContext(Dispatchers.IO) {
                isLoading = true
                if (Global.userQQ.trim().isNotEmpty()) {
                    val getMessageResult =
                        getMessage(Global.username, Global.password, personNameBeingChat.value)

                    if (getMessageResult.first == "error" && getMessageResult.second.isNotEmpty()) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                context,
                                "聊天记录获取失败！原因：${getMessageResult.second}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        val existingMessages = mutableListOf<Pair<String, String>>()

                        chatMessage.forEach { existingMessages.add(it.message to it.sendTime) }

                        var lastMessageTimestamp = ""

                        getMessageResult.second.forEachIndexed { index, chatRecord ->
                            val senderQQ = chatRecord.senderQQ
                            val message = chatRecord.message
                            val timestamp = chatRecord.timestamp
                            val senderType = if (chatRecord.senderUsername != Global.username) {
                                SenderType.Others
                            } else {
                                SenderType.Me
                            }

                            // 判断当前消息和前一条消息的时间差
                            val isFirstMessage = index == 0 || calculateTimeDifference(
                                lastMessageTimestamp,
                                timestamp
                            ) > 10

                            // 如果消息不存在于已存在的列表中，则创建新消息
                            if (!existingMessages.contains(message to timestamp)) {
                                val newMessage = ChatMessage(
                                    false,
                                    isFirstMessage,
                                    personNameBeingChat.value,
                                    senderType,
                                    senderQQ,
                                    message,
                                    timestamp
                                )

                                withContext(Dispatchers.Main) {
                                    // 添加新消息到 chatMessage
                                    chatMessage.add(newMessage)
                                    if (chatMessage.isNotEmpty()) {
                                        withContext(Dispatchers.IO) {
                                            writeToFile(
                                                context,
                                                "/ChatMessage/Count",
                                                personNameBeingChat.value,
                                                chatMessage.size.toString()
                                            )
                                            writeToFile(
                                                context,
                                                "/ChatMessage/Message",
                                                personNameBeingChat.value,
                                                chatMessage.toString()
                                            )
                                        }
                                    }
                                    existingMessages.add(message to timestamp)
                                    lastMessageTimestamp = timestamp
                                    isLoading = false
                                }
                            }
                        }
                    }
                }
            }
            delay(3000)
        }
    }
    var isSendSuccessfully by remember { mutableStateOf(true) }

// 发送协程
    LaunchedEffect(isSend) {
        withContext(Dispatchers.IO) {
            val messageIndex = chatMessage.size
            if (isSend && isSendSuccessfully && Global.userQQ.trim().isNotEmpty() && text.trim()
                    .isNotEmpty()
            ) {

                if (chatMessage.isNotEmpty()) {
                    // 计算时间差，确保时间格式化无误
                    val lastMessageTime = LocalDateTime.parse(
                        chatMessage.last().sendTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )
                    val currentMessageTime = LocalDateTime.parse(
                        myMessage.sendTime,
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                    )

                    // 比较时间差，单位为分钟
                    val timeDifference =
                        ChronoUnit.MINUTES.between(lastMessageTime, currentMessageTime)

                    isShowTime =
                        timeDifference > 10
                }

                myMessage = ChatMessage(
                    true,
                    isShowTime,
                    personNameBeingChat.value,
                    SenderType.Me,
                    Global.userQQ,
                    text,
                    getCurrentTime()
                )

                chatMessage.add(myMessage)


                val sendResult =
                    sendMessage(Global.username, Global.password, personNameBeingChat.value, text)

                text = ""

                isSendSuccessfully = false

                if (sendResult.first == "error") {
                    chatMessage.removeAll {
                        it.isFake
                    }
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "发送失败！原因：${sendResult.second}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else if (sendResult.first == "success") {
                    if (Global.userQQ.trim().isNotEmpty()) {
                        val getMessageResult =
                            getMessage(Global.username, Global.password, personNameBeingChat.value)

                        if (getMessageResult.first == "error" && (getMessageResult.second as? List<*>).isNullOrEmpty()) {
                            chatMessage.removeAt(messageIndex)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "聊天记录获取失败！原因：${getMessageResult.second}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } else {
                            val existingMessages = mutableListOf<Pair<String, String>>()
                            chatMessage.removeAll {
                                it.isFake
                            }
                            chatMessage.filter {
                                !it.isFake
                            }.forEach { existingMessages.add(it.message to it.sendTime) }
                            getMessageResult.second.forEach { chatRecord ->
                                val senderQQ = chatRecord.senderQQ
                                val message = chatRecord.message
                                val timestamp = chatRecord.timestamp
                                val senderType = if (chatRecord.senderUsername != Global.username) {
                                    SenderType.Others
                                } else {
                                    SenderType.Me
                                }
                                if (!existingMessages.contains(message to timestamp)) {
                                    val newMessage = ChatMessage(
                                        false,
                                        isShowTime,
                                        personNameBeingChat.value,
                                        senderType,
                                        senderQQ,
                                        message,
                                        timestamp
                                    )

                                    chatMessage.add(newMessage)
                                    existingMessages.add(message to timestamp)
                                }
                            }
                        }
                    }
                    chatMessage.removeAll {
                        it.isFake
                    }
                }
                chatMessage.removeAll {
                    it.isFake
                }
                isSend = false
                isSendSuccessfully = true
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val selectionJsonDetail =
                readFromFile(context, "ChatSettings/${personNameBeingChat.value}/chatSettings.json")
            val selectionJsonGlobal = readFromFile(context, "ChatSettings/chatGlobalSettings.json")
            if (selectionJsonDetail.isNotEmpty() && isJson(selectionJsonDetail)) {
                val gsonDetailResult =
                    Gson().fromJson(selectionJsonDetail, SelectionDetail::class.java)
                Global.setChatSelection2(gsonDetailResult.isCloseMessageReminder)
                Global.setChatSelection3(gsonDetailResult.isPinChat)
                if (gsonDetailResult.isPinChat) {
                    pinUser(Global.username, Global.password, personNameBeingChat.value)
                } else {
                    pinUser(Global.username, Global.password, personNameBeingChat.value, "true")
                }
            } else if (selectionJsonDetail.isEmpty() || !isJson(selectionJsonDetail)) {
                Global.setChatSelection2(false)
                Global.setChatSelection3(false)
            }

            if (selectionJsonGlobal.isNotEmpty() && isJson(selectionJsonGlobal)) {
                val gsonGlobalResult =
                    Gson().fromJson(selectionJsonGlobal, SelectionGlobal::class.java)
                Global.setChatSelection1(gsonGlobalResult.isEnterToSendMessage)
            } else if (selectionJsonGlobal.isEmpty() || !isJson(selectionJsonGlobal)) {
                Global.setChatSelection1(false)
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(200)
        textFieldChange = true
    }

    LaunchedEffect(text, isSelectImageSuccessfully.value) {
        if (chatMessage.isNotEmpty()) {
            listState.scrollToItem(chatMessage.size - 1, Int.MAX_VALUE)
        }
        if (text.trim().isNotEmpty() || isSelectImageSuccessfully.value) {
            driveText = true
            delay(200)
            buttonChange = true
            delay(300)
            isSendButtonVisible = true
        } else {
            delay(200)
            isSendButtonVisible = false
            buttonChange = false
            delay(400)
            driveText = false
        }
    }

    var isBack by remember { mutableStateOf(true) }

    LaunchedEffect(isBack) {
        if (!isBack) {
            navController.popBackStack()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(x = 10.dp)
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                if (isBack) {
                                    isBack = false
                                }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                            )

                            if (unreadCountInChat.value.toIntOrNull() != null) {
                                if (unreadCountInChat.value.toInt() > 0) {
                                    Surface(
                                        modifier = Modifier
                                            .width(25.dp)
                                            .height(25.dp)
                                            .clip(CircleShape),
                                        color = if (isDarkMode) Color(0xFF242424) else Color.LightGray
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = unreadCountInChat.value,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(),
                                                textAlign = TextAlign.Center,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Box(
                    modifier = when {
                        unreadCountInChat.value.toIntOrNull() != null && unreadCountInChat.value.toInt() > 0 -> {
                            Modifier
                                .fillMaxHeight()
                                .width(200.dp)
                                .offset(x = -(15.dp))
                        }

                        else -> {
                            Modifier
                                .fillMaxHeight()
                                .width(200.dp)
                                .offset(x = -(3.dp))
                        }
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = personNameBeingChat.value,
                            fontSize = 18.sp,
                            modifier = Modifier,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (personIsOnlineBeingChat.value) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(bottom = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(8.dp),
                                    painter = painterResource(id = R.drawable.point_green),
                                    contentDescription = null
                                )
                                Text(
                                    text = "在线",
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .padding(start = 5.dp),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(bottom = 5.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    modifier = Modifier
                                        .size(8.dp),
                                    painter = painterResource(id = R.drawable.point_gray),
                                    contentDescription = null
                                )
                                Text(
                                    text = "离线",
                                    fontSize = 10.sp,
                                    modifier = Modifier
                                        .padding(start = 5.dp),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }

                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .offset(x = -(20).dp)
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                navController.navigate("ChatSettings")
                            },
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.more),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(20.dp),
                            tint = if (isDarkMode) Color.White else Color.Black
                        )
                    }
                }
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth(),
                thickness = 0.2.dp,
                color = Color.Gray
            )

            LaunchedEffect(chatMessage.size) {
                if (chatMessage.isNotEmpty()) {
                    delay(1500)
                    listState.scrollToItem(chatMessage.size - 1, Int.MAX_VALUE)
                }
            }

            if (isSelectImageSuccessfully.value) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 490.dp)
                        .imePadding(),
                    state = listState
                ) {
                    itemsIndexed(chatMessage) { _, message ->
                        if (message.message.trim().isNotEmpty()) {
                            ChatMessage(message = message)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp)
                        .imePadding(),
                    state = listState
                ) {
                    itemsIndexed(chatMessage) { _, message ->
                        if (message.message.trim().isNotEmpty()) {
                            ChatMessage(message = message)
                        }
                    }
                }
            }
        }
    }


    var lineCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(text) {
        withContext(Dispatchers.IO) {
            while (true) {
                val newLineCount = text.split("\n").size

                lineCount = newLineCount.coerceAtMost(4)

                if (text.length > lineCount * 10) {
                    lineCount = (text.length / 10) + 1
                }

                lineCount = lineCount.coerceAtMost(4)

                delay(500)
            }
        }
    }


    val textFieldHeight by animateDpAsState(
        targetValue = if (lineCount == 1) 50.dp else (50.dp + (lineCount - 1) * 25.dp),
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .height(textFieldHeight + 40.dp)
                    .fillMaxWidth(),
                color = if (isDarkMode) Color(0xFF252525) else Color(0xFFeeeeee)
            ) {

                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {

                    val maxWidth =
                        if (text.trim()
                                .isNotEmpty() || isSelectImageSuccessfully.value
                        ) maxWidth * 0.5f else maxWidth * 0.75f

                    val textFieldWidth by animateDpAsState(
                        targetValue = if (textFieldChange) maxWidth else 0.dp,
                        animationSpec = tween(
                            durationMillis = 1000,
                            easing = FastOutSlowInEasing
                        )
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, end = 10.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.text_input),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                                .weight(0.1f),
                            tint = if (isDarkMode) Color.White else Color.Black
                        )

                        if (chatSelection1.value) {
                            TextField(
                                modifier = Modifier
                                    .width(textFieldWidth)
                                    .height(textFieldHeight)
                                    .padding(start = 10.dp, end = 10.dp)
                                    .onFocusChanged { focusState: FocusState ->
                                        isFocusTextField = focusState.isFocused
                                    },
                                value = text,
                                onValueChange = { newText -> text = newText },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDarkMode) Color(0xFF2d2d2d) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color(0xFF2d2d2d) else Color.White,
                                    cursorColor = Color(0xFF95EC69),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF95EC69),
                                    unfocusedIndicatorColor = Color.White
                                ),
                                keyboardActions = KeyboardActions(
                                    onSend = {
                                        if (text.trim().length <= 1000 && text.trim()
                                                .isNotEmpty() && isSendSuccessfully
                                        ) {
                                            isSend = true
                                        } else if (text.length > 1000) {
                                            Toast.makeText(
                                                context,
                                                "字数达到上限，无法发送！",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(
                                    imeAction = ImeAction.Send
                                )
                            )
                        } else {
                            TextField(
                                modifier = Modifier
                                    .width(textFieldWidth)
                                    .height(textFieldHeight)
                                    .padding(start = 10.dp, end = 10.dp)
                                    .onFocusChanged { focusState: FocusState ->
                                        isFocusTextField = focusState.isFocused
                                    },
                                value = text,
                                onValueChange = { newText ->
                                    text = newText
                                },
                                textStyle = TextStyle(
                                    fontSize = 15.sp,
                                    lineHeight = 22.sp,
                                    color = if (isDarkMode) Color.White else Color.Black
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = if (isDarkMode) Color((0xFF2d2d2d)) else Color.White,
                                    unfocusedContainerColor = if (isDarkMode) Color((0xFF2d2d2d)) else Color.White,
                                    cursorColor = Color(0xFF95EC69),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    focusedIndicatorColor = Color(0xFF95EC69),
                                    unfocusedIndicatorColor = Color.White
                                )
                            )
                        }


                        Surface(
                            modifier = Modifier
                                .weight(0.1f)
                                .clip(CircleShape),
                            color = Color.Transparent
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.emotion),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clickable(
                                        indication = null,
                                        interactionSource = MutableInteractionSource()
                                    ) {
                                        Global.setIsOpenMoreFunction(!isOpenMoreFunction.value)
                                        Global.setIsSelectImageSuccessfully(false)
                                    },
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                        if (driveText) {

                            val buttonSize by animateDpAsState(
                                targetValue = if (buttonChange) 58.dp else 0.dp,
                                animationSpec = tween(
                                    durationMillis = 300,
                                )
                            )

                            Card(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .height(34.dp)
                                    .width(buttonSize)
                                    .clickable {
                                        if (text.trim().length <= 1000 && text.trim()
                                                .isNotEmpty() && isSendSuccessfully
                                        ) {
                                            isSend = true
                                        } else if (text.length > 1000) {
                                            Toast.makeText(
                                                context,
                                                "字数达到上限，无法发送！",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color(0xFF07C160),
                                    contentColor = Color.White
                                ),
                                shape = RectangleShape
                            ) {
                                AnimatedVisibility(
                                    visible = isSendButtonVisible,
                                    enter = fadeIn(
                                        animationSpec = tween(durationMillis = 300)
                                    ),
                                    exit = fadeOut(
                                        animationSpec = tween(durationMillis = 300)
                                    )
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "发送",
                                            modifier = Modifier,
                                            fontSize = 15.sp,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

            }
            AnimatedVisibility(
                visible = isShowMoreFunctionAnimation,
                enter = expandVertically(
                    // 从上往下展开
                    expandFrom = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                ),
                exit = fadeOut(
                    animationSpec = tween(durationMillis = 300)
                ) + shrinkVertically(
                    // 控件消失时向上收缩
                    shrinkTowards = Alignment.Top,
                    animationSpec = tween(durationMillis = 300)
                )
            ) {
                Box(
                    modifier = Modifier
                        .height(400.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    ImageUploadScreen()
                }
            }
        }
    }
}


@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ChatSettings(navController: NavController) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val personNameBeingChat = Global.personNameBeingChat.collectAsState()
    val personQQBeingChat = Global.personQQBeingChat.collectAsState()


    val chatSelection1 = Global.chatSelection1.collectAsState()
    val chatSelection2 = Global.chatSelection2.collectAsState()
    val chatSelection3 = Global.chatSelection3.collectAsState()

    LaunchedEffect(chatSelection1.value, chatSelection2.value, chatSelection3.value) {
        withContext(Dispatchers.IO) {
            val selectionJsonDetail = Gson().toJson(
                SelectionDetail(
                    chatSelection2.value,
                    chatSelection3.value
                )
            )
            val selectionJsonGlobal = Gson().toJson(
                SelectionGlobal(
                    chatSelection1.value
                )
            )
            if (isJson(selectionJsonDetail)) {
                writeToFile(
                    context,
                    "ChatSettings/${personNameBeingChat.value}",
                    "chatSettings.json",
                    selectionJsonDetail
                )
            }

            if (isJson(selectionJsonGlobal)) {
                writeToFile(
                    context,
                    "ChatSettings",
                    "chatGlobalSettings.json",
                    selectionJsonGlobal
                )
            }

        }
    }

    LaunchedEffect(chatSelection3.value) {
        withContext(Dispatchers.IO) {
            if (chatSelection3.value) {
                val pinUserResult =
                    pinUser(Global.username, Global.password, personNameBeingChat.value)
                if (pinUserResult.first == "error" && pinUserResult.second != "该用户已经被置顶") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "置顶失败！原因：${pinUserResult.second}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                val pinUserResult =
                    pinUser(Global.username, Global.password, personNameBeingChat.value, "true")
                if (pinUserResult.first == "error" && pinUserResult.second != "该用户并没有被置顶") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            context,
                            "取消置顶失败！原因：${pinUserResult.second}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    var isBack by remember { mutableStateOf(true) }

    LaunchedEffect(isBack) {
        if (!isBack) {
            navController.popBackStack()
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .padding(
                        top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
                    )
                    .height(50.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(CircleShape),
                    color = Color.Transparent
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable(
                                indication = null,
                                interactionSource = MutableInteractionSource()
                            ) {
                                if (isBack) {
                                    isBack = false
                                }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .fillMaxHeight(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp),
                                tint = if (isDarkMode) Color.White else Color.Black
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .offset(x = -(22).dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "聊天信息",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = 10.dp, end = 20.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Avatar(personQQBeingChat.value, personNameBeingChat.value)
                        }
                        Box(
                            modifier = Modifier
                                .offset(y = -(11.dp)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.add),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(55.dp),
                                tint = Color.LightGray
                            )
                        }
                    }
                }

                item {
                    Selection(
                        1,
                        painterResource(R.drawable.message_tip),
                        "回车键发送消息",
                        true
                    )
                    Selection(
                        2,
                        painterResource(R.drawable.without_disturb),
                        "消息免打扰",
                        true
                    )
                    Selection(3, painterResource(R.drawable.top), "置顶聊天", false)
                    Spacer(modifier = Modifier.height(20.dp))
                    SelectionWithoutButton(
                        { println("用户点击了投诉") },
                        painterResource(R.drawable.complaints),
                        "投诉"
                    )
                }
            }
        }
    }
}

@Composable
fun Selection(ordinal: Int, painter: Painter, description: String, isDivider: Boolean) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    val chatSelection1 = Global.chatSelection1.collectAsState()
    val chatSelection2 = Global.chatSelection2.collectAsState()
    val chatSelection3 = Global.chatSelection3.collectAsState()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, end = 1.dp)
            .height(55.dp),
        color = if (isDarkMode) Color(0xFF1c1c1c) else Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 15.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(ButtonDefaults.IconSize)
                        .padding(top = 4.dp),
                    tint = Color.LightGray
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 4.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Switch(
                        checked = when (ordinal) {
                            1 -> chatSelection1.value
                            2 -> chatSelection2.value
                            3 -> chatSelection3.value
                            else -> false
                        },
                        onCheckedChange = {
                            when (ordinal) {
                                1 -> {
                                    Global.setChatSelection1(!chatSelection1.value)
                                }

                                2 -> {
                                    Global.setChatSelection2(!chatSelection2.value)
                                }

                                3 -> {
                                    Global.setChatSelection3(!chatSelection3.value)
                                }
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF07C160),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.LightGray
                        )
                    )
                }
            }
            if (isDivider) {
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 4.dp),
                    thickness = 0.2.dp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun SelectionWithoutButton(operation: () -> Unit, painter: Painter, description: String) {
    val context = LocalContext.current
    val isDarkMode =
        context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 1.dp, end = 1.dp, bottom = 10.dp)
            .height(55.dp),
        color = if (isDarkMode) Color(0xFF1c1c1c) else Color.White
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    operation()
                }
                .fillMaxSize()
                .padding(start = 15.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(ButtonDefaults.IconSize),
                tint = Color.LightGray
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(start = 10.dp)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    painter = painterResource(R.drawable.enter),
                    contentDescription = null,
                    modifier = Modifier
                        .size(15.dp),
                    tint = if (isDarkMode) Color.White else Color.Black
                )
            }
        }
    }
}

@Composable
fun Avatar(qq: String, name: String) {
    Column(
        modifier = Modifier
            .width(70.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (qq.trim().isNotEmpty()) {
            AsyncImage(
                model = "https://q.qlogo.cn/headimg_dl?dst_uin=${qq}&spec=640&img_type=jpg",
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        } else {
            Image(
                painterResource(R.drawable.my),
                contentDescription = null,
                modifier = Modifier
                    .size(55.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(top = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray
        )
    }
}
