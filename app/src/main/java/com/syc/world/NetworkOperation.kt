package com.syc.world

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import kotlin.coroutines.cancellation.CancellationException

fun isJson(jsonString: String): Boolean {
    return try {
        Gson().fromJson(jsonString, Any::class.java)
        true
    } catch (e: JsonSyntaxException) {
        false
    }
}

fun parseUserInfo(json: String): UserInfo? {
    if (json.isBlank()) {
        Log.d("解析问题", "JSON input cannot be empty or blank.")
        throw IllegalArgumentException("JSON input cannot be empty or blank.")
    }

    val gson = Gson()
    return try {
        gson.fromJson(json, UserInfo::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun parseSynopsisData(json: String): SynopsisData? {
    val gson = Gson()
    return try {
        gson.fromJson(json, SynopsisData::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

data class UserInfo(
    @SerializedName("status") val status: String,
    @SerializedName("username") val username: String,
    @SerializedName("registerIp") val registerIp: String,
    @SerializedName("registerTime") val registerTime: Long,
    @SerializedName("lastAccessTime") val lastAccessTime: Long,
    @SerializedName("loginCount") val loginCount: String,
    @SerializedName("loginIp") val loginIp: String,
    @SerializedName("online") val online: String,
    @SerializedName("qq") val qq: String,
    @SerializedName("synopsis") val synopsis: String,
    @SerializedName("stepCount") val stepCount: String
)

data class IpResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("data")
    val data: IpInfo
)

data class PostResponse(
    @SerializedName("status") val status: String,
    @SerializedName("posts") val posts: List<Post>
)

data class Post(
    @SerializedName("username") val username: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("ip") val ip: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("commentsCount") val commentsCount: Int,
    @SerializedName("shares") val shares: Int,
    @SerializedName("views") val views: Int,
    @SerializedName("postId") val postId: Int,
    @SerializedName("qq") val qq: Long,
    @SerializedName("isLiked") val islike: Boolean,
    @SerializedName("online") val online: Boolean,
    @SerializedName("comments") val comments: List<Comments>
)

data class Comments(
    @SerializedName("id") val id: Int,
    @SerializedName("parentCommentId") val parentCommentId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("content") val content: String,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("qq") val qq: Long,
    @SerializedName("online") val online: Boolean
)

data class WebCommonInfo(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String
)

data class actionInfo(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("postId")
    val postId: Int,
    @SerializedName("newLikesCount")
    val newLikesCount: Int
)

data class sendCommentInfo(
    @SerializedName("status")
    val status: String,
    @SerializedName("message")
    val message: String,
    @SerializedName("commentId")
    val commentId: Int
)

data class IpInfo(
    // 接口中的 "QUERY_IP" 对应用户的IP地址
    @SerializedName("QUERY_IP")
    val ip: String,
    // 省份中文
    @SerializedName("PROVINCE_CN")
    val province: String,
    // 省份代码
    @SerializedName("PROVINCE_CODE")
    val provinceCode: String,
    // 城市中文
    @SerializedName("CITY_CN")
    val city: String,
    // 城市代码
    @SerializedName("CITY_CODE")
    val cityCode: String,
    // 区域中文（例如 "AREA_CN"）
    @SerializedName("AREA_CN")
    val region: String,
    // 区域代码（例如 "AREA_CODE"）
    @SerializedName("AREA_CODE")
    val regionCode: String,
    // 这里简单使用国家中文作为地址（你也可以根据需要组合多个字段）
    @SerializedName("COUNTRY_CN")
    val addr: String,
    // 区域英文名称（例如 "AREA_EN"）
    @SerializedName("AREA_EN")
    val regionNames: String,
    // 如果有错误信息，可用此字段，这里没有返回值，默认为空字符串
    val err: String = ""
)


data class RankInfo(
    @SerializedName("username") val username: String,
    @SerializedName("qq") val qq: String,
    @SerializedName("stepCount") val stepCount: Int,
    @SerializedName("rank") val rank: Int
)

data class SynopsisData(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class ChatResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("chatList")
    val chatList: List<User>
)

data class User(
    @SerializedName("index")
    val index: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("qq")
    val qq: String,

    @SerializedName("online")
    val online: Boolean,

    @SerializedName("isPinned")
    val isPinned: Boolean
)

data class ChatRecord(
    @SerializedName("sender_username")
    val senderUsername: String,  // 发送者的用户名

    @SerializedName("receiver_username")
    val receiverUsername: String, // 接收者的用户名

    @SerializedName("sender_qq")
    val senderQQ: String, // 发送者的QQ

    @SerializedName("receiver_qq")
    val receiverQQ: String, // 接收者的QQ

    @SerializedName("message")
    val message: String,  // 消息内容

    @SerializedName("timestamp")
    val timestamp: String  // 时间戳，保持为字符串类型
)

data class ChatMessageResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("chatRecords")
    val chatRecords: List<ChatRecord>
)

suspend fun getUrl(): String {
    val url = "https://sharechain.qq.com/93bd306d9c78bc6c4bc469c43c086cb6"

    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: "【URL】http://xyc.okc.today【URL】"
                val pattern = Pattern.compile("【URL】(.*?)【URL】", Pattern.DOTALL)
                val matcher = pattern.matcher(responseBody)

                if (matcher.find()) {
                    matcher.group(1) ?: "http://xyc.okc.today"
                } else {
                    "http://xyc.okc.today"
                }
            } else {
                "http://xyc.okc.today"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "http://xyc.okc.today"
        } catch (e: CancellationException) {
            "http://xyc.okc.today"
        }
    }
}

fun login(username: String, password: String): String {
    val url = "${Global.url}/syc/login.php".toHttpUrlOrNull() ?: return "Error: Invalid URL"

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("信息获取", responseBody)
            responseBody
        } else {
            "Error: ${response.message}"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error: ${e.message}"
    }
}


fun checkUserOnline(username: String): String {
    val url = "${Global.url}/syc/keepAlive.php"

    val client = OkHttpClient()

    val formBody = FormBody.Builder()
        .add("username", username)
        .build()

    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("在线状态", responseBody)
            responseBody
        } else {
            "Error"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error"
    }
}

