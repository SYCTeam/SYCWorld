package com.syc.world

import android.util.Log
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
import java.io.IOException
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
            Log.d("信息获取", responseBody)
            responseBody
        } else {
            "Error"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        "Error"
    }
}

suspend fun getAddressFromIp(ip: String): String {
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
                    return ipResponse.data.addr + " · " + ipResponse.data.province
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

fun modifySynopsis(username: String, password: String,synopsis: String): String {
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

fun modifyStepCount(username: String, password: String,stepCount: String): String {
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

