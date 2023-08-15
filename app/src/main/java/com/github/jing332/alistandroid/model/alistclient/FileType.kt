package com.github.jing332.alistandroid.model.alistclient

import android.provider.DocumentsContract

enum class FileType(val v: Int, val mime: String) {
    UNKNOWN(0, ""),
    FOLDER(1, DocumentsContract.Document.MIME_TYPE_DIR),

    //    OFFICE(2, "application/document"),
    VIDEO(2, "video/*"),
    AUDIO(3, "audio/*"),
    TEXT(4, "text/*"),
    IMAGE(5, "image/*");
}