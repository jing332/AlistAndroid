package com.github.jing332.alistandroid.model
import kotlinx.serialization.Serializable

import kotlinx.serialization.SerialName


@Serializable
data class AListConfig(
    @SerialName("bleve_dir")
    val bleveDir: String = "", // /storage/emulated/0/Android/data/com.github.jing332.alistandroid.debug/files/data/bleve
    @SerialName("cdn")
    val cdn: String = "",
    @SerialName("database")
    val database: Database = Database(),
    @SerialName("delayed_start")
    val delayedStart: Int = 0, // 0
    @SerialName("force")
    val force: Boolean = false, // false
    @SerialName("jwt_secret")
    val jwtSecret: String = "", // UZK8hzYeR21fMw4h
    @SerialName("log")
    val log: Log = Log(),
    @SerialName("max_connections")
    val maxConnections: Int = 0, // 0
    @SerialName("scheme")
    val scheme: Scheme = Scheme(),
    @SerialName("site_url")
    val siteUrl: String = "",
    @SerialName("temp_dir")
    val tempDir: String = "", // /storage/emulated/0/Android/data/com.github.jing332.alistandroid.debug/files/data/temp
    @SerialName("tls_insecure_skip_verify")
    val tlsInsecureSkipVerify: Boolean = false, // true
    @SerialName("token_expires_in")
    val tokenExpiresIn: Int = 0 // 48
) {
    @Serializable
    data class Database(
        @SerialName("db_file")
        val dbFile: String = "", // /storage/emulated/0/Android/data/com.github.jing332.alistandroid.debug/files/data/data.db
        @SerialName("host")
        val host: String = "",
        @SerialName("name")
        val name: String = "",
        @SerialName("password")
        val password: String = "",
        @SerialName("port")
        val port: Int = 0, // 0
        @SerialName("ssl_mode")
        val sslMode: String = "",
        @SerialName("table_prefix")
        val tablePrefix: String = "", // x_
        @SerialName("type")
        val type: String = "", // sqlite3
        @SerialName("user")
        val user: String = ""
    )

    @Serializable
    data class Log(
        @SerialName("compress")
        val compress: Boolean = false, // false
        @SerialName("enable")
        val enable: Boolean = false, // true
        @SerialName("max_age")
        val maxAge: Int = 0, // 28
        @SerialName("max_backups")
        val maxBackups: Int = 0, // 5
        @SerialName("max_size")
        val maxSize: Int = 0, // 10
        @SerialName("name")
        val name: String = "" // /storage/emulated/0/Android/data/com.github.jing332.alistandroid.debug/files/data/log/log.log
    )

    @Serializable
    data class Scheme(
        @SerialName("address")
        val address: String = "", // 0.0.0.0
        @SerialName("cert_file")
        val certFile: String = "",
        @SerialName("force_https")
        val forceHttps: Boolean = false, // false
        @SerialName("http_port")
        val httpPort: Int = 0, // 5244
        @SerialName("https_port")
        val httpsPort: Int = 0, // -1
        @SerialName("key_file")
        val keyFile: String = "",
        @SerialName("unix_file")
        val unixFile: String = "",
        @SerialName("unix_file_perm")
        val unixFilePerm: String = ""
    )
}