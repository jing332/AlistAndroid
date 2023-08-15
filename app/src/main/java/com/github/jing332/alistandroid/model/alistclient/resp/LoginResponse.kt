package com.github.jing332.alistandroid.model.alistclient.resp

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(val token: String)