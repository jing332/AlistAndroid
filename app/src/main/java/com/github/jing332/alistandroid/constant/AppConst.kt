package com.github.jing332.alistandroid.constant

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

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
}