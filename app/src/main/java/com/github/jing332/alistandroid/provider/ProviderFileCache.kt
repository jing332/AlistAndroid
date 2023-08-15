package com.github.jing332.alistandroid.provider

import android.content.Context
import com.github.jing332.alistandroid.data.appDb
import com.github.jing332.alistandroid.data.dao.ProviderCacheDao
import com.github.jing332.alistandroid.data.entities.ProviderCache
import java.io.File

class ProviderFileCache(val context: Context) {

    private val dao: ProviderCacheDao
        get() = appDb.providerCacheDao

    val cachePath: String by lazy {
        context.externalCacheDir!!.absolutePath + File.separator + "provider"
    }

    init {
        File(cachePath).deleteRecursively()
    }

    fun getFile(path: String): File? {
        File(cachePath).listFiles()?.forEach {
            if (it.internalPath() == path) {
                return it
            }
        }

        return null
    }

    fun putFile(file: File) {
        dao.insert(ProviderCache(path = "", modifier = ""))
    }

    fun newFile(path: String): File {
        return File(cachePath + File.separator + path)
    }


    private fun File.internalPath(): String {
        return this.absolutePath.removePrefix(cachePath)
    }
}