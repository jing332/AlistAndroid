package com.github.jing332.alistandroid.model.alistclient

import android.util.Log
import com.drake.net.Net
import com.drake.net.request.BaseRequest
import com.github.jing332.alistandroid.model.alistclient.resp.FileResponse
import com.github.jing332.alistandroid.model.alistclient.resp.ListResponse
import com.github.jing332.alistandroid.model.alistclient.resp.LoginResponse

class AListClient(private val baseUrl: String = "http://192.168.0.116:5244/api") {
    companion object {
        const val TAG = "AListClient"
        const val CODE_OK = 200
    }

    private var token: String = ""

    var username: String = ""
    var password: String = ""

    fun login(username: String = this.username, password: String = this.password): LoginResponse {
        return Net.post("$baseUrl/auth/login") {
            init(checkLogin = false)
            param("Username", username)
            param("Password", password)
        }.execute<LoginResponse>().apply {
            this@AListClient.token = token
        }
    }

    fun list(
        path: String = "", password: String = "",
        page: Int = 1, perPage: Int = 0, refresh: Boolean = false
    ): ListResponse {
        return Net.post("$baseUrl/fs/list") {
            init()
            json(
                "path" to path,
                "password" to password,
                "page" to page,
                "per_page" to perPage,
                "refresh" to refresh
            )
        }.execute<ListResponse>()
    }

    fun get(path: String, password: String = ""): FileResponse {
        return Net.post("$baseUrl/fs/get") {
            init()
            json(
                "path" to path,
                "password" to password,
            )
        }.execute<FileResponse>()
    }

    fun put(path: String) {
        Net.put("$baseUrl/fs/put") {
            init()
            json(
                "path" to path,
            )
        }.execute<String>()
    }

    fun mkdir(path: String): String {
        return Net.post("$baseUrl/fs/mkdir") {
            init()
            json(
                "path" to path,
            )
        }.execute<String>()
    }

    fun rename(path: String, name: String) {
        Net.post("$baseUrl/fs/rename") {
            init()
            json(
                "path" to path,
                "name" to name
            )
        }.execute<String>()
    }

    private fun BaseRequest.init(checkLogin: Boolean = true) {
        converter = SerializationConverter()

        if (checkLogin && token.isEmpty()) {
            Log.i(TAG, "token is empty, try login...")
            val l = login()
        }

        this.addHeader("Authorization", token)
    }
}