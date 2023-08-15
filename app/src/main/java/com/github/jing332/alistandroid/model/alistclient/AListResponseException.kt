package com.github.jing332.alistandroid.model.alistclient

import com.drake.net.exception.HttpResponseException
import okhttp3.Response

data class AListResponseException(
    override val response: Response,
    override val message: String?,
    override val cause: Throwable? = null,
    val code: Int,
) : HttpResponseException(response, message, cause) {
}