fun getUserInformation(username: String): String {

    val url = "${Global.url}/syc/check.php".toHttpUrlOrNull()?.newBuilder()
        ?.addQueryParameter("username", username)
        ?.build()

    if (url == null) {
        return "Error"
    }

    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .get()
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("用户信息获取", responseBody)
            responseBody
        } else {
            "Error"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error"
    }
}

fun getAddressFromIp(ip: String): String {

    val url = "https://zj.v.api.aa1.cn/api/ip-taobao/?ip=$ip"


    val client = OkHttpClient()

    val request = Request.Builder()
        .url(url)
        .build()

    try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            Log.d("IP问题", "访问成功")
            val responseString = response.body?.string() ?: "无"
            try {
                val ipResponse: IpResponse =
                    Gson().fromJson(responseString, IpResponse::class.java)
                return ipResponse.data.addr + "·" + ipResponse.data.province
            } catch (e: Exception) {
                e.printStackTrace()
                return "无"
            }
        } else {
            return "无"
        }
    } catch (e: TimeoutCancellationException) {
        return "无"
    } catch (e: IOException) {
        e.printStackTrace()
        return "无"
    }
}

fun modifySynopsis(username: String, password: String, synopsis: String): String {
    val url = "${Global.url}/syc/synopsis.php".toHttpUrlOrNull() ?: return "Error: Invalid URL"

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("synopsis", synopsis)
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("信息获取", responseBody)
            responseBody
        } else {
            "Error: ${response.message}"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error: ${e.message}"
    }
}

fun modifyStepCount(username: String, password: String, stepCount: String): String {
    val url = "${Global.url}/syc/stepCount.php".toHttpUrlOrNull() ?: return "Error: Invalid URL"

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("stepCount", stepCount)
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("步数信息获取", responseBody)
            responseBody
        } else {
            "Error: ${response.message}"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error: ${e.message}"
    }
}

suspend fun getRank(): List<RankInfo> {
    val url = "${Global.url}/syc/rank.php"

    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(url)
            .build()

        try {
            val response = withTimeout(4000) {
                client.newCall(request).execute()
            }

            if (response.isSuccessful) {
                val responseString = response.body?.string() ?: "[]"
                try {
                    Log.d("信息获取", responseString)
                    val rankInfoList: List<RankInfo> =
                        Gson().fromJson(responseString, Array<RankInfo>::class.java).toList()

                    rankInfoList.take(3)
                } catch (e: Exception) {
                    e.printStackTrace()
                    emptyList()
                }
            } else {
                emptyList()
            }
        } catch (e: TimeoutCancellationException) {
            emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }
}

