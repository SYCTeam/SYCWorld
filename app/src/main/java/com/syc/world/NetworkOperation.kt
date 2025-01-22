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

data class IpInfo(
    @SerializedName("ip") val ip: String,
    @SerializedName("pro") val province: String,
    @SerializedName("proCode") val provinceCode: String,
    @SerializedName("city") val city: String,
    @SerializedName("cityCode") val cityCode: String,
    @SerializedName("region") val region: String,
    @SerializedName("regionCode") val regionCode: String,
    @SerializedName("addr") val addr: String,
    @SerializedName("regionNames") val regionNames: String,
    @SerializedName("err") val err: String
)

data class SynopsisData(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String
)

data class StepCount(
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
    val url = "https://whois.pconline.com.cn/ipJson.jsp?ip=$ip&json=true"

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
                val responseString = response.body?.string() ?: "无"
                try {
                    Log.d("信息获取", responseString)
                    val ipInfo: IpInfo =
                        Gson().fromJson(responseString, IpInfo::class.java)

                    ipInfo.city.ifEmpty {
                        ipInfo.province
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    "无"
                }
            } else {
                "无"
            }
        } catch (e: TimeoutCancellationException) {
            "无"
        } catch (e: IOException) {
            e.printStackTrace()
            "无"
        }
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

fun modifyStepCount(username: String, password: String,synopsis: String): String {
    val url = "${Global.url}/syc/stepCount.php".toHttpUrlOrNull() ?: return "Error: Invalid URL"

    val client = OkHttpClient()

    // 创建请求体，包含用户名和密码
    val formBody = FormBody.Builder()
        .add("username", username)
        .add("password", password)
        .add("stepCount", synopsis)
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
