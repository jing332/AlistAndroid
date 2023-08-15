package com.github.jing332.alistandroid.model.alistclient.resp

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ListResponse(
    @SerialName("content")
    val content: List<Content> = listOf(),
    @SerialName("provider")
    val provider: String = "", // AliyundriveOpen
    @SerialName("readme")
    val readme: String = "",
    @SerialName("total")
    val total: Int = 0, // 1
    @SerialName("write")
    val write: Boolean = false // true
) {
    @Serializable
    data class Content(
        @SerialName("is_dir")
        val isDir: Boolean = false, // false
        @SerialName("modified")
        val modified: String = "", // 2021-12-28T08:00:20.711Z
        @SerialName("name")
        val name: String = "", // xxx
        @SerialName("sign")
        val sign: String = "",
        @SerialName("size")
        val size: Long = 0, // 218352
        @SerialName("thumb")
        val thumb: String = "",
        @SerialName("type")
        val type: Int = 0 // 0
    )
}