fun modifyPassword(
    username: String,
    oldPassword: String,
    newPassword: String
): Pair<String, String> {
    val url = "${Global.url}/syc/modifyPassword.php".toHttpUrlOrNull() ?: return Pair(
        "Error",
        "Invalid URL"
    )

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("oldPassword", oldPassword)
        .add("newPassword", newPassword)
        .build()

    Log.d("密码信息获取", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("密码信息获取", responseBody)
            try {
                val passwordInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                Pair(passwordInfo.status, passwordInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun modifyQQ(username: String, newQQ: String): Pair<String, String> {
    val url =
        "${Global.url}/syc/modifyQQ.php".toHttpUrlOrNull() ?: return Pair("Error", "Invalid URL")

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("newQQ", newQQ)
        .build()

    Log.d("QQ信息获取", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("QQ信息获取", responseBody)
            try {
                val passwordInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                Pair(passwordInfo.status, passwordInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun mail(qq: String, username: String): Pair<String, String> {

    // 构建GET请求的URL
    val url = ("http://api.mmp.cc/api/mail?" +
            "email=3760453912@qq.com" +  // 发信人邮箱
            "&key=raxdahlhoccucfhc" +  // 邮箱授权码（可以填默认值或者通过其他方式获取）
            "&mail=${qq}@qq.com" +  // 收件人邮箱
            "&title=${username}邀请你上线酸夜沉空间" +  // 邮件标题
            "&name=${username}" +  // 发信人昵称
            "&text=火速前往酸夜沉空间吧！")

    // 增加超时时间设置
    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 设置模拟浏览器请求头
    val request = Request.Builder()
        .url(url)
        .get()  // 使用GET方法
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("邮箱信息获取", responseBody)
            try {
                val passwordInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                // 返回状态和信息
                Pair(passwordInfo.status, passwordInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                // 如果解析失败，返回"error"状态和信息
                Pair("error", "Failed to parse response")
            }
        } else {
            // 如果请求失败，返回"error"状态和信息
            Pair("error", "Request failed")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        // 捕获异常并返回"error"状态和信息
        Pair("error", "IOException occurred")
    }
}

fun postMoment(username: String, password: String, content: String): Pair<String, String> {
    val url =
        "${Global.url}/syc/post.php".toHttpUrlOrNull() ?: return Pair("Error", "Invalid URL")

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("content",content)
        .build()

    Log.d("发布动态", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            try {
                val passwordInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                Pair(passwordInfo.status, passwordInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun getPost(sort: String = "random",postId: String = "0",username: String,password: String): Pair<String, List<Post>> {
    val url =
        "${Global.url}/syc/checkPost.php".toHttpUrlOrNull() ?: return Pair("Error", emptyList())

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("orderBy", sort)
        .add("postId",postId)
        .add("username",username)
        .add("password",password)
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("帖子问题", responseBody)
            try {
                val postResponse: PostResponse = Gson().fromJson(responseBody, PostResponse::class.java)
                // Assuming PostResponse has a field `posts` that holds a list of Post objects
                Log.d("帖子问题", postResponse.posts.toString())
                Pair(postResponse.status, postResponse.posts)  // Return the list of posts
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", emptyList())  // Return an empty list in case of parsing error
            }
        } else {
            Pair("error", emptyList())  // Return an empty list in case of unsuccessful response
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", emptyList())  // Return an empty list in case of IOException
    }
}

fun addlike(username: String, password: String, postId: String): Pair<String, String> {
    val url =
        "${Global.url}/syc/postAction.php".toHttpUrlOrNull() ?: return Pair("Error", "Invalid URL")

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password",password)
        .add("postId",postId)
        .add("action","like")
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            try {
                val actionInfo: actionInfo =
                    Gson().fromJson(responseBody, actionInfo::class.java)
                Pair(actionInfo.status, if (actionInfo.status != "success") actionInfo.message else actionInfo.newLikesCount.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun cancellike(username: String, password: String, postId: String): Pair<String, String> {
    val url =
        "${Global.url}/syc/postAction.php".toHttpUrlOrNull() ?: return Pair("Error", "Invalid URL")

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password",password)
        .add("postId",postId)
        .add("action","like")
        .add("isCancel","true")
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            try {
                val actionInfo: actionInfo =
                    Gson().fromJson(responseBody, actionInfo::class.java)
                Pair(actionInfo.status, if (actionInfo.status != "success") actionInfo.message else actionInfo.newLikesCount.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

@SuppressLint("SuspiciousIndentation")
fun getIpaddress(context: Context,ip: String): Pair<String, String> {
    // 如果 IP 是特定的，直接返回指定的地址
    if (ip == "221.193.168.119") {
        return Pair("success", "北京朝阳和平里东街道")
    } else if (ip == "39.144.225.117") {
        return Pair("success", "缅甸佤邦")
    }

    // 检查本地文件是否已经保存了查询结果
    val cacheFile = File(context.cacheDir, "$ip.json")
    if (cacheFile.exists()) {
        // 读取文件内容并返回
        try {
            val fileContent = cacheFile.readText()
            val data = JSONObject(fileContent).getString("location")
            return Pair("success", data)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair("error", "Error reading cached file")
        }
    }

    // 如果没有缓存，进行 API 查询
    val url = "https://api.ip77.net/ip2/v4/".toHttpUrlOrNull() ?: return Pair("error", "Invalid URL")
    val client = OkHttpClient()

    val formBody = FormBody.Builder()
        .add("ip", ip)
        .build()

    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            try {
                val data = JSONObject(responseBody).getJSONObject("data")
                val location = data.getString("location").replace("中国", "")
                // 保存查询结果到文件
                val jsonToSave = JSONObject().apply {
                    put("location", location)
                }
                cacheFile.writeText(jsonToSave.toString())
                Pair("success", location)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun pinUser(
    username: String,
    password: String,
    pinUser: String,
    isCancel: String = "false"
): Pair<String, String> {
    val url = "${Global.url}/syc/pinUser.php".toHttpUrlOrNull() ?: return Pair(
        "Error",
        "Invalid URL"
    )

    Log.d("置顶问题", "isCancel: $isCancel")

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBodyBuilder =
        FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("pinUser", pinUser)
        .add("isCancel", isCancel)

    val formBody = formBodyBuilder.build()

    Log.d("置顶用户信息获取", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("置顶用户信息获取", responseBody)
            try {
                val pinUserInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                Pair(pinUserInfo.status, pinUserInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun getChatList(
    username: String,
    password: String
): Pair<String, List<User>> {
    val url = "${Global.url}/syc/getChatList.php".toHttpUrlOrNull() ?: return Pair(
        "Error",
        emptyList()
    )

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBodyBuilder =
        FormBody.Builder()
            .add("username", username)
            .add("password", password)

    val formBody = formBodyBuilder.build()

    Log.d("聊天列表信息获取", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("聊天列表信息获取", responseBody)
            try {
                val chatListInfo: ChatResponse =
                    Gson().fromJson(responseBody, ChatResponse::class.java)
                Pair(chatListInfo.status, chatListInfo.chatList)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", emptyList())
            }
        } else {
            Pair("error", emptyList())
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", emptyList())
    }
}

fun sendMessage(
    username: String,
    password: String,
    receiver: String,
    message: String
): Pair<String, String> {
    val url = "${Global.url}/syc/sendMessage.php".toHttpUrlOrNull() ?: return Pair(
        "Error",
        "Invalid URL"
    )

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBodyBuilder =
        FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .add("receiver", receiver)
            .add("message", message)

    val formBody = formBodyBuilder.build()

    Log.d("聊天信息获取", url.toString())

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("聊天信息获取", responseBody)
            try {
                val pinUserInfo: WebCommonInfo =
                    Gson().fromJson(responseBody, WebCommonInfo::class.java)
                Pair(pinUserInfo.status, pinUserInfo.message)
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}

fun getMessage(
    username: String,
    password: String,
    receiver: String
): Pair<String, List<ChatRecord>> {
    val url = "${Global.url}/syc/getChatMessage.php".toHttpUrlOrNull()
        ?: return Pair("error", listOf())

    val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    // 创建请求体，包含用户名、密码和接收者
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("receiver", receiver)
        .build()

    Log.d("聊天信息获取", "请求 URL: $url")

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            Log.d("聊天信息获取", "响应: $responseBody")
            try {
                val chatMessageInfo: ChatMessageResponse =
                    Gson().fromJson(responseBody, ChatMessageResponse::class.java)

                if (chatMessageInfo.status == "success") {
                    Pair(chatMessageInfo.status, chatMessageInfo.chatRecords)
                } else {
                    Pair("error", listOf())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("聊天信息解析", "解析错误: ${e.message}")
                Pair("error", listOf())
            }
        } else {
            Log.e("网络请求失败", "响应失败: ${response.message}")
            Pair("error", listOf())
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.e("网络请求失败", "IO异常: ${e.message}")
        Pair("error", listOf())
    }
}

fun sendComment(username: String, password: String, postId: Int, parentCommentId: Int,content: String): Pair<String, String> {
    val url =
        "${Global.url}/syc/comments.php".toHttpUrlOrNull() ?: return Pair("Error", "Invalid URL")

    val client = OkHttpClient()

    // 创建请求体
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password",password)
        .add("postId",postId.toString())
        .add("parentCommentId",parentCommentId.toString())
        .add("content",content)
        .build()

    // 构建请求
    val request = Request.Builder()
        .url(url)
        .post(formBody)
        .build()

    return try {
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseBody = response.body?.string() ?: ""
            try {
                val sendCommentInfo: sendCommentInfo =
                    Gson().fromJson(responseBody, sendCommentInfo::class.java)
                Pair(sendCommentInfo.status, if (sendCommentInfo.status != "success") sendCommentInfo.message else sendCommentInfo.commentId.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Pair("error", "Parsing error")
            }
        } else {
            Pair("error", response.message)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Pair("error", e.message ?: "Unknown error")
    }
}
