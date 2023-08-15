package com.github.jing332.alistandroid.model.alistclient

import com.drake.net.convert.NetConverter
import com.drake.net.exception.ConvertException
import com.drake.net.exception.RequestParamsException
import com.drake.net.exception.ServerResponseException
import com.drake.net.request.kType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okhttp3.Response
import org.json.JSONObject
import java.lang.reflect.Type
import kotlin.reflect.KType

class SerializationConverter : NetConverter {

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        val jsonDecoder = Json {
            explicitNulls = false
            ignoreUnknownKeys = true // 数据类可以不用声明Json的所有字段
            coerceInputValues = true // 如果Json字段是Null则使用数据类字段默认值
        }
    }

    override fun <R> onConvert(succeed: Type, response: Response): R? {
        try {
            return NetConverter.onConvert<R>(succeed, response)
        } catch (e: ConvertException) {
            val code = response.code
            when {
                code in 200..299 -> { // 请求成功
                    if (!response.isSuccessful)
                        throw ConvertException(
                            response,
                            "Response is not successful code=${response.code}"
                        )

                    val bodyString = response.body?.string() ?: return null
                    val json = JSONObject(bodyString)
                    val retCode = json.optInt("code")
                    val retMsg = json.optString("msg")
                    if (retCode != AListClient.CODE_OK)
                        throw AListResponseException(
                            response,
                            message = retMsg,
                            code = retCode
                        )

                    val retData = json.optString("data")

                    val kType = response.request.kType
                        ?: throw ConvertException(response, "Request does not contain KType")
                    return retData.parseBody<R>(kType)
                }

                code in 400..499 -> throw RequestParamsException(
                    response,
                    code.toString()
                ) // 请求参数错误
                code >= 500 -> throw ServerResponseException(response, code.toString()) // 服务器异常错误
                else -> throw ConvertException(response)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <R> String.parseBody(succeed: KType): R? {
        return jsonDecoder.decodeFromString(Json.serializersModule.serializer(succeed), this) as R
    }
}