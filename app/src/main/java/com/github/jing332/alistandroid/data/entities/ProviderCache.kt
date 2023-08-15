package com.github.jing332.alistandroid.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "provider_caches")
data class ProviderCache(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val path: String,
    val status: Status = Status.PADDING,
    val modifier: String,
    val isDirection: Boolean = false,
) {
    enum class Status {
        PADDING,
        DONE
    }
}