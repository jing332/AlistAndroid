package com.github.jing332.alistandroid.provider

import android.content.Context
import com.drake.net.Net
import com.github.jing332.alistandroid.model.alistclient.AListClient
import com.github.jing332.alistandroid.model.alistclient.resp.ListResponse
import okhttp3.Response
import java.io.File

class ProviderManager(val context: Context) {
    val mCache by lazy { ProviderFileCache(context) }
    val client by lazy { AListClient() }

    fun setUserInfo(username: String, password: String) {
        client.username = username
        client.password = password
    }

    fun getFile(path: String): File {
        val res = client.get(path)

        val resp = Net.get(res.rawUrl) {
        }.execute<Response>()
        if (!resp.isSuccessful)
            throw Exception("download file failed")

        val file = mCache.newFile(path)
        file.absoluteFile.mkdirs()
        file.delete()
        file.createNewFile()

        val fos = file.outputStream()
        resp.body?.byteStream()?.use { ins ->
            ins.buffered().use { buffered ->
                val buffer = ByteArray(4096)
                while (true) {
                    val read = buffered.read(buffer)
                    if (read == -1) break
                    fos.write(buffer, 0, read)
                }
            }
        }
        fos.close()

        return file
    }

    private var currentFiles: List<ListResponse.Content> = emptyList()

    fun list(docId: String): List<ListResponse.Content> {
        val res = client.list(docId)
        return res.content.apply {
            currentFiles = this
        }
    }

    fun rename(path: String, name: String) {
        client.rename(path, name)
    }

    fun isChildDocument(parentDocId: String, docId: String): Boolean {
//        return currentFiles.any { it. == docId }
        return false
    }

}