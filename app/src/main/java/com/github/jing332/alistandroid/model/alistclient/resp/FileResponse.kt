package com.github.jing332.alistandroid.model.alistclient.resp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FileResponse(
    @SerialName("is_dir")
    val isDir: Boolean = false, // false
    @SerialName("modified")
    val modified: String = "", // 2021-12-28T08:00:20.711Z
    @SerialName("name")
    val name: String = "", // xxx
    @SerialName("provider")
    val provider: String = "", // AliyundriveOpen
    @SerialName("raw_url")
    val rawUrl: String = "", // https://cn-beijing-data.aliyundrive.net/xxx
    @SerialName("readme")
    val readme: String = "",
//        @SerialName("related")
//        val related: Any? = Any(), // null
    @SerialName("sign")
    val sign: String = "",
    @SerialName("size")
    val size: Long = 0, // 218352
    @SerialName("thumb")
    val thumb: String = "",
    @SerialName("type")
    val type: Int = 0 // 0
)