package com.github.jing332.alistandroid.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity("engines")
data class Engine(
    @PrimaryKey
    var code: String = "",
    var header: String = "",
    var name: String = "",
    var note: String = "",
    var type: String = "",
    var url: String = "",
)