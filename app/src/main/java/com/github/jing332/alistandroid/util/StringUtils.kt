package com.github.jing332.alistandroid.util

object StringUtils {
    private fun paramsParseInternal(params: String): HashMap<String, String> {
        val parameters: HashMap<String, String> = hashMapOf()
        if (params.isBlank()) return parameters

        for (param in params.split("&")) {
            val entry = param.split("=".toRegex()).dropLastWhile { it.isEmpty() }
            if (entry.size > 1) {
                parameters[entry[0]] = entry[1]
            } else {
                parameters[entry[0]] = ""
            }
        }
        return parameters
    }

    fun String.paramsParse() = paramsParseInternal(this)
}