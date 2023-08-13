package com.github.jing332.alistandroid.constant

import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.github.jing332.alistandroid.BuildConfig
import com.github.jing332.alistandroid.app
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@Suppress("DEPRECATION")
object AppConst {
    val yaml = Yaml(configuration = YamlConfiguration(strictMode = false))

    @OptIn(ExperimentalSerializationApi::class)
    val json = Json {
        ignoreUnknownKeys = true
        allowStructuredMapKeys = true
        prettyPrint = true
        isLenient = true
        explicitNulls = false
    }

    val localBroadcast by lazy {
        LocalBroadcastManager.getInstance(app)
    }

    val fileProviderAuthor = BuildConfig.APPLICATION_ID + ".fileprovider"